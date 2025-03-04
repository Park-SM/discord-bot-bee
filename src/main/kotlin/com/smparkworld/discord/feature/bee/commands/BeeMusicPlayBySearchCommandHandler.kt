package com.smparkworld.discord.feature.bee.commands

import com.smparkworld.discord.common.base.ButtonID
import com.smparkworld.discord.common.base.InputID
import com.smparkworld.discord.common.base.ModalID
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
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.text.TextInput
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle
import net.dv8tion.jda.api.interactions.modals.Modal
import net.dv8tion.jda.api.managers.AudioManager

class BeeMusicPlayBySearchCommandHandler(
    private val getVoiceChannelByEventAuthor: GetVoiceChannelByEventAuthorUseCase,
    private val saveSingleMessagePerChannelUseCase: SaveSingleMessagePerChannelUseCase,
    private val getSingleMessagePerChannelUseCase: GetSingleMessagePerChannelUseCase
) : CommandHandler() {

    override suspend fun handle(command: String, event: SlashCommandInteractionEvent) {
        checkVoiceChannelValidation(event, result = getVoiceChannelByEventAuthor(event)) {

            val prevMessage = getSingleMessagePerChannelUseCase(event)
            if (prevMessage != null) {
                event.sendNoticeEmbedsMessage(getString(StringCode.BEE_CMD_MUSIC_PLAY_DESC_ALREADY), ephemeral = true, deleteAfter = 5_000L)
                return@checkVoiceChannelValidation
            }

            val musicManager = event.guild?.idLong?.let(MusicManagerMediator::obtainGuildMusicManager)
            if (musicManager == null) {
                event.sendUnknownExceptionEmbedsMessage()
                return@checkVoiceChannelValidation
            }
            sendCurrentTrackDashboardMessage(event, musicManager, isInit = true)
        }
    }

    override fun handleInteractionByButton(event: ButtonInteractionEvent) {
        val musicManager = event.guild?.idLong?.let(MusicManagerMediator::obtainGuildMusicManager)
        if (musicManager == null) {
            event.sendUnknownExceptionEmbedsMessage()
            return
        }
        when (event.componentId) {
            ButtonID.SEARCH_MUSIC -> {
                val modal = Modal.create(ModalID.SEARCH_MUSIC, getString(StringCode.MODAL_TITLE_SEARCH_MUSIC))
                    .addActionRow(
                        TextInput.create(InputID.SEARCH_MUSIC, getString(StringCode.INPUT_TITLE_SEARCH_MUSIC_LABEL), TextInputStyle.SHORT)
                            .setPlaceholder(getString(StringCode.INPUT_TITLE_SEARCH_MUSIC_HINT))
                            .setRequired(true)
                            .build()
                    )
                    .build()
                event.replyModal(modal).queue()
            }
            ButtonID.PAUSE_OR_RESUME_MUSIC -> {
                if (musicManager.isPaused) {
                    musicManager.resumeTrack()
                } else {
                    musicManager.pauseTrack()
                }
                event.sendDeferReply()
            }
            ButtonID.SKIP_MUSIC -> {
                musicManager.skipTrack()
                commandHandlerScope.launch {
                    sendCurrentTrackDashboardMessage(event, musicManager, withDeferReply = true)
                }
            }
            ButtonID.HISTORY_MUSIC -> {
                val historyTitles = musicManager.getHistory()
                    .mapIndexed { idx, e -> "> ${idx + 1}. [${e.title}](${e.uri})" }
                    .joinToString("\n")
                    .takeIf(String::isNotBlank)

                val message = EmbedBuilder()
                    .setTitle(getString(StringCode.BEE_CMD_MUSIC_PLAY_HISTORY))
                    .setDescription(historyTitles)
                    .build()
                event.sendEmbedsMessage(message, ephemeral = true, deleteAfter = 10_000L)
            }
        }
    }

    override fun handleInteractionByModal(event: ModalInteractionEvent) {
        val musicManager = event.guild?.idLong?.let(MusicManagerMediator::obtainGuildMusicManager)
        if (musicManager == null) {
            event.sendUnknownExceptionEmbedsMessage()
            return
        }
        commandHandlerScope.launch {
            when (event.modalId) {
                ModalID.SEARCH_MUSIC -> checkVoiceChannelValidation(event, result = getVoiceChannelByEventAuthor(event)) { voiceChannel ->

                    val input = event.getValue(InputID.SEARCH_MUSIC)?.asString ?: return@checkVoiceChannelValidation

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
        }
    }

    private suspend fun onNewTrackReceived(
        track: Track,
        manager: GuildMusicManager,
        voiceChannel: VoiceChannel,
        event: IReplyCallback
    ) {
        val audioManager = manager.getAudioManager()
        if (audioManager == null) {
            event.sendUnknownExceptionEmbedsMessage()
            return
        }
        connectBeeBotToEventAuthorVoiceChannel(audioManager, voiceChannel)
        manager.queue(track)

        manager.setOnNextTrackLoaded { bySkip ->
            if (bySkip) return@setOnNextTrackLoaded

            commandHandlerScope.launch {
                sendCurrentTrackDashboardMessage(event, manager, withDeferReply = false)
            }
        }
        sendCurrentTrackDashboardMessage(event, manager, withDeferReply = true)
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

    private suspend fun sendCurrentTrackDashboardMessage(
        event: IReplyCallback,
        manager: GuildMusicManager,
        isInit: Boolean = false,
        withDeferReply: Boolean = false
    ) {
        val currentTrack = manager.currentTrack

        val message = if (currentTrack != null) {

            val playlistTitles = manager.getPlaylist()
                .mapIndexed { idx, e -> "> ${idx + 1}. `${e.title}`" }
                .joinToString("\n")
                .takeIf(String::isNotBlank)

            EmbedBuilder()
                .setTitle(getString(StringCode.BEE_CMD_MUSIC_PLAY_TITLE))
                .setThumbnail(currentTrack.thumbnailUrl)
                .setUrl(currentTrack.uri)
                .addField(getString(StringCode.BEE_CMD_MUSIC_PLAY_CURRENT_TITLE), "> `${currentTrack.title}`", false)
                .addFieldIfNotNull(getString(StringCode.BEE_CMD_MUSIC_PLAY_PLAYLIST), playlistTitles, false)
                .build()
        } else {
            EmbedBuilder()
                .setTitle(getString(StringCode.BEE_CMD_MUSIC_PLAY_TITLE))
                .addField(getString(if (isInit) StringCode.BEE_CMD_MUSIC_PLAY_DESC_INIT else StringCode.BEE_CMD_MUSIC_PLAY_DESC_EMPTY), "", false)
                .addField(getString(StringCode.BEE_CMD_MUSIC_PLAY_HELP_1_TITLE), getString(StringCode.BEE_CMD_MUSIC_PLAY_HELP_1_DESC), false)
                .addField(getString(StringCode.BEE_CMD_MUSIC_PLAY_HELP_2_TITLE), getString(StringCode.BEE_CMD_MUSIC_PLAY_HELP_2_DESC), false)
                .build()
        }
        upsertMessageByLimit(event, message, withDeferReply)
    }

    private suspend fun upsertMessageByLimit(
        event: IReplyCallback,
        message: MessageEmbed,
        withDeferReply: Boolean,
        limit: Int = 3
    ) {
        val textChannel = (event.channel as? TextChannel) ?: return
        val prevMessage = getSingleMessagePerChannelUseCase(event)

        val prevMessageInLimit = textChannel.history.retrievePast(limit).execute()
            .firstOrNull { it.idLong == prevMessage?.idLong }

        if (prevMessageInLimit == null) {
            saveSingleMessagePerChannelUseCase(event, message = null)
            prevMessage?.delete()?.queue()
        }
        if (prevMessageInLimit != null) {
            if (withDeferReply) {
                event.sendDeferReply()
            }
            prevMessageInLimit.editMessageEmbeds(message).queue()
        } else {
            val buttons = listOfNotNull(
                Button.success(ButtonID.SEARCH_MUSIC, getString(StringCode.BUTTON_NAME_SEARCH_MUSIC)),
                Button.primary(ButtonID.PAUSE_OR_RESUME_MUSIC, getString(StringCode.BUTTON_NAME_PAUSE_OR_RESUME_MUSIC)),
                Button.primary(ButtonID.SKIP_MUSIC, getString(StringCode.BUTTON_NAME_SKIP_MUSIC)),
                Button.primary(ButtonID.HISTORY_MUSIC, getString(StringCode.BUTTON_NAME_HISTORY_MUSIC)),
            )
            event.sendEmbedsMessageAndReturn(message, buttons).also {
                saveSingleMessagePerChannelUseCase(event, it)
            }
        }
    }
}