package com.smparkworld.discord.common.extensions

import com.smparkworld.discord.common.base.StringCode
import com.smparkworld.discord.common.base.StringsParser.getString
import kotlinx.coroutines.suspendCancellableCoroutine
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback
import net.dv8tion.jda.api.interactions.components.ItemComponent
import java.util.concurrent.TimeUnit

fun EmbedBuilder.addFieldIfNotNull(name: String, value: String?, inline: Boolean): EmbedBuilder {
    if (value != null) this.addField(name, value, inline)
    return this
}

fun EmbedBuilder.addFieldAsQuote(name: String, value: String, inline: Boolean): EmbedBuilder {
    this.addField(name, "> $value", inline)
    return this
}

fun IReplyCallback.sendDeferReply() {
    this.deferReply().setEphemeral(true).queue {
        it.deleteOriginal().queueAfter(500L, TimeUnit.MILLISECONDS)
    }
}

fun IReplyCallback.sendEmbedsMessage(message: MessageEmbed, ephemeral: Boolean = false, deleteAfter: Long = -1) {
    this.replyEmbeds(message).setEphemeral(ephemeral).queue {
        if (deleteAfter > -1) it.deleteOriginal().queueAfter(deleteAfter, TimeUnit.MILLISECONDS)
    }
}

fun IReplyCallback.sendNoticeEmbedsMessage(description: String, ephemeral: Boolean = false, deleteAfter: Long = -1) {
    val message = EmbedBuilder()
        .setDescription(description)
        .build()
    this.sendEmbedsMessage(message, ephemeral, deleteAfter)
}

fun IReplyCallback.sendUnknownExceptionEmbedsMessage() {
    this.sendNoticeEmbedsMessage(description = getString(StringCode.UNKNOWN_EXCEPTION))
}

suspend fun IReplyCallback.sendEmbedsMessageAndReturn(message: MessageEmbed, actions: List<ItemComponent>): Message = suspendCancellableCoroutine { continuation ->
    this.replyEmbeds(message)
        .addActionRow(actions)
        .queue { it.retrieveOriginal().queue(continuation::resumeIfActive) }
}