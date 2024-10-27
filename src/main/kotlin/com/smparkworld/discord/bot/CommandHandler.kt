package com.smparkworld.discord.bot

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent

interface CommandHandler {

    fun handle(command: String, event: SlashCommandInteractionEvent)

    fun handleInteractionByButton(event: ButtonInteractionEvent) {}
    fun handleInteractionByStringSelectMenu(event: StringSelectInteractionEvent) {}
    fun handleInteractionByEntitySelectMenu(event: EntitySelectInteractionEvent) {}
}