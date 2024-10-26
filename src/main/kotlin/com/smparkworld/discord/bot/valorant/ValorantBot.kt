package com.smparkworld.discord.bot.valorant

import com.smparkworld.discord.base.StringCode
import com.smparkworld.discord.base.StringsParser.getString
import com.smparkworld.discord.bot.DiscordBot
import com.smparkworld.discord.bot.usecase.GetAudioChannelUsersByEventAuthorUseCase
import com.smparkworld.discord.bot.usecase.GetAudioChannelUsersByEventAuthorUseCaseImpl
import com.smparkworld.discord.bot.valorant.commands.ValorantRandomMapCommandHandler
import com.smparkworld.discord.bot.valorant.commands.ValorantRandomPickCommandHandler
import com.smparkworld.discord.bot.valorant.commands.ValorantRandomPickHardCommandHandler
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData

class ValorantBot(
    getVoiceChannelUsersByMember: GetAudioChannelUsersByEventAuthorUseCase = GetAudioChannelUsersByEventAuthorUseCaseImpl()
) : DiscordBot() {

    private val handlers = mapOf(
        getString(StringCode.VAL_CMD_RANDOM_PICK) to ValorantRandomPickCommandHandler(getVoiceChannelUsersByMember),
        getString(StringCode.VAL_CMD_RANDOM_PICK_HARD) to ValorantRandomPickHardCommandHandler(getVoiceChannelUsersByMember),
        getString(StringCode.VAL_CMD_RANDOM_MAP) to ValorantRandomMapCommandHandler(getVoiceChannelUsersByMember)
    )

    override fun applyCommandData(commandData: SlashCommandData) {
        commandData.addSubcommands(
            // 1. 에이전트 역할군 랜덤픽
            SubcommandData(getString(StringCode.VAL_CMD_RANDOM_PICK), getString(StringCode.VAL_CMD_RANDOM_PICK_DESC))
                .addOption(OptionType.USER, getString(StringCode.IGNORE_1), getString(StringCode.IGNORE_USER_DESC), false)
                .addOption(OptionType.USER, getString(StringCode.IGNORE_2), getString(StringCode.IGNORE_USER_DESC), false)
                .addOption(OptionType.USER, getString(StringCode.IGNORE_3), getString(StringCode.IGNORE_USER_DESC), false)
                .addOption(OptionType.USER, getString(StringCode.IGNORE_4), getString(StringCode.IGNORE_USER_DESC), false)
                .addOption(OptionType.USER, getString(StringCode.IGNORE_5), getString(StringCode.IGNORE_USER_DESC), false),

            // 2. 에이전트 역할군에 맞는 에이전트 랜덤픽
            SubcommandData(getString(StringCode.VAL_CMD_RANDOM_PICK_HARD), getString(StringCode.VAL_CMD_RANDOM_PICK_HARD_DESC))
                .addOption(OptionType.USER, getString(StringCode.IGNORE_1), getString(StringCode.IGNORE_USER_DESC), false)
                .addOption(OptionType.USER, getString(StringCode.IGNORE_2), getString(StringCode.IGNORE_USER_DESC), false)
                .addOption(OptionType.USER, getString(StringCode.IGNORE_3), getString(StringCode.IGNORE_USER_DESC), false)
                .addOption(OptionType.USER, getString(StringCode.IGNORE_4), getString(StringCode.IGNORE_USER_DESC), false)
                .addOption(OptionType.USER, getString(StringCode.IGNORE_5), getString(StringCode.IGNORE_USER_DESC), false),

            // 3. 맵 랜덤픽
            SubcommandData(getString(StringCode.VAL_CMD_RANDOM_MAP), getString(StringCode.VAL_CMD_RANDOM_MAP_DESC))
                .addOptions(
                    OptionData(OptionType.STRING, getString(StringCode.IGNORE_1), getString(StringCode.IGNORE_MAP_DESC), false).also(::applyIgnoreMapChoices),
                    OptionData(OptionType.STRING, getString(StringCode.IGNORE_2), getString(StringCode.IGNORE_MAP_DESC), false).also(::applyIgnoreMapChoices),
                    OptionData(OptionType.STRING, getString(StringCode.IGNORE_3), getString(StringCode.IGNORE_MAP_DESC), false).also(::applyIgnoreMapChoices),
                    OptionData(OptionType.STRING, getString(StringCode.IGNORE_4), getString(StringCode.IGNORE_MAP_DESC), false).also(::applyIgnoreMapChoices),
                    OptionData(OptionType.STRING, getString(StringCode.IGNORE_5), getString(StringCode.IGNORE_MAP_DESC), false).also(::applyIgnoreMapChoices),
                    OptionData(OptionType.STRING, getString(StringCode.IGNORE_6), getString(StringCode.IGNORE_MAP_DESC), false).also(::applyIgnoreMapChoices),
                    OptionData(OptionType.STRING, getString(StringCode.IGNORE_7), getString(StringCode.IGNORE_MAP_DESC), false).also(::applyIgnoreMapChoices),
                    OptionData(OptionType.STRING, getString(StringCode.IGNORE_8), getString(StringCode.IGNORE_MAP_DESC), false).also(::applyIgnoreMapChoices),
                ),
        )
    }

    private fun applyIgnoreMapChoices(option: OptionData) {
        ValorantMapType.values().forEach { option.addChoice(it.typeName, it.name) }
    }

    override fun onSlashCommand(command: String, event: SlashCommandInteractionEvent) {
        handlers[event.subcommandName]?.handle(command, event)
    }
}