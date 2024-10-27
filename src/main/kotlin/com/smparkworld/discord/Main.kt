package com.smparkworld.discord

import com.smparkworld.discord.bot.DiscordBotType
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.hooks.EventListener
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.internal.interactions.CommandDataImpl

fun main(args: Array<String>) {
    val token = try {
        args[0]
    } catch (e: Exception) {
        throw IllegalArgumentException("Not found token")
    }
    val jda = JDABuilder.create(
            token,
            GatewayIntent.MESSAGE_CONTENT,
            GatewayIntent.DIRECT_MESSAGE_POLLS, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES
        )
        .build()

    initDiscordBots(jda)
    initDiscordBotsCommands(jda)
}

fun initDiscordBots(jda: JDA) {
    val bots: List<EventListener> = DiscordBotType.values()
        .onEach { it.bot.initialize(command = it.commandType.command) }
        .map { it.bot }
    jda.addEventListener(*bots.toTypedArray())
}

fun initDiscordBotsCommands(jda: JDA) {
    val commands: List<CommandData> = DiscordBotType.values().map { type ->
        CommandDataImpl(type.commandType.command, type.commandType.description)
            .apply(type.bot::applyCommandData)
    }
    jda.updateCommands()
        .addCommands(commands)
        .queue()
}