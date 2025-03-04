package com.smparkworld.discord.feature.bee

import com.smparkworld.discord.common.base.StringCode
import com.smparkworld.discord.common.base.StringsParser.getString
import com.smparkworld.discord.common.framework.DiscordBot
import com.smparkworld.discord.data.message.MessageRepositoryImpl
import com.smparkworld.discord.domain.*
import com.smparkworld.discord.feature.bee.commands.BeeForceMoveUserCommandHandler
import com.smparkworld.discord.feature.bee.commands.BeeHelpCommandHandler
import com.smparkworld.discord.feature.bee.commands.BeeMusicPlayBySearchCommandHandler
import com.smparkworld.discord.feature.bee.commands.BeeMusicPlayLeaveCommandHandler
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData

class BeeBot(
    getVoiceChannelByEventAuthor: GetVoiceChannelByEventAuthorUseCase = GetVoiceChannelByEventAuthorUseCaseImpl(),
    saveSingleMessagePerGuildUseCase: SaveSingleMessagePerGuildUseCase = SaveSingleMessagePerGuildUseCaseImpl(MessageRepositoryImpl),
    getSingleMessagePerGuildUseCase: GetSingleMessagePerGuildUseCase = GetSingleMessagePerGuildUseCaseImpl(MessageRepositoryImpl)
) : DiscordBot() {

    override val commandHandlers = mapOf(
        getString(StringCode.BEE_CMD_HELP) to BeeHelpCommandHandler(),
        getString(StringCode.BEE_CMD_FORCE_MOVE_USER) to BeeForceMoveUserCommandHandler(getVoiceChannelByEventAuthor),
        getString(StringCode.BEE_CMD_MUSIC_PLAY_BY_SEARCH) to BeeMusicPlayBySearchCommandHandler(getVoiceChannelByEventAuthor, saveSingleMessagePerGuildUseCase, getSingleMessagePerGuildUseCase),
        getString(StringCode.BEE_CMD_MUSIC_PLAY_LEAVE) to BeeMusicPlayLeaveCommandHandler(getVoiceChannelByEventAuthor)
    )

    override fun applyCommandData(commandData: SlashCommandData) {
        commandData.addSubcommands(
            // 1. 기능 및 명령어 도움말
            SubcommandData(getString(StringCode.BEE_CMD_HELP), getString(StringCode.BEE_CMD_HELP_DESC)),

            // 2. 유저 강제로 데려오기
            SubcommandData(getString(StringCode.BEE_CMD_FORCE_MOVE_USER), getString(StringCode.BEE_CMD_FORCE_MOVE_USER_DESC))
                .addOption(OptionType.USER, getString(StringCode.TARGET_USER), getString(StringCode.TARGET_USER_DESC)),

            // 3. 검색해서 노래 재생하기
            SubcommandData(getString(StringCode.BEE_CMD_MUSIC_PLAY_BY_SEARCH), getString(StringCode.BEE_CMD_MUSIC_PLAY_BY_SEARCH_DESC)),

            // 4. 봇 퇴장시키기
            SubcommandData(getString(StringCode.BEE_CMD_MUSIC_PLAY_LEAVE), getString(StringCode.BEE_CMD_MUSIC_PLAY_LEAVE_DESC)),
        )
    }
}