package com.smparkworld.discord.bot.usecase

import com.smparkworld.discord.bot.usecase.GetAudioChannelUsersByEventAuthor.Result
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.interactions.Interaction

interface GetAudioChannelUsersByEventAuthor {

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

class GetAudioChannelUsersByEventAuthorImpl : GetAudioChannelUsersByEventAuthor {

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