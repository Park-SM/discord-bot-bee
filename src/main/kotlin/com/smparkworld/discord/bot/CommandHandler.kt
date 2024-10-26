package com.smparkworld.discord.bot

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

interface CommandHandler {

    fun handle(command: String, event: SlashCommandInteractionEvent)
}