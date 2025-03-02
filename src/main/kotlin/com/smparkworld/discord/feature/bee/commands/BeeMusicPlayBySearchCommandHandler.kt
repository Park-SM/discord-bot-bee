package com.smparkworld.discord.feature.bee.commands

import com.smparkworld.discord.common.base.StringCode
import com.smparkworld.discord.common.base.StringsParser.getString
import com.smparkworld.discord.common.extensions.checkVoiceChannelValidation
import com.smparkworld.discord.common.extensions.sendEmbedsMessage
import com.smparkworld.discord.common.extensions.sendNoticeEmbedsMessage
import com.smparkworld.discord.common.extensions.sendUnknownExceptionEmbedsMessage
import com.smparkworld.discord.common.framework.CommandHandler
import com.smparkworld.discord.core.media.GuildMusicManager
import com.smparkworld.discord.core.media.MusicManagerMediator
import com.smparkworld.discord.core.media.MusicResultListener
import com.smparkworld.discord.core.media.model.LoadingFailureReason
import com.smparkworld.discord.core.media.model.LoadingResult
import com.smparkworld.discord.core.media.model.Track
import com.smparkworld.discord.domain.GetVoiceChannelByEventAuthorUseCase
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.managers.AudioManager

class BeeMusicPlayBySearchCommandHandler(
    private val getVoiceChannelByEventAuthor: GetVoiceChannelByEventAuthorUseCase,
) : CommandHandler() {

    override fun handle(command: String, event: SlashCommandInteractionEvent) {
        checkVoiceChannelValidation(event, result = getVoiceChannelByEventAuthor(event)) { voiceChannel ->

            val input = event.getOption(getString(StringCode.MUSIC_KEYWORD))?.asString
            if (input.isNullOrBlank()) {
                event.sendNoticeEmbedsMessage(getString(StringCode.BEE_CMD_MUSIC_PLAY_INPUT_EMPTY))
                return@checkVoiceChannelValidation
            }

            val guild = event.guild
            if (guild == null) {
                event.sendUnknownExceptionEmbedsMessage()
                return@checkVoiceChannelValidation
            }

            val musicManager = MusicManagerMediator.obtainGuildMusicManager(guild.idLong)

            val isValidYoutubeLink = input.startsWith(YOUTUBE_URL_PREFIX_1, ignoreCase = true)
                    || input.startsWith(YOUTUBE_URL_PREFIX_2, ignoreCase = true)
                    || input.startsWith(YOUTUBE_URL_PREFIX_3, ignoreCase = true)

            val query = if (isValidYoutubeLink) input else "${SEARCH_PREFIX}:${input}"

            val listener = object : MusicResultListener {
                override fun onLoadSuccess(result: LoadingResult) {
                    when (result) {
                        is LoadingResult.OnTrackLoaded -> {
                            onNewTrackReceived(
                                track = result.track,
                                manager = musicManager,
                                voiceChannel = voiceChannel,
                                event = event
                            )
                        }
                        is LoadingResult.OnTracksLoaded -> {
                            onNewTrackReceived(
                                track = result.tracks.firstOrNull() ?: return,
                                manager = musicManager,
                                voiceChannel = voiceChannel,
                                event = event
                            )
                        }
                    }
                }
                override fun onLoadFailure(reason: LoadingFailureReason) {
                    when (reason) {
                        is LoadingFailureReason.NoMatches -> {
                            event.sendNoticeEmbedsMessage(getString(StringCode.BEE_CMD_MUSIC_PLAY_NOT_FOUND))
                        }
                        is LoadingFailureReason.Unknown -> {
                            event.sendUnknownExceptionEmbedsMessage()
                        }
                        is LoadingFailureReason.Error -> {
                            reason.exception.printStackTrace()
                            event.sendUnknownExceptionEmbedsMessage()
                        }
                    }
                }
            }
            musicManager.load(query, listener)
        }
    }

    private fun onNewTrackReceived(
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

        val playlistTitles = manager.getPlaylist()
            .mapIndexed { idx, e -> "> ${idx + 1}. `${e.info.title}`" }
            .joinToString("\n")

        if (playlistTitles.isNotEmpty()) {
            val currentAudioTrack = manager.currentTrack?.title
                ?: getString(StringCode.BEE_CMD_MUSIC_PLAY_CURRENT_TITLE_EXCEPTION)

            val message = EmbedBuilder()
                .setTitle(getString(StringCode.BEE_CMD_MUSIC_PLAY_TITLE))
                .setDescription(getString(StringCode.BEE_CMD_MUSIC_PLAY_PUT_PLAYLIST_DESC))
                .setUrl(track.uri)
                .addField("> `${track.title}`", "", false)
                .addField(getString(StringCode.BEE_CMD_MUSIC_PLAY_CURRENT_TITLE), "> `${currentAudioTrack}`", false)
                .addField(getString(StringCode.BEE_CMD_MUSIC_PLAY_PLAYLIST), playlistTitles, false)
                .build()
            event.sendEmbedsMessage(message)
        } else {
            val message = EmbedBuilder()
                .setTitle(getString(StringCode.BEE_CMD_MUSIC_PLAY_TITLE))
                .setDescription(getString(StringCode.BEE_CMD_MUSIC_PLAY_DESC))
                .setUrl(track.uri)
                .addField("> `${track.title}`", "", false)
                .build()
            event.sendEmbedsMessage(message)
        }
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

    companion object {
        private const val SEARCH_PREFIX = "ytsearch"
        private const val YOUTUBE_URL_PREFIX_1 = "https://youtube.com/"
        private const val YOUTUBE_URL_PREFIX_2 = "https://www.youtube.com/"
        private const val YOUTUBE_URL_PREFIX_3 = "https://music.youtube.com/"
    }
}