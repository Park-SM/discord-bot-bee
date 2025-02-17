package com.smparkworld.discord.usecase

import com.smparkworld.discord.usecase.GetVoiceChannelUsersByEventAuthorUseCase.Result
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel
import net.dv8tion.jda.api.interactions.Interaction

/**
 * 명령어를 호출한 사람이 속한 [VoiceChannel]을 찾고,
 * 이 [VoiceChannel]에 참여 중엔 [Member]들을 반환합니다.
 */
interface GetVoiceChannelUsersByEventAuthorUseCase {

    operator fun invoke(event: Interaction): Result

    sealed interface Result {

        object NotInVoiceChannel : Result

        data class Error(
            val exception: Exception
        ) : Result

        data class Success(
            val members: List<Member>
        ) : Result
    }
}

class GetVoiceChannelUsersByEventAuthorUseCaseImpl : GetVoiceChannelUsersByEventAuthorUseCase {

    override operator fun invoke(event: Interaction): Result = try {
        val voiceState = event.member?.voiceState
        val channel = voiceState?.channel as? VoiceChannel

        if (voiceState?.inAudioChannel() == true && channel != null) {
            Result.Success(members = channel.members)
        } else {
            Result.NotInVoiceChannel
        }
    } catch (e: Exception) {
        Result.Error(e)
    }
}