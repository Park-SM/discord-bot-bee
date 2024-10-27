package com.smparkworld.discord.usecase

import com.smparkworld.discord.usecase.GetVoiceChannelUsersByEventAuthorUseCase.Result
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.interactions.Interaction

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
        val member = event.member
        val voiceState = member?.voiceState

        if (voiceState?.inAudioChannel() == true) {
            Result.Success(members = voiceState.channel?.members.orEmpty())
        } else {
            Result.NotInVoiceChannel
        }
    } catch (e: Exception) {
        Result.Error(e)
    }
}