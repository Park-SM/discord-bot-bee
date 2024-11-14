package com.smparkworld.discord.bot.bee

import com.smparkworld.discord.base.StringCode
import com.smparkworld.discord.base.StringsParser.getString
import com.smparkworld.discord.bot.DiscordBot
import com.smparkworld.discord.bot.bee.commands.BeeForceMoveUserCommandHandler
import com.smparkworld.discord.bot.bee.commands.BeeHelpCommandHandler
import com.smparkworld.discord.usecase.GetVoiceChannelByEventAuthorUseCase
import com.smparkworld.discord.usecase.GetVoiceChannelByEventAuthorUseCaseImpl
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData

class BeeBot(
    getVoiceChannelByEventAuthor: GetVoiceChannelByEventAuthorUseCase = GetVoiceChannelByEventAuthorUseCaseImpl(),
) : DiscordBot() {

    override val commandHandlers = mapOf(
        getString(StringCode.BEE_CMD_HELP) to BeeHelpCommandHandler(),
        getString(StringCode.BEE_CMD_FORCE_MOVE_USER) to BeeForceMoveUserCommandHandler(getVoiceChannelByEventAuthor)
    )

    override fun applyCommandData(commandData: SlashCommandData) {
        commandData.addSubcommands(
            // 1. 기능 및 명령어 도움말
            SubcommandData(getString(StringCode.BEE_CMD_HELP), getString(StringCode.BEE_CMD_HELP_DESC)),

            // 2. 유저 강제로 데려오기
            SubcommandData(getString(StringCode.BEE_CMD_FORCE_MOVE_USER), getString(StringCode.BEE_CMD_FORCE_MOVE_USER_DESC))
                .addOption(OptionType.USER, getString(StringCode.TARGET_USER), getString(StringCode.TARGET_USER_DESC)),
        )
    }
}