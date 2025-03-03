package com.smparkworld.discord.feature.bee.commands

import com.smparkworld.discord.common.base.StringCode
import com.smparkworld.discord.common.base.StringsParser.getString
import com.smparkworld.discord.common.extensions.*
import com.smparkworld.discord.common.framework.CommandHandler
import com.smparkworld.discord.core.media.GuildMusicManager
import com.smparkworld.discord.core.media.MusicManagerMediator
import com.smparkworld.discord.core.media.model.Track
import com.smparkworld.discord.core.media.model.TrackLoadingResult
import com.smparkworld.discord.domain.GetSingleMessagePerChannelUseCase
import com.smparkworld.discord.domain.GetVoiceChannelByEventAuthorUseCase
import com.smparkworld.discord.domain.SaveSingleMessagePerChannelUseCase
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.managers.AudioManager
import kotlin.coroutines.coroutineContext

class BeeMusicPlayBySearchCommandHandler(
    private val getVoiceChannelByEventAuthor: GetVoiceChannelByEventAuthorUseCase,
    private val saveSingleMessagePerChannelUseCase: SaveSingleMessagePerChannelUseCase,
    private val getSingleMessagePerChannelUseCase: GetSingleMessagePerChannelUseCase
) : CommandHandler() {

    override suspend fun handle(command: String, event: SlashCommandInteractionEvent) {
        checkVoiceChannelValidation(event, result = getVoiceChannelByEventAuthor(event)) { voiceChannel ->

            val input = event.getOption(getString(StringCode.MUSIC_KEYWORD))?.asString
            if (input.isNullOrBlank()) {
                event.sendNoticeEmbedsMessage(getString(StringCode.BEE_CMD_MUSIC_PLAY_INPUT_EMPTY))
                return@checkVoiceChannelValidation
            }

            val musicManager = event.guild?.idLong?.let(MusicManagerMediator::obtainGuildMusicManager)
            if (musicManager == null) {
                event.sendUnknownExceptionEmbedsMessage()
                return@checkVoiceChannelValidation
            }

            when (val result = musicManager.load(query = input)) {
                is TrackLoadingResult.SuccessTrackLoading -> onNewTrackReceived(
                    track = result.track,
                    manager = musicManager,
                    voiceChannel = voiceChannel,
                    event = event
                )
                is TrackLoadingResult.SuccessTracksLoading -> onNewTrackReceived(
                    track = result.tracks.firstOrNull() ?: return@checkVoiceChannelValidation,
                    manager = musicManager,
                    voiceChannel = voiceChannel,
                    event = event
                )
                is TrackLoadingResult.NoMatches -> {
                    event.sendNoticeEmbedsMessage(getString(StringCode.BEE_CMD_MUSIC_PLAY_NOT_FOUND))
                }
                is TrackLoadingResult.UnknownException -> {
                    event.sendUnknownExceptionEmbedsMessage()
                }
                is TrackLoadingResult.Error -> {
                    result.exception.printStackTrace()
                    event.sendUnknownExceptionEmbedsMessage()
                }
            }
        }
    }

    private suspend fun onNewTrackReceived(
        track: Track,
        manager: GuildMusicManager,
        voiceChannel: VoiceChannel,
        event: SlashCommandInteractionEvent
    ) {
        val audioManager = manager.getAudioManager()
        if (audioManager == null) {
            event.sendUnknownExceptionEmbedsMessage()
            return
        }
        connectBeeBotToEventAuthorVoiceChannel(audioManager, voiceChannel)
        manager.queue(track)

        manager.setOnNextTrackLoaded {
            commandHandlerScope.launch {
                sendCurrentTrackDashboardMessage(event, manager, track)
            }
        }
        sendCurrentTrackDashboardMessage(event, manager, track)
    }

    private fun connectBeeBotToEventAuthorVoiceChannel(
        audioManager: AudioManager,
        voiceChannel: VoiceChannel,
    ) {
        val oldChannel = audioManager.connectedChannel as? VoiceChannel
        if (oldChannel != voiceChannel) {
            audioManager.openAudioConnection(voiceChannel)
        }
    }

    private suspend fun sendCurrentTrackDashboardMessage(event: SlashCommandInteractionEvent, manager: GuildMusicManager, track: Track) {
        val playlistTitles = manager.getPlaylist()
            .mapIndexed { idx, e -> "> ${idx + 1}. `${e.info.title}`" }
            .joinToString("\n")
            .takeIf(String::isNotBlank)

        val currentAudioTrack = manager.currentTrack?.title
            ?: getString(StringCode.BEE_CMD_MUSIC_PLAY_CURRENT_TITLE_EXCEPTION)

        val message = EmbedBuilder()
            .setTitle(getString(StringCode.BEE_CMD_MUSIC_PLAY_TITLE))
            .setThumbnail(track.thumbnailUrl)
            .setUrl(track.uri)
            .setDescription(getString(StringCode.BEE_CMD_MUSIC_PLAY_CURRENT_TITLE))
            .addField("> `${currentAudioTrack}`", "", false)
            .addFieldIfNotNull(getString(StringCode.BEE_CMD_MUSIC_PLAY_PLAYLIST), playlistTitles, false)
            .build()
        upsertMessageByLimit(event, message)
    }

    private suspend fun upsertMessageByLimit(
        event: SlashCommandInteractionEvent,
        message: MessageEmbed,
        limit: Int = 3
    ) {
        val textChannel = (event.channel as? TextChannel) ?: return
        val prevMessage = getSingleMessagePerChannelUseCase(event)

        val prevMessageInLimit = textChannel.history.retrievePast(limit).execute()
            .firstOrNull { it.idLong == prevMessage?.idLong }

        if (prevMessageInLimit == null) {
            saveSingleMessagePerChannelUseCase(event, message = null)
            prevMessage?.delete()
        }
        if (prevMessageInLimit != null) {
            prevMessageInLimit.editMessageEmbeds(message).queue()
        } else {
            event.sendEmbedsMessageAndReturn(message).also {
                saveSingleMessagePerChannelUseCase(event, it)
            }
        }
    }
}