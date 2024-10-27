package com.smparkworld.discord.bot

import com.smparkworld.discord.bot.bee.BeeBot
import com.smparkworld.discord.bot.valorant.ValorantBot

enum class DiscordBotType(
    val commandType: CommandType,
    val bot: DiscordBot
) {
    BEE(CommandType.BEE, BeeBot()),
    VALORANT(CommandType.VALORANT, ValorantBot())
}