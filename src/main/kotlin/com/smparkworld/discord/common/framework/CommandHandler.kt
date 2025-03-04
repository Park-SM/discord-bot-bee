package com.smparkworld.discord.common.framework

import com.smparkworld.discord.common.extensions.getAuthorName
import com.smparkworld.discord.common.extensions.sendUnknownExceptionEmbedsMessage
import com.smparkworld.discord.core.logger.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent

abstract class CommandHandler {

    lateinit var commandHandlerScope: CoroutineScope

    fun handleSafely(command: String, event: SlashCommandInteractionEvent) {
        event.runSafely {
            commandHandlerScope.launch {
                event.runSafely {
                    Logger.i(TAG, "New command executed: `${event.commandString}` by ${event.getAuthorName()}")
                    handle(command, event)
                }
            }
        }
    }
    protected abstract suspend fun handle(command: String, event: SlashCommandInteractionEvent)

    open fun handleInteractionByModal(event: ModalInteractionEvent) {}
    open fun handleInteractionByButton(event: ButtonInteractionEvent) {}
    open fun handleInteractionByStringSelectMenu(event: StringSelectInteractionEvent) {}
    open fun handleInteractionByEntitySelectMenu(event: EntitySelectInteractionEvent) {}

    private inline fun SlashCommandInteractionEvent.runSafely(perform: () -> Unit) {
        try {
            perform()
        } catch (e: Exception) {
            e.printStackTrace()
            sendUnknownExceptionEmbedsMessage()
        }
    }

    companion object {
        private const val TAG = "CommandHandler"
    }
}