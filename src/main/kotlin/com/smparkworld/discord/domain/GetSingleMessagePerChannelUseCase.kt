package com.smparkworld.discord.domain

import com.smparkworld.discord.domain.repository.MessageRepository
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.interactions.Interaction

/**
 * 명령어를 호출한 Channel 기준으로 저장된 하나의 [Message]를 반환합니다.
 */
interface GetSingleMessagePerChannelUseCase {

    operator fun invoke(event: Interaction): Message?
}

class GetSingleMessagePerChannelUseCaseImpl(
    private val repository: MessageRepository
) : GetSingleMessagePerChannelUseCase {

    override fun invoke(event: Interaction): Message? {
        return repository.getMessageByKey(
            key = (event.channel as? TextChannel)?.id ?: return null
        )
    }
}