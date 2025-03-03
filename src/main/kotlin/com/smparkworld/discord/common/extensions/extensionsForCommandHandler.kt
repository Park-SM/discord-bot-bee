package com.smparkworld.discord.common.extensions

import com.smparkworld.discord.common.base.StringCode
import com.smparkworld.discord.common.base.StringsParser.getString
import com.smparkworld.discord.common.framework.CommandHandler
import com.smparkworld.discord.domain.GetVoiceChannelByEventAuthorUseCase
import com.smparkworld.discord.domain.GetVoiceChannelUsersByEventAuthorUseCase
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

/**
 * 명령어를 호출한 사람이 속한 [VoiceChannel]을 반환합니다.
 * 어느 채널에도 속하지 않은 상태에서 명령어를 호출한 경우 에러 문구를 출력합니다.
 */
suspend fun CommandHandler.checkVoiceChannelValidation(
    event: SlashCommandInteractionEvent,
    result: GetVoiceChannelByEventAuthorUseCase.Result,
    perform: suspend (channel: VoiceChannel) -> Unit
) {
    when (result) {
        is GetVoiceChannelByEventAuthorUseCase.Result.Success -> {
            perform.invoke(result.voiceChannel)
        }
        is GetVoiceChannelByEventAuthorUseCase.Result.NotInVoiceChannel -> {
            event.sendNoticeEmbedsMessage(getString(StringCode.ABSENT_COMMAND_AUTHOR))
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
suspend fun CommandHandler.checkVoiceChannelValidation(
    event: SlashCommandInteractionEvent,
    result: GetVoiceChannelUsersByEventAuthorUseCase.Result,
    perform: suspend (members: List<Member>) -> Unit
) {
    when (result) {
        is GetVoiceChannelUsersByEventAuthorUseCase.Result.Success -> {
            perform.invoke(result.members)
        }
        is GetVoiceChannelUsersByEventAuthorUseCase.Result.NotInVoiceChannel -> {
            event.sendNoticeEmbedsMessage(getString(StringCode.ABSENT_COMMAND_AUTHOR))
        }
        is GetVoiceChannelUsersByEventAuthorUseCase.Result.Error -> {
            event.sendUnknownExceptionEmbedsMessage()
        }
    }
}