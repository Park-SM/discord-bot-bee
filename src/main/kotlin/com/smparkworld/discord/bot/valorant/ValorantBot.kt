package com.smparkworld.discord.bot.valorant

import com.smparkworld.discord.bot.DiscordBot
import com.smparkworld.discord.bot.usecase.GetAudioChannelUsersByEventAuthorUseCase
import com.smparkworld.discord.bot.usecase.GetAudioChannelUsersByEventAuthorUseCase.Result
import com.smparkworld.discord.bot.usecase.GetAudioChannelUsersByEventAuthorUseCaseImpl
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import java.util.*

class ValorantBot(
    private val getVoiceChannelUsersByMember: GetAudioChannelUsersByEventAuthorUseCase = GetAudioChannelUsersByEventAuthorUseCaseImpl()
) : DiscordBot() {

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
                .addOption(OptionType.USER, "ignore-5", "채널 내 제외할 유저를 입력합니다.", false),
            SubcommandData(SUBCOMMAND_RANDOM_MAP, SUBCOMMAND_RANDOM_MAP_DESC)
                .addOptions(
                    OptionData(OptionType.STRING, "ignore-1", "제외할 맵을 입력합니다.", false)
                        .also { option -> ValorantMapType.values().forEach { option.addChoice(it.typeName, it.name) } },
                    OptionData(OptionType.STRING, "ignore-2", "제외할 맵을 입력합니다.", false)
                        .also { option -> ValorantMapType.values().forEach { option.addChoice(it.typeName, it.name) } },
                    OptionData(OptionType.STRING, "ignore-3", "제외할 맵을 입력합니다.", false)
                        .also { option -> ValorantMapType.values().forEach { option.addChoice(it.typeName, it.name) } },
                    OptionData(OptionType.STRING, "ignore-4", "제외할 맵을 입력합니다.", false)
                        .also { option -> ValorantMapType.values().forEach { option.addChoice(it.typeName, it.name) } },
                    OptionData(OptionType.STRING, "ignore-5", "제외할 맵을 입력합니다.", false)
                        .also { option -> ValorantMapType.values().forEach { option.addChoice(it.typeName, it.name) } },
                    OptionData(OptionType.STRING, "ignore-6", "제외할 맵을 입력합니다.", false)
                        .also { option -> ValorantMapType.values().forEach { option.addChoice(it.typeName, it.name) } },
                    OptionData(OptionType.STRING, "ignore-7", "제외할 맵을 입력합니다.", false)
                        .also { option -> ValorantMapType.values().forEach { option.addChoice(it.typeName, it.name) } },
                    OptionData(OptionType.STRING, "ignore-8", "제외할 맵을 입력합니다.", false)
                        .also { option -> ValorantMapType.values().forEach { option.addChoice(it.typeName, it.name) } }
                )
                ,
        )
    }

    override fun onSlashCommand(command: String, event: SlashCommandInteractionEvent) {
        when (event.subcommandName) {
            SUBCOMMAND_RANDOM_PICK -> handleRandomPickCommand(event)
            SUBCOMMAND_RANDOM_PICK_HARD -> handleRandomPickHardCommand(event)
            SUBCOMMAND_RANDOM_MAP -> handleRandomMap(event)
        }
    }

    private fun handleRandomPickCommand(event: SlashCommandInteractionEvent) {
        checkAudioChannelValidation(event) { members ->

            val ignores: List<User> = obtainIgnoredUsers(event)
            val players: List<String> = obtainPlayerNames(members, ignores)

            checkPlayersValidation(event, players, ignores.size) {
                val agentTypeValues = StringBuilder()
                    .also { builder ->
                        ValorantAgentType.values()
                            .map { it.typeName }
                            .toMutableList()
                            .also { it.add(WILDCARD) }
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

    private fun handleRandomPickHardCommand(event: SlashCommandInteractionEvent) {
        checkAudioChannelValidation(event) { members ->

            val ignores: List<User> = obtainIgnoredUsers(event)
            val players: List<String> = obtainPlayerNames(members, ignores)

            checkPlayersValidation(event, players, ignores.size) {
                val agentTypeValues = StringBuilder()
                    .also { builder ->
                        ValorantAgentType.values()
                            .map { (it.agentNames.random() to it.typeName) }
                            .toMutableList()
                            .also { candidates ->
                                val wildcard = ValorantAgentType.AGENT_ALL
                                    .filterNot { agent -> candidates.find { it.first == agent} != null }
                                    .also(Collections::shuffle)
                                    .first()
                                candidates.add(wildcard to WILDCARD)
                            }
                            .map { "${it.first} (${it.second})" }
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
                    .addField("에이전트", agentTypeValues, true)
                    .apply {
                        if (ignores.isNotEmpty()) addField("제외된 유저", ignoresValue, false)
                    }
                    .build()

                event.replyEmbeds(message).queue()
            }
        }
    }

    private fun handleRandomMap(event: SlashCommandInteractionEvent) {
        checkAudioChannelValidation(event) {

            val ignores = listOfNotNull(
                event.getOption("ignore-1")?.asString,
                event.getOption("ignore-2")?.asString,
                event.getOption("ignore-3")?.asString,
                event.getOption("ignore-4")?.asString,
                event.getOption("ignore-5")?.asString,
                event.getOption("ignore-6")?.asString,
                event.getOption("ignore-7")?.asString,
                event.getOption("ignore-8")?.asString
            )
            val ignoresName = ignores
                .map(ValorantMapType::valueOf)
                .joinToString(", ") { it.typeName }

            val candidates = ValorantMapType.values()
                .filterNot { ignores.contains(it.name) }
                .also(Collections::shuffle)

            if (candidates.isNotEmpty()) {

                val map = candidates.first()

                val message = EmbedBuilder()
                    .setTitle("발로란트 맵 랜덤픽")
                    .setDescription("발로란트에 존재하는 맵 중 하나를 추천합니다.")
                    .setImage(map.thumbnailUrl)
                    .addField("선택된 맵", "\"${map.typeName}\"", true)
                    .apply {
                        if (ignores.isNotEmpty()) addField("제외된 맵", ignoresName, false)
                    }
                    .build()

                event.replyEmbeds(message).queue()
            } else {
                event.reply("맵을 너무 많이 제외시켰어요. 다시 조정해주세요.").queue()
            }
        }
    }

    private fun obtainIgnoredUsers(event: SlashCommandInteractionEvent): List<User> {
        return listOfNotNull(
            event.getOption("ignore-1")?.asUser,
            event.getOption("ignore-2")?.asUser,
            event.getOption("ignore-3")?.asUser,
            event.getOption("ignore-4")?.asUser,
            event.getOption("ignore-5")?.asUser
        )
    }

    private fun obtainPlayerNames(members: List<Member>, ignores: List<User>): List<String> {
        return members
            .filterNot { ignores.contains(it.user) }
            .filterNot { it.user.isBot }
            .filterNot { it.user.isSystem }
            .map { it.user.globalName ?: it.user.name }
    }

    private fun checkAudioChannelValidation(
        event: SlashCommandInteractionEvent,
        perform: (members: List<Member>) -> Unit
    ) {
        when (val result = getVoiceChannelUsersByMember(event)) {
            is Result.Success -> {
                perform.invoke(result.members)
            }
            is Result.NotInVoiceChannel -> {
                event.reply("음성 채널에 없으시군요. 음성 채널 안에서 명령해주세요!").queue()
            }
            is Result.Error -> {
                event.reply("알 수 없는 문제가 발생했어요! DM으로 `Park#3309`한테 문의해주세요!").queue()
            }
        }
    }

    private fun checkPlayersValidation(
        event: SlashCommandInteractionEvent,
        players: List<String>,
        ignoredUserSize: Int,
        perform: () -> Unit
    ) {
        when {
            (players.isEmpty()) -> {
                event.reply("랜덤픽을 돌리기 위한 플레이어가 부족해요! 제외된 ${ignoredUserSize}명을 다시 조정해주세요.").queue()
            }
            (players.size > 5) -> {
                event.reply("채널에 있는 유저가 5명을 초과해요! 최대 5명이 되도록 일부 유저를 제외해주세요.").queue()
            }
            else -> perform.invoke()
        }
    }

    companion object {

        private const val WILDCARD = "와일드 카드"

        private const val SUBCOMMAND_RANDOM_PICK = "random-pick"
        private const val SUBCOMMAND_RANDOM_PICK_DESC = "음성 채널에 있는 유저들에게 에이전트의 역할군을 무작위로 지정합니다."

        private const val SUBCOMMAND_RANDOM_PICK_HARD = "random-pick-hard"
        private const val SUBCOMMAND_RANDOM_PICK_HARD_DESC = "음성 채널에 있는 유저들에게 에이전트를 무작위로 지정합니다."

        private const val SUBCOMMAND_RANDOM_MAP = "random-map"
        private const val SUBCOMMAND_RANDOM_MAP_DESC = "발로란트에 존재하는 맵 중 하나를 추천합니다."
    }
}