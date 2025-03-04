package com.smparkworld.discord.feature.bee.commands

import com.smparkworld.discord.common.base.StringCode
import com.smparkworld.discord.common.base.StringsParser.getString
import com.smparkworld.discord.common.extensions.checkVoiceChannelValidation
import com.smparkworld.discord.common.extensions.randomValueOf
import com.smparkworld.discord.common.extensions.requireGuild
import com.smparkworld.discord.common.extensions.sendNoticeEmbedsMessage
import com.smparkworld.discord.common.framework.CommandHandler
import com.smparkworld.discord.core.media.MusicManagerMediator
import com.smparkworld.discord.domain.GetVoiceChannelByEventAuthorUseCase
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class BeeMusicPlayLeaveCommandHandler(
    private val getVoiceChannelByEventAuthor: GetVoiceChannelByEventAuthorUseCase,
) : CommandHandler() {

    override suspend fun handle(command: String, event: SlashCommandInteractionEvent) {
        checkVoiceChannelValidation(event, result = getVoiceChannelByEventAuthor(event)) { _ ->

            MusicManagerMediator
                .removeMusicManager(event.requireGuild().idLong)

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