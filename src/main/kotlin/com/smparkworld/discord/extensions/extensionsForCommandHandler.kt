package com.smparkworld.discord.extensions

import com.smparkworld.discord.base.StringCode
import com.smparkworld.discord.base.StringsParser.getString
import com.smparkworld.discord.bot.CommandHandler
import com.smparkworld.discord.usecase.GetVoiceChannelByEventAuthorUseCase
import com.smparkworld.discord.usecase.GetVoiceChannelUsersByEventAuthorUseCase
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

/**
 * 명령어를 호출한 사람이 속한 [VoiceChannel]을 반환합니다.
 * 어느 채널에도 속하지 않은 상태에서 명령어를 호출한 경우 에러 문구를 출력합니다.
 */
fun CommandHandler.checkVoiceChannelValidation(
    event: SlashCommandInteractionEvent,
    result: GetVoiceChannelByEventAuthorUseCase.Result,
    perform: (channel: VoiceChannel) -> Unit
) {
    when (result) {
        is GetVoiceChannelByEventAuthorUseCase.Result.Success -> {
            perform.invoke(result.voiceChannel)
        }
        is GetVoiceChannelByEventAuthorUseCase.Result.NotInVoiceChannel -> {
            val message = EmbedBuilder()
                .setDescription(getString(StringCode.ABSENT_COMMAND_AUTHOR))
                .build()
            event.sendEmbedsMessage(message)
        }
        is GetVoiceChannelByEventAuthorUseCase.Result.Error -> {
            event.sendUnknownExceptionEmbedsMessage()
        }
    }
}

/**
 * 명령어를 호출한 사람이 속한 [VoiceChannel]을 찾고, 그 [VoiceChannel]에 참여 중엔 [Member]들을 반환합니다.
 * 어느 채널에도 속하지 않은 상태에서 명령어를 호출한 경우 에러 문구를 출력합니다.
 */
fun CommandHandler.checkVoiceChannelValidation(
    event: SlashCommandInteractionEvent,
    result: GetVoiceChannelUsersByEventAuthorUseCase.Result,
    perform: (members: List<Member>) -> Unit
) {
    when (result) {
        is GetVoiceChannelUsersByEventAuthorUseCase.Result.Success -> {
            perform.invoke(result.members)
        }
        is GetVoiceChannelUsersByEventAuthorUseCase.Result.NotInVoiceChannel -> {
            val message = EmbedBuilder()
                .setDescription(getString(StringCode.ABSENT_COMMAND_AUTHOR))
                .build()
            event.sendEmbedsMessage(message)
        }
        is GetVoiceChannelUsersByEventAuthorUseCase.Result.Error -> {
            event.sendUnknownExceptionEmbedsMessage()
        }
    }
}