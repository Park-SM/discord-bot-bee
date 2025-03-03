package com.smparkworld.discord.domain.repository

import net.dv8tion.jda.api.entities.Message

interface MessageRepository {

    fun putMessageByKey(key: String, message: Message?)

    fun getMessageByKey(key: String): Message?

    fun removeMessageByKey(key: String)
}