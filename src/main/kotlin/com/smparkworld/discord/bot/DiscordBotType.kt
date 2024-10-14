package com.smparkworld.discord.bot

import com.smparkworld.discord.bot.valorant.ValorantBot

enum class DiscordBotType(
    val commandType: CommandType,
    val bot: DiscordBot
) {

    VALORANT(CommandType.VALORANT, ValorantBot())
}