package com.smparkworld.discord.extensions

import com.smparkworld.discord.base.StringCode
import com.smparkworld.discord.base.StringsParser.getString
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback

fun EmbedBuilder.addFieldAsQuote(name: String, value: String, inline: Boolean): EmbedBuilder {
    this.addField(name, "> $value", inline)
    return this
}

fun IReplyCallback.sendEmbedsMessage(message: MessageEmbed) {
    this.replyEmbeds(message).queue()
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