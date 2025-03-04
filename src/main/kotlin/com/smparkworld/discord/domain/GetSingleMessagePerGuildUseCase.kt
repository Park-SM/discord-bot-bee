package com.smparkworld.discord.domain

import com.smparkworld.discord.domain.repository.MessageRepository
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.interactions.Interaction

/**
 * 명령어를 호출한 Guild 기준으로 저장된 하나의 [Message]를 반환합니다.
 */
interface GetSingleMessagePerGuildUseCase {

    operator fun invoke(event: Interaction): Message?
}

class GetSingleMessagePerGuildUseCaseImpl(
    private val repository: MessageRepository
) : GetSingleMessagePerGuildUseCase {

    override fun invoke(event: Interaction): Message? {
        return repository.getMessageByKey(
            key = event.guild?.idLong ?: return null
        )
    }
}