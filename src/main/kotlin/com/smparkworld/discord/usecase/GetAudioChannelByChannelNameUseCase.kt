package com.smparkworld.discord.usecase

import com.smparkworld.discord.usecase.GetVoiceChannelByNameUseCase.Result
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel

interface GetVoiceChannelByNameUseCase {

    operator fun invoke(
        guild: Guild,
        channelName: String,
        perform: (channel: VoiceChannel) -> Unit
    ): Result

    sealed interface Result {

        data class Error(
            val exception: Exception
        ) : Result

        object Success : Result
    }
}

class GetVoiceChannelByNameUseCaseImpl : GetVoiceChannelByNameUseCase {

    override operator fun invoke(
        guild: Guild,
        channelName: String,
        perform: (channel: VoiceChannel) -> Unit
    ): Result = try {
        val channel = guild.voiceChannels.find { it.name == channelName }
        if (channel != null) {
            perform.invoke(channel)
        } else {
            guild.createVoiceChannel(channelName).queue(perform)
        }
        Result.Success
    } catch (e: Exception) {
        Result.Error(e)
    }
}