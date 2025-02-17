package com.smparkworld.discord.bot

import com.smparkworld.discord.extensions.sendUnknownExceptionMessage
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent

abstract class CommandHandler {

    fun handleSafely(command: String, event: SlashCommandInteractionEvent) {
        try {
            handle(command, event)
        } catch (e: Exception) {
            e.printStackTrace()
            event.sendUnknownExceptionMessage()
        }
    }
    protected abstract fun handle(command: String, event: SlashCommandInteractionEvent)

    open fun handleInteractionByButton(event: ButtonInteractionEvent) {}
    open fun handleInteractionByStringSelectMenu(event: StringSelectInteractionEvent) {}
    open fun handleInteractionByEntitySelectMenu(event: EntitySelectInteractionEvent) {}
}