package com.smparkworld.discord.bot.bee.commands

import com.smparkworld.discord.base.StringCode
import com.smparkworld.discord.base.StringsParser.getString
import com.smparkworld.discord.bot.CommandHandler
import com.smparkworld.discord.bot.bee.commands.player.MusicManagerMediator
import com.smparkworld.discord.extensions.checkVoiceChannelValidation
import com.smparkworld.discord.extensions.sendEmbedsMessage
import com.smparkworld.discord.extensions.sendUnknownExceptionEmbedsMessage
import com.smparkworld.discord.usecase.GetVoiceChannelByEventAuthorUseCase
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import kotlin.random.Random

class BeeMusicPlayLeaveCommandHandler(
    private val musicManagerMediator: MusicManagerMediator,
    private val getVoiceChannelByEventAuthor: GetVoiceChannelByEventAuthorUseCase,
) : CommandHandler() {

    override fun handle(command: String, event: SlashCommandInteractionEvent) {
        checkVoiceChannelValidation(event, result = getVoiceChannelByEventAuthor(event)) { _ ->

            val guild = event.guild
            if (guild == null) {
                event.sendUnknownExceptionEmbedsMessage()
                return@checkVoiceChannelValidation
            }

            musicManagerMediator.obtainGuildTracker(guild.idLong)
                .scheduler
                .clearQueue()

            guild.audioManager.closeAudioConnection()

            val msg = when (Random.nextInt(0, 3)) {
                0 -> getString(StringCode.BEE_CMD_MUSIC_PLAY_LEAVE_MSG_1)
                1 -> getString(StringCode.BEE_CMD_MUSIC_PLAY_LEAVE_MSG_2)
                else -> getString(StringCode.BEE_CMD_MUSIC_PLAY_LEAVE_MSG_3)
            }
            val message = EmbedBuilder()
                .setDescription(msg)
                .build()
            event.sendEmbedsMessage(message)
        }
    }
}