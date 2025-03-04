package com.smparkworld.discord.domain.repository

import net.dv8tion.jda.api.entities.Message

interface MessageRepository {

    fun putMessageByKey(key: Long, message: Message?)

    fun getMessageByKey(key: Long): Message?

    fun removeMessageByKey(key: Long)
}