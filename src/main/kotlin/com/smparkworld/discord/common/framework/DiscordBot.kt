package com.smparkworld.discord.common.framework

import kotlinx.coroutines.*
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData

abstract class DiscordBot : ListenerAdapter() {

    protected abstract val commandHandlers: Map<String, CommandHandler>

    private val botCoroutineContext by lazy {
        Dispatchers.Default + SupervisorJob()
    }
    private var command: String? = null

    fun initialize(command: String) {
        this.command = command
        this.commandHandlers.values.forEach {
            it.commandHandlerScope = CoroutineScope(
                context = botCoroutineContext + Job(botCoroutineContext[Job])
            )
        }
    }

    abstract fun applyCommandData(commandData: SlashCommandData)

    open fun onSlashCommand(command: String, event: SlashCommandInteractionEvent) {
        commandHandlers[event.subcommandName]?.handleSafely(command, event)
    }

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        commandHandlers.values.forEach { it.handleInteractionByButton(event) }
    }

    override fun onModalInteraction(event: ModalInteractionEvent) {
        commandHandlers.values.forEach { it.handleInteractionByModal(event) }
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