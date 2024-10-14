package com.smparkworld.discord.bot.valorant

import com.smparkworld.discord.bot.DiscordBot
import com.smparkworld.discord.bot.usecase.GetAudioChannelUsersByEventAuthor
import com.smparkworld.discord.bot.usecase.GetAudioChannelUsersByEventAuthor.Result
import com.smparkworld.discord.bot.usecase.GetAudioChannelUsersByEventAuthorImpl
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import java.util.*

class ValorantBot(
    private val getVoiceChannelUsersByMember: GetAudioChannelUsersByEventAuthor = GetAudioChannelUsersByEventAuthorImpl()
) : DiscordBot() {

    override fun onSlashCommand(command: String, event: SlashCommandInteractionEvent) {
        when (event.subcommandName) {
            SUBCOMMAND_RANDOM_PICK -> {
                val ignores = listOfNotNull(
                    event.getOption("ignore-1")?.asUser,
                    event.getOption("ignore-2")?.asUser,
                    event.getOption("ignore-3")?.asUser,
                    event.getOption("ignore-4")?.asUser,
                    event.getOption("ignore-5")?.asUser
                )

                when (val result = getVoiceChannelUsersByMember(event)) {
                    is Result.Success -> {

                        val players = result.members
                            .filterNot { ignores.contains(it.user) }
                            .filterNot { it.user.isBot }
                            .filterNot { it.user.isSystem }
                            .map { it.user.globalName }

                        when {
                            (players.isEmpty()) -> {
                                event.reply("랜덤픽을 돌리기 위한 플레이어가 부족해요! 제외된 ${ignores.size}명을 다시 조정해주세요.").queue()
                            }
                            (players.size > 5) -> {
                                event.reply("채널에 있는 유저가 5명을 초과해요! 최대 5명이 되도록 일부 유저를 제외해주세요.").queue()
                            }
                            else -> {
                                val agentTypeValues = StringBuilder()
                                    .also { builder ->
                                        ValorantAgentType.values()
                                            .map { it.typeName }
                                            .also(Collections::shuffle)
                                            .subList(0, players.size)
                                            .forEach { builder.append("$it\n") }
                                    }
                                    .toString()

                                val playersValue = StringBuilder()
                                    .also { builder -> players.forEach { builder.append("$it\n") } }
                                    .toString()

                                val ignoresValue = StringBuilder()
                                    .also { builder -> ignores.forEach { builder.append("${it.globalName ?: it.name}\n") } }
                                    .toString()

                                val message = EmbedBuilder()
                                    .setTitle("발로란트 에이전트 역할군 랜덤픽")
                                    .setDescription("음성 채널에 있는 유저들에게 에이전트의 역할군을 무작위로 지정합니다.")
                                    .addField("이름", playersValue, true)
                                    .addField("", "→\n".repeat(players.size), true)
                                    .addField("역할군", agentTypeValues, true)
                                    .apply {
                                        if (ignores.isNotEmpty()) addField("제외된 유저", ignoresValue, false)
                                    }
                                    .build()

                                event.replyEmbeds(message).queue()
                            }
                        }
                    }
                    is Result.NotInVoiceChannel -> {
                        event.reply("음성 채널에 없으시군요. 음성 채널 안에서 명령해주세요!").queue()
                    }
                    is Result.Error -> {
                        event.reply("알 수 없는 문제가 발생했어요! DM으로 `Park#3309`한테 문의해주세요!").queue()
                    }
                }
            }
            SUBCOMMAND_RANDOM_PICK_HARD -> {
                event.reply("아직 준비 중이에요.").queue()
            }
        }
    }

    override fun applyCommandData(commandData: SlashCommandData) {
        commandData.addSubcommands(
            SubcommandData(SUBCOMMAND_RANDOM_PICK, SUBCOMMAND_RANDOM_PICK_DESC)
                .addOption(OptionType.USER, "ignore-1", "채널 내 제외할 유저를 입력합니다.", false)
                .addOption(OptionType.USER, "ignore-2", "채널 내 제외할 유저를 입력합니다.", false)
                .addOption(OptionType.USER, "ignore-3", "채널 내 제외할 유저를 입력합니다.", false)
                .addOption(OptionType.USER, "ignore-4", "채널 내 제외할 유저를 입력합니다.", false)
                .addOption(OptionType.USER, "ignore-5", "채널 내 제외할 유저를 입력합니다.", false),
            SubcommandData(SUBCOMMAND_RANDOM_PICK_HARD, SUBCOMMAND_RANDOM_PICK_HARD_DESC)
                .addOption(OptionType.USER, "ignore-1", "채널 내 제외할 유저를 입력합니다.", false)
                .addOption(OptionType.USER, "ignore-2", "채널 내 제외할 유저를 입력합니다.", false)
                .addOption(OptionType.USER, "ignore-3", "채널 내 제외할 유저를 입력합니다.", false)
                .addOption(OptionType.USER, "ignore-4", "채널 내 제외할 유저를 입력합니다.", false)
                .addOption(OptionType.USER, "ignore-5", "채널 내 제외할 유저를 입력합니다.", false)
        )
    }

    companion object {

        private const val SUBCOMMAND_RANDOM_PICK = "random-pick"
        private const val SUBCOMMAND_RANDOM_PICK_DESC = "음성 채널에 있는 유저들에게 에이전트의 역할군을 무작위로 지정합니다."

        private const val SUBCOMMAND_RANDOM_PICK_HARD = "random-pick-hard"
        private const val SUBCOMMAND_RANDOM_PICK_HARD_DESC = "음성 채널에 있는 유저들에게 에이전트를 무작위로 지정합니다."
    }
}