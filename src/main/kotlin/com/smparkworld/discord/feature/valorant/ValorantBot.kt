package com.smparkworld.discord.feature.valorant

import com.smparkworld.discord.common.base.StringCode
import com.smparkworld.discord.common.base.StringsParser.getString
import com.smparkworld.discord.common.framework.DiscordBot
import com.smparkworld.discord.domain.GetVoiceChannelByNameUseCase
import com.smparkworld.discord.domain.GetVoiceChannelByNameUseCaseImpl
import com.smparkworld.discord.domain.GetVoiceChannelUsersByEventAuthorUseCase
import com.smparkworld.discord.domain.GetVoiceChannelUsersByEventAuthorUseCaseImpl
import com.smparkworld.discord.feature.valorant.commands.ValorantRandomMapCommandHandler
import com.smparkworld.discord.feature.valorant.commands.ValorantRandomPickCommandHandler
import com.smparkworld.discord.feature.valorant.commands.ValorantRandomPickHardCommandHandler
import com.smparkworld.discord.feature.valorant.commands.ValorantTeamCommandHandler
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData

class ValorantBot(
    getVoiceChannelUsersByMember: GetVoiceChannelUsersByEventAuthorUseCase = GetVoiceChannelUsersByEventAuthorUseCaseImpl(),
    getVoiceChannelByNameUseCase: GetVoiceChannelByNameUseCase = GetVoiceChannelByNameUseCaseImpl()
) : DiscordBot() {

    override val commandHandlers = mapOf(
        getString(StringCode.VAL_CMD_RANDOM_PICK) to ValorantRandomPickCommandHandler(getVoiceChannelUsersByMember),
        getString(StringCode.VAL_CMD_RANDOM_PICK_HARD) to ValorantRandomPickHardCommandHandler(getVoiceChannelUsersByMember),
        getString(StringCode.VAL_CMD_RANDOM_MAP) to ValorantRandomMapCommandHandler(getVoiceChannelUsersByMember),
        getString(StringCode.VAL_CMD_TEAM) to ValorantTeamCommandHandler(getVoiceChannelUsersByMember, getVoiceChannelByNameUseCase)
    )

    override fun applyCommandData(commandData: SlashCommandData) {
        commandData.addSubcommands(
            // 1. 에이전트 역할군 랜덤픽
            SubcommandData(getString(StringCode.VAL_CMD_RANDOM_PICK), getString(StringCode.VAL_CMD_RANDOM_PICK_DESC))
                .addOption(OptionType.USER, getString(StringCode.IGNORE1), getString(StringCode.IGNORE_USER_DESC), false)
                .addOption(OptionType.USER, getString(StringCode.IGNORE2), getString(StringCode.IGNORE_USER_DESC), false)
                .addOption(OptionType.USER, getString(StringCode.IGNORE3), getString(StringCode.IGNORE_USER_DESC), false)
                .addOption(OptionType.USER, getString(StringCode.IGNORE4), getString(StringCode.IGNORE_USER_DESC), false)
                .addOption(OptionType.USER, getString(StringCode.IGNORE5), getString(StringCode.IGNORE_USER_DESC), false),

            // 2. 에이전트 역할군에 맞는 에이전트 랜덤픽
            SubcommandData(getString(StringCode.VAL_CMD_RANDOM_PICK_HARD), getString(StringCode.VAL_CMD_RANDOM_PICK_HARD_DESC))
                .addOption(OptionType.USER, getString(StringCode.IGNORE1), getString(StringCode.IGNORE_USER_DESC), false)
                .addOption(OptionType.USER, getString(StringCode.IGNORE2), getString(StringCode.IGNORE_USER_DESC), false)
                .addOption(OptionType.USER, getString(StringCode.IGNORE3), getString(StringCode.IGNORE_USER_DESC), false)
                .addOption(OptionType.USER, getString(StringCode.IGNORE4), getString(StringCode.IGNORE_USER_DESC), false)
                .addOption(OptionType.USER, getString(StringCode.IGNORE5), getString(StringCode.IGNORE_USER_DESC), false),

            // 3. 맵 랜덤픽
            SubcommandData(getString(StringCode.VAL_CMD_RANDOM_MAP), getString(StringCode.VAL_CMD_RANDOM_MAP_DESC))
                .addOptions(
                    OptionData(OptionType.STRING, getString(StringCode.IGNORE1), getString(StringCode.IGNORE_MAP_DESC), false).also(::applyIgnoreMapChoices),
                    OptionData(OptionType.STRING, getString(StringCode.IGNORE2), getString(StringCode.IGNORE_MAP_DESC), false).also(::applyIgnoreMapChoices),
                    OptionData(OptionType.STRING, getString(StringCode.IGNORE3), getString(StringCode.IGNORE_MAP_DESC), false).also(::applyIgnoreMapChoices),
                    OptionData(OptionType.STRING, getString(StringCode.IGNORE4), getString(StringCode.IGNORE_MAP_DESC), false).also(::applyIgnoreMapChoices),
                    OptionData(OptionType.STRING, getString(StringCode.IGNORE5), getString(StringCode.IGNORE_MAP_DESC), false).also(::applyIgnoreMapChoices),
                    OptionData(OptionType.STRING, getString(StringCode.IGNORE6), getString(StringCode.IGNORE_MAP_DESC), false).also(::applyIgnoreMapChoices),
                    OptionData(OptionType.STRING, getString(StringCode.IGNORE7), getString(StringCode.IGNORE_MAP_DESC), false).also(::applyIgnoreMapChoices),
                    OptionData(OptionType.STRING, getString(StringCode.IGNORE8), getString(StringCode.IGNORE_MAP_DESC), false).also(::applyIgnoreMapChoices),
                ),

            // 4. 팀 만들기
            SubcommandData(getString(StringCode.VAL_CMD_TEAM), getString(StringCode.VAL_CMD_TEAM_DESC))
                .addOption(OptionType.USER, getString(StringCode.IGNORE1), getString(StringCode.IGNORE_USER_DESC), false)
                .addOption(OptionType.USER, getString(StringCode.IGNORE2), getString(StringCode.IGNORE_USER_DESC), false)
                .addOption(OptionType.USER, getString(StringCode.IGNORE3), getString(StringCode.IGNORE_USER_DESC), false)
                .addOption(OptionType.USER, getString(StringCode.IGNORE4), getString(StringCode.IGNORE_USER_DESC), false)
                .addOption(OptionType.USER, getString(StringCode.IGNORE5), getString(StringCode.IGNORE_USER_DESC), false),
        )
    }

    private fun applyIgnoreMapChoices(option: OptionData) {
        ValorantMapType.values().forEach { option.addChoice(it.typeName, it.name) }
    }
}