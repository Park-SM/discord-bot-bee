package com.smparkworld.discord.common.extensions

import com.smparkworld.discord.common.base.StringCode
import com.smparkworld.discord.common.base.StringsParser.getString
import kotlinx.coroutines.suspendCancellableCoroutine
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback

fun EmbedBuilder.addFieldIfNotNull(name: String, value: String?, inline: Boolean): EmbedBuilder {
    if (value != null) this.addField(name, value, inline)
    return this
}

fun EmbedBuilder.addFieldAsQuote(name: String, value: String, inline: Boolean): EmbedBuilder {
    this.addField(name, "> $value", inline)
    return this
}

fun IReplyCallback.sendEmbedsMessage(message: MessageEmbed) {
    this.replyEmbeds(message).queue()
}

suspend fun IReplyCallback.sendEmbedsMessageAndReturn(message: MessageEmbed): Message = suspendCancellableCoroutine { continuation ->
    this.replyEmbeds(message).queue {
        this.hook.retrieveOriginal().queue(continuation::resumeIfActive)
    }
}

fun IReplyCallback.sendNoticeEmbedsMessage(description: String) {
    val message = EmbedBuilder()
        .setDescription(description)
        .build()
    this.sendEmbedsMessage(message)
}

fun IReplyCallback.sendUnknownExceptionEmbedsMessage() {
    this.sendNoticeEmbedsMessage(description = getString(StringCode.UNKNOWN_EXCEPTION))
}