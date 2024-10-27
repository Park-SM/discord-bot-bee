package com.smparkworld.discord.bot

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData

abstract class DiscordBot : ListenerAdapter() {

    protected abstract val commandHandlers: Map<String, CommandHandler>

    private var command: String? = null

    fun initialize(command: String) {
        this.command = command
    }

    abstract fun applyCommandData(commandData: SlashCommandData)

    open fun onSlashCommand(command: String, event: SlashCommandInteractionEvent) {
        commandHandlers[event.subcommandName]?.handle(command, event)
    }

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        commandHandlers.values.forEach { it.handleInteractionByButton(event) }
    }

    override fun onStringSelectInteraction(event: StringSelectInteractionEvent) {
        commandHandlers.values.forEach { it.handleInteractionByStringSelectMenu(event) }
    }

    override fun onEntitySelectInteraction(event: EntitySelectInteractionEvent) {
        commandHandlers.values.forEach { it.handleInteractionByEntitySelectMenu(event) }
    }

    final override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        if (event.name == command) onSlashCommand(event.name, event)
    }
}