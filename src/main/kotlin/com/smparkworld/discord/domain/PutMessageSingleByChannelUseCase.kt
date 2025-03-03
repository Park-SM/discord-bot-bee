package com.smparkworld.discord.domain

import com.smparkworld.discord.domain.repository.MessageRepository
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.interactions.Interaction

/**
 * 명령어를 호출한 Channel 기준으로 하나의 [Message]를 저장합니다.
 * 호출 시 기존에 저장된 [Message]가 있으면 대체합니다.
 */
interface SaveSingleMessagePerChannelUseCase {

    operator fun invoke(event: Interaction, message: Message?)
}

class SaveSingleMessagePerChannelUseCaseImpl(
    private val repository: MessageRepository
) : SaveSingleMessagePerChannelUseCase {

    override fun invoke(event: Interaction, message: Message?) {
        val key = (event.channel as? TextChannel)?.id ?: return

        if (message != null) {
            repository.putMessageByKey(key = key, message = message)
        } else {
            repository.removeMessageByKey(key = key)
        }
    }
}