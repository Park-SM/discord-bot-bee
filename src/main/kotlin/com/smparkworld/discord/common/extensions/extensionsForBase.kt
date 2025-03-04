package com.smparkworld.discord.common.extensions

import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.Interaction
import net.dv8tion.jda.api.requests.RestAction
import kotlin.coroutines.resume

fun <T> randomValueOf(vararg values: T): T =
    values.random()

fun SlashCommandInteractionEvent.requireGuild(): Guild =
    this.guild ?: throw IllegalStateException("Not found guild")

fun SlashCommandInteractionEvent.requireAuthor(): Member =
    this.member ?: throw IllegalStateException("Not found author member")

fun Interaction.getAuthorName(): String? =
    this.member?.effectiveName

fun <T> CancellableContinuation<T>.resumeIfActive(value: T) {
    if (isActive) resume(value)
}

suspend fun <T> RestAction<T>.execute(): T = suspendCancellableCoroutine { continuation ->
    queue(continuation::resumeIfActive)
}