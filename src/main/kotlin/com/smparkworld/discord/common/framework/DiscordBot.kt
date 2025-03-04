package com.smparkworld.discord.common.framework

import com.smparkworld.discord.common.extensions.getAuthorName
import com.smparkworld.discord.core.logger.Logger
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
        Logger.s(TAG, "$command || Command executed. `${event.commandString}` by ${event.getAuthorName()}")
    }

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        if (commandHandlers.values.map { it.handleInteractionByButton(event) }.any { it }) {
            Logger.s(TAG, "$command || Button clicked. `${event.interaction.button.label}` by ${event.getAuthorName()}")
        }
    }

    override fun onModalInteraction(event: ModalInteractionEvent) {
        if (commandHandlers.values.map { it.handleInteractionByModal(event) }.any { it }) {
            Logger.s(TAG, "$command || Modal submitted. `${event.interaction.values}` by ${event.getAuthorName()}")
        }
    }

    override fun onStringSelectInteraction(event: StringSelectInteractionEvent) {
        if (commandHandlers.values.map { it.handleInteractionByStringSelectMenu(event) }.any { it }) {
            Logger.s(TAG, "$command || StringSelect submitted. `${event.interaction.values}` by ${event.getAuthorName()}")
        }
    }

    override fun onEntitySelectInteraction(event: EntitySelectInteractionEvent) {
        if (commandHandlers.values.map { it.handleInteractionByEntitySelectMenu(event) }.any { it }) {
            Logger.s(TAG, "$command || EntitySelect submitted. `${event.interaction.values}` by ${event.getAuthorName()}")
        }
    }

    final override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        if (event.name == command) onSlashCommand(event.name, event)
    }

    companion object {
        private const val TAG = "DiscordBot"
    }
}