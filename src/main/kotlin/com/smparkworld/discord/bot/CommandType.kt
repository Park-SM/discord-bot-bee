package com.smparkworld.discord.bot

import com.smparkworld.discord.base.StringCode
import com.smparkworld.discord.base.StringsParser.getString

enum class CommandType(
    val command: String,
    val description: String
) {
    BEE(getString(StringCode.BEE_CMD), getString(StringCode.BEE_CMD_DESC)),
    VALORANT(getString(StringCode.VAL_CMD), getString(StringCode.VAL_CMD_DESC))
}