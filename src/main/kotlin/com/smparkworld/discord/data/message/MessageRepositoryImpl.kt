package com.smparkworld.discord.data.message

import com.smparkworld.discord.domain.repository.MessageRepository
import net.dv8tion.jda.api.entities.Message
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

object MessageRepositoryImpl : MessageRepository {

    private val cachedMessages: ConcurrentMap<Long, Message> = ConcurrentHashMap()

    override fun putMessageByKey(key: Long, message: Message?) {
        cachedMessages[key] = message
    }

    override fun getMessageByKey(key: Long): Message? {
        return cachedMessages[key]
    }

    override fun removeMessageByKey(key: Long) {
        cachedMessages.remove(key)
    }
}