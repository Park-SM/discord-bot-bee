package com.smparkworld.discord.bot

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData

abstract class DiscordBot : ListenerAdapter() {

    private var command: String? = null

    fun initialize(command: String) {
        this.command = command
    }

    final override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        if (event.name == command) onSlashCommand(event.name, event)
    }

    open fun onSlashCommand(command: String, event: SlashCommandInteractionEvent) {}
    open fun applyCommandData(commandData: SlashCommandData) {}
}