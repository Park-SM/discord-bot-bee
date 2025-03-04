package com.smparkworld.discord.common.framework

import com.smparkworld.discord.common.extensions.sendUnknownExceptionEmbedsMessage
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
                    handle(command, event)
                }
            }
        }
    }
    protected abstract suspend fun handle(command: String, event: SlashCommandInteractionEvent)

    open fun handleInteractionByModal(event: ModalInteractionEvent): Boolean = false
    open fun handleInteractionByButton(event: ButtonInteractionEvent): Boolean = false
    open fun handleInteractionByStringSelectMenu(event: StringSelectInteractionEvent): Boolean = false
    open fun handleInteractionByEntitySelectMenu(event: EntitySelectInteractionEvent): Boolean = false

    private inline fun SlashCommandInteractionEvent.runSafely(perform: () -> Unit) {
        try {
            perform()
        } catch (e: Exception) {
            e.printStackTrace()
            sendUnknownExceptionEmbedsMessage()
        }
    }
}