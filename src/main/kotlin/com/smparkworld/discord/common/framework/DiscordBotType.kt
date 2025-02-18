package com.smparkworld.discord.common.framework

import com.smparkworld.discord.feature.bee.BeeBot
import com.smparkworld.discord.feature.valorant.ValorantBot

enum class DiscordBotType(
    val commandType: CommandType,
    val bot: DiscordBot
) {
    BEE(CommandType.BEE, BeeBot()),
    VALORANT(CommandType.VALORANT, ValorantBot())
}