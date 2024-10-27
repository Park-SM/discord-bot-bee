package com.smparkworld.discord.bot.bee

import com.smparkworld.discord.base.StringCode
import com.smparkworld.discord.base.StringsParser.getString
import com.smparkworld.discord.bot.DiscordBot
import com.smparkworld.discord.bot.bee.commands.BeeHelpCommandHandler
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData

class BeeBot : DiscordBot() {

    override val commandHandlers = mapOf(
        getString(StringCode.BEE_CMD_HELP) to BeeHelpCommandHandler()
    )

    override fun applyCommandData(commandData: SlashCommandData) {
        commandData.addSubcommands(
            // 1. 기능 및 명령어 도움말
            SubcommandData(getString(StringCode.BEE_CMD_HELP), getString(StringCode.BEE_CMD_HELP_DESC)),
        )
    }
}