package com.smparkworld.discord.bot.bee

import com.smparkworld.discord.base.StringCode
import com.smparkworld.discord.base.StringsParser.getString
import com.smparkworld.discord.bot.DiscordBot
import com.smparkworld.discord.bot.bee.commands.BeeHelpCommandHandler
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData

class BeeBot : DiscordBot() {

    private val handlers = mapOf(
        getString(StringCode.BEE_CMD_HELP) to BeeHelpCommandHandler()
    )

    override fun applyCommandData(commandData: SlashCommandData) {
        commandData.addSubcommands(
            // 1. 기능 및 명령어 도움말
            SubcommandData(getString(StringCode.BEE_CMD_HELP), getString(StringCode.BEE_CMD_HELP_DESC)),
        )
    }

    override fun onSlashCommand(command: String, event: SlashCommandInteractionEvent) {
        handlers[event.subcommandName]?.handle(command, event)
    }

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        handlers.values.forEach { it.handleInteractionByButton(event) }
    }

    override fun onStringSelectInteraction(event: StringSelectInteractionEvent) {
        handlers.values.forEach { it.handleInteractionByStringSelectMenu(event) }
    }
}