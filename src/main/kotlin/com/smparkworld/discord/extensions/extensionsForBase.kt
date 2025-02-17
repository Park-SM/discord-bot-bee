package com.smparkworld.discord.extensions

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

fun <T> randomValueOf(vararg values: T): T =
    values.random()

fun SlashCommandInteractionEvent.requireGuild(): Guild =
    this.guild ?: throw IllegalStateException("Not found guild")

fun SlashCommandInteractionEvent.requireAuthor(): Member =
    this.member ?: throw IllegalStateException("Not found author member")