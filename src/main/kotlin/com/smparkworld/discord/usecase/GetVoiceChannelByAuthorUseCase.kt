package com.smparkworld.discord.usecase

import com.smparkworld.discord.usecase.GetVoiceChannelByEventAuthorUseCase.Result
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel
import net.dv8tion.jda.api.interactions.Interaction

interface GetVoiceChannelByEventAuthorUseCase {

    operator fun invoke(event: Interaction): Result

    sealed interface Result {

        object NotInVoiceChannel : Result

        data class Error(
            val exception: Exception
        ) : Result

        data class Success(
            val voiceChannel: VoiceChannel
        ) : Result
    }
}

class GetVoiceChannelByEventAuthorUseCaseImpl : GetVoiceChannelByEventAuthorUseCase {

    override operator fun invoke(event: Interaction): Result = try {
        val voiceState = event.member?.voiceState
        val channel = voiceState?.channel as? VoiceChannel

        if (voiceState?.inAudioChannel() != null && channel != null) {
            Result.Success(voiceChannel = channel)
        } else {
            Result.NotInVoiceChannel
        }
    } catch (e: Exception) {
        Result.Error(e)
    }
}