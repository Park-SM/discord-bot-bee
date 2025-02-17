package com.smparkworld.discord.extensions

import com.smparkworld.discord.base.StringCode
import com.smparkworld.discord.base.StringsParser.getString
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback

fun IReplyCallback.sendEmbedsMessage(message: MessageEmbed) {
    this.replyEmbeds(message).queue()
}

fun IReplyCallback.sendUnknownExceptionMessage() {
    val message = EmbedBuilder()
        .setDescription(getString(StringCode.UNKNOWN_EXCEPTION))
        .build()
    this.sendEmbedsMessage(message)
}