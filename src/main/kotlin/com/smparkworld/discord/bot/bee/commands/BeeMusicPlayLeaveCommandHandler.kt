package com.smparkworld.discord.bot.bee.commands

import com.smparkworld.discord.base.StringCode
import com.smparkworld.discord.base.StringsParser.getString
import com.smparkworld.discord.bot.CommandHandler
import com.smparkworld.discord.bot.bee.commands.player.MusicManagerMediator
import com.smparkworld.discord.extensions.checkVoiceChannelValidation
import com.smparkworld.discord.extensions.randomValueOf
import com.smparkworld.discord.extensions.requireGuild
import com.smparkworld.discord.extensions.sendNoticeEmbedsMessage
import com.smparkworld.discord.usecase.GetVoiceChannelByEventAuthorUseCase
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class BeeMusicPlayLeaveCommandHandler(
    private val musicManagerMediator: MusicManagerMediator,
    private val getVoiceChannelByEventAuthor: GetVoiceChannelByEventAuthorUseCase,
) : CommandHandler() {

    override fun handle(command: String, event: SlashCommandInteractionEvent) {
        checkVoiceChannelValidation(event, result = getVoiceChannelByEventAuthor(event)) { _ ->

            musicManagerMediator.obtainGuildTracker(event.requireGuild().idLong)
                .scheduler
                .clearQueue()

            event.requireGuild()
                .audioManager
                .closeAudioConnection()

            val message = randomValueOf(
                getString(StringCode.BEE_CMD_MUSIC_PLAY_LEAVE_MSG_1),
                getString(StringCode.BEE_CMD_MUSIC_PLAY_LEAVE_MSG_2),
                getString(StringCode.BEE_CMD_MUSIC_PLAY_LEAVE_MSG_3)
            )
            event.sendNoticeEmbedsMessage(message)
        }
    }
}