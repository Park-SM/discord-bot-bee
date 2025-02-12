package com.smparkworld.discord.bot.bee.commands

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.smparkworld.discord.base.StringCode
import com.smparkworld.discord.base.StringsParser.getString
import com.smparkworld.discord.bot.CommandHandler
import com.smparkworld.discord.bot.bee.commands.player.GuildMusicManager
import com.smparkworld.discord.bot.bee.commands.player.MusicManagerMediator
import com.smparkworld.discord.usecase.GetVoiceChannelByEventAuthorUseCase
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.managers.AudioManager

class BeeMusicPlayBySearchCommandHandler(
    private val musicManagerMediator: MusicManagerMediator,
    private val getVoiceChannelByEventAuthor: GetVoiceChannelByEventAuthorUseCase,
) : CommandHandler() {

    override fun handle(command: String, event: SlashCommandInteractionEvent) {
        checkVoiceChannelValidation(event) { voiceChannel ->

            val input = event.getOption(getString(StringCode.MUSIC_KEYWORD))?.asString
            if (input.isNullOrBlank()) {
                val message = EmbedBuilder()
                    .setDescription(getString(StringCode.BEE_CMD_MUSIC_PLAY_INPUT_EMPTY))
                    .build()
                event.replyEmbeds(message).queue()
                return@checkVoiceChannelValidation
            }

            val guild = event.guild
            if (guild == null) {
                val message = EmbedBuilder()
                    .setDescription(getString(StringCode.UNKNOWN_EXCEPTION))
                    .build()
                event.replyEmbeds(message).queue()
                return@checkVoiceChannelValidation
            }

            val musicManager = musicManagerMediator.obtainGuildTracker(guild.idLong)
            val audioManager = guild.audioManager.apply { sendingHandler = musicManager.sendHandler }

            val isValidYoutubeLink = input.startsWith(YOUTUBE_URL_PREFIX_1, ignoreCase = true)
                    || input.startsWith(YOUTUBE_URL_PREFIX_2, ignoreCase = true)
                    || input.startsWith(YOUTUBE_URL_PREFIX_3, ignoreCase = true)

            val query = if (isValidYoutubeLink) input else "${SEARCH_PREFIX}:${input}"

            musicManagerMediator.loadItem(query, object : AudioLoadResultHandler {
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
                    val message = EmbedBuilder()
                        .setDescription(getString(StringCode.BEE_CMD_MUSIC_PLAY_NOT_FOUND))
                        .build()
                    event.replyEmbeds(message).queue()
                }
                override fun loadFailed(exception: FriendlyException) {
                    exception.printStackTrace()
                    val message = EmbedBuilder()
                        .setDescription(getString(StringCode.UNKNOWN_EXCEPTION))
                        .build()
                    event.replyEmbeds(message).queue()
                }
            })
        }
    }

    private fun checkVoiceChannelValidation(
        event: SlashCommandInteractionEvent,
        perform: (channel: VoiceChannel) -> Unit
    ) {
        when (val result = getVoiceChannelByEventAuthor(event)) {
            is GetVoiceChannelByEventAuthorUseCase.Result.Success -> {
                perform.invoke(result.voiceChannel)
            }
            is GetVoiceChannelByEventAuthorUseCase.Result.NotInVoiceChannel -> {
                val message = EmbedBuilder()
                    .setDescription(getString(StringCode.ABSENT_COMMAND_AUTHOR))
                    .build()
                event.replyEmbeds(message).queue()
            }
            is GetVoiceChannelByEventAuthorUseCase.Result.Error -> {
                val message = EmbedBuilder()
                    .setDescription(getString(StringCode.UNKNOWN_EXCEPTION))
                    .build()
                event.replyEmbeds(message).queue()
            }
        }
    }

    private fun onNewTrackReceived(
        track: AudioTrack,
        manager: GuildMusicManager,
        audioManager: AudioManager,
        voiceChannel: VoiceChannel,
        event: SlashCommandInteractionEvent
    ) {
        connectBeeBotToVoiceChannelIfAbsent(audioManager, voiceChannel)
        manager.scheduler.queue(track)

        val playlistTitles = manager.scheduler
            .getPlaylist()
            .mapIndexed { idx, e -> "> ${idx + 1}. `${e.info.title}`" }
            .joinToString("\n")

        if (playlistTitles.isNotEmpty()) {
            val currentAudioTrack = manager.scheduler.currentAudioTrack?.info?.title
                ?: getString(StringCode.BEE_CMD_MUSIC_PLAY_CURRENT_TITLE_EXCEPTION)

            val message = EmbedBuilder()
                .setTitle(getString(StringCode.BEE_CMD_MUSIC_PLAY_TITLE))
                .setDescription(getString(StringCode.BEE_CMD_MUSIC_PLAY_PUT_PLAYLIST_DESC))
                .setUrl(track.info.uri)
                .addField("> `${track.info.title}`", "", false)
                .addField(getString(StringCode.BEE_CMD_MUSIC_PLAY_CURRENT_TITLE), "> `${currentAudioTrack}`", false)
                .addField(getString(StringCode.BEE_CMD_MUSIC_PLAY_PLAYLIST), playlistTitles, false)
                .build()
            event.replyEmbeds(message).queue()
        } else {
            val message = EmbedBuilder()
                .setTitle(getString(StringCode.BEE_CMD_MUSIC_PLAY_TITLE))
                .setDescription(getString(StringCode.BEE_CMD_MUSIC_PLAY_DESC))
                .setUrl(track.info.uri)
                .addField("> `${track.info.title}`", "", false)
                .build()
            event.replyEmbeds(message).queue()
        }
    }

    private fun connectBeeBotToVoiceChannelIfAbsent(
        audioManager: AudioManager,
        voiceChannel: VoiceChannel,
    ) {
        if (!audioManager.isConnected) {
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