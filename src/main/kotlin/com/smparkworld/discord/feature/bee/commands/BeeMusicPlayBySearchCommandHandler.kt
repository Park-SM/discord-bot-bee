package com.smparkworld.discord.feature.bee.commands

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.smparkworld.discord.common.base.StringCode
import com.smparkworld.discord.common.base.StringsParser.getString
import com.smparkworld.discord.common.extensions.checkVoiceChannelValidation
import com.smparkworld.discord.common.extensions.sendEmbedsMessage
import com.smparkworld.discord.common.extensions.sendNoticeEmbedsMessage
import com.smparkworld.discord.common.extensions.sendUnknownExceptionEmbedsMessage
import com.smparkworld.discord.common.framework.CommandHandler
import com.smparkworld.discord.core.media.GuildMusicManager
import com.smparkworld.discord.core.media.MusicManagerMediator
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

            val musicManager = MusicManagerMediator.obtainGuildTracker(guild.idLong)
            val audioManager = guild.audioManager.apply { sendingHandler = musicManager.getSendHandler() }

            val isValidYoutubeLink = input.startsWith(YOUTUBE_URL_PREFIX_1, ignoreCase = true)
                    || input.startsWith(YOUTUBE_URL_PREFIX_2, ignoreCase = true)
                    || input.startsWith(YOUTUBE_URL_PREFIX_3, ignoreCase = true)

            val query = if (isValidYoutubeLink) input else "${SEARCH_PREFIX}:${input}"

            MusicManagerMediator.loadItem(query, object : AudioLoadResultHandler {
                override fun trackLoaded(track: AudioTrack) {
                    onNewTrackReceived(
                        track = track,
                        manager = musicManager,
                        audioManager = audioManager,
                        voiceChannel = voiceChannel,
                        event = event
                    )
                }
                override fun playlistLoaded(playlist: AudioPlaylist) {
                    onNewTrackReceived(
                        track = playlist.tracks.firstOrNull() ?: return,
                        manager = musicManager,
                        audioManager = audioManager,
                        voiceChannel = voiceChannel,
                        event = event
                    )
                }
                override fun noMatches() {
                    event.sendNoticeEmbedsMessage(getString(StringCode.BEE_CMD_MUSIC_PLAY_NOT_FOUND))
                }
                override fun loadFailed(exception: FriendlyException) {
                    exception.printStackTrace()
                    event.sendUnknownExceptionEmbedsMessage()
                }
            })
        }
    }

    private fun onNewTrackReceived(
        track: AudioTrack,
        manager: GuildMusicManager,
        audioManager: AudioManager,
        voiceChannel: VoiceChannel,
        event: SlashCommandInteractionEvent
    ) {
        connectBeeBotToEventAuthorVoiceChannel(audioManager, voiceChannel)
        manager.queue(track)

        val playlistTitles = manager.getPlaylist()
            .mapIndexed { idx, e -> "> ${idx + 1}. `${e.info.title}`" }
            .joinToString("\n")

        if (playlistTitles.isNotEmpty()) {
            val currentAudioTrack = manager.currentTrack?.info?.title
                ?: getString(StringCode.BEE_CMD_MUSIC_PLAY_CURRENT_TITLE_EXCEPTION)

            val message = EmbedBuilder()
                .setTitle(getString(StringCode.BEE_CMD_MUSIC_PLAY_TITLE))
                .setDescription(getString(StringCode.BEE_CMD_MUSIC_PLAY_PUT_PLAYLIST_DESC))
                .setUrl(track.info.uri)
                .addField("> `${track.info.title}`", "", false)
                .addField(getString(StringCode.BEE_CMD_MUSIC_PLAY_CURRENT_TITLE), "> `${currentAudioTrack}`", false)
                .addField(getString(StringCode.BEE_CMD_MUSIC_PLAY_PLAYLIST), playlistTitles, false)
                .build()
            event.sendEmbedsMessage(message)
        } else {
            val message = EmbedBuilder()
                .setTitle(getString(StringCode.BEE_CMD_MUSIC_PLAY_TITLE))
                .setDescription(getString(StringCode.BEE_CMD_MUSIC_PLAY_DESC))
                .setUrl(track.info.uri)
                .addField("> `${track.info.title}`", "", false)
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