package com.smparkworld.discord.bot.bee.commands

import com.smparkworld.discord.base.StringCode
import com.smparkworld.discord.base.StringsParser
import com.smparkworld.discord.base.StringsParser.getString
import com.smparkworld.discord.bot.CommandHandler
import com.smparkworld.discord.bot.bee.commands.player.MusicManagerMediator
import com.smparkworld.discord.usecase.GetVoiceChannelByEventAuthorUseCase
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import kotlin.random.Random

class BeeMusicPlayLeaveCommandHandler(
    private val musicManagerMediator: MusicManagerMediator,
    private val getVoiceChannelByEventAuthor: GetVoiceChannelByEventAuthorUseCase,
) : CommandHandler() {

    override fun handle(command: String, event: SlashCommandInteractionEvent) {
        checkVoiceChannelValidation(event) { _ ->

            val guild = event.guild
            if (guild == null) {
                val message = EmbedBuilder()
                    .setDescription(getString(StringCode.UNKNOWN_EXCEPTION))
                    .build()
                event.replyEmbeds(message).queue()
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
            event.replyEmbeds(message).queue()
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
}