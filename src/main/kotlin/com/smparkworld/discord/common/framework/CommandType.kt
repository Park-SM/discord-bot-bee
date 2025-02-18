package com.smparkworld.discord.common.framework

import com.smparkworld.discord.common.base.StringCode
import com.smparkworld.discord.common.base.StringsParser.getString

enum class CommandType(
    val command: String,
    val description: String
) {
    BEE(getString(StringCode.BEE_CMD), getString(StringCode.BEE_CMD_DESC)),
    VALORANT(getString(StringCode.VAL_CMD), getString(StringCode.VAL_CMD_DESC))
}