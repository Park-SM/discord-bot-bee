package com.smparkworld.discord.common.framework

import com.smparkworld.discord.common.extensions.getAuthorName
import com.smparkworld.discord.common.extensions.sendUnknownExceptionEmbedsMessage
import com.smparkworld.discord.core.logger.Logger
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent

abstract class CommandHandler {

    fun handleSafely(command: String, event: SlashCommandInteractionEvent) {
        try {
            Logger.i(TAG, "New command executed: `${event.commandString}` by ${event.getAuthorName()}")
            handle(command, event)
        } catch (e: Exception) {
            e.printStackTrace()
            event.sendUnknownExceptionEmbedsMessage()
        }
    }
    protected abstract fun handle(command: String, event: SlashCommandInteractionEvent)

    open fun handleInteractionByButton(event: ButtonInteractionEvent) {}
    open fun handleInteractionByStringSelectMenu(event: StringSelectInteractionEvent) {}
    open fun handleInteractionByEntitySelectMenu(event: EntitySelectInteractionEvent) {}

    companion object {
        private const val TAG = "CommandHandler"
    }
}