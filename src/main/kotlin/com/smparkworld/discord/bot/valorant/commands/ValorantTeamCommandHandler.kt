package com.smparkworld.discord.bot.valorant.commands

import com.smparkworld.discord.base.StringCode
import com.smparkworld.discord.base.StringsParser.getString
import com.smparkworld.discord.bot.CommandHandler
import com.smparkworld.discord.usecase.GetAudioChannelUsersByEventAuthorUseCase
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import java.util.Collections

class ValorantTeamCommandHandler(
    private val getVoiceChannelUsersByMember: GetAudioChannelUsersByEventAuthorUseCase
) : CommandHandler() {

    override fun handle(command: String, event: SlashCommandInteractionEvent) {
        checkAudioChannelValidation(event) { members ->

            val ignores: List<User> = obtainIgnoredUsers(event)
            val players: List<Member> = obtainPlayers(members, ignores)

            checkPlayersValidation(event, players) {
                val guild = event.guild
                    ?: throw IllegalStateException("Not found guild")

                // 1. 팀 생성 및 팀원 구성
                val (playerGroupA, playerGroupB) = players
                    .also(Collections::shuffle)
                    .let(::splitList)

                val playerGroupANames = StringBuilder()
                    .also { builder -> players.forEach { builder.append("${it.user.globalName ?: it.user.name }\n") } }
                    .toString()

                val playerGroupBNames = StringBuilder()
                    .also { builder -> players.forEach { builder.append("${it.user.globalName ?: it.user.name }\n") } }
                    .toString()

                // 2. 두 개의 새로운 음성 채널 생성 및 팀원 이동
                guild.createVoiceChannel(getString(StringCode.VAL_TEAM_AUDIO_CHANNEL_NAME_A)).queue { channel ->
                    playerGroupA.forEach { player ->
                        guild.moveVoiceMember(player, channel).queue()
                    }
                }
                guild.createVoiceChannel(getString(StringCode.VAL_TEAM_AUDIO_CHANNEL_NAME_B)).queue {channel ->
                    playerGroupB.forEach { player ->
                        guild.moveVoiceMember(player, channel).queue()
                    }
                }

                val message = EmbedBuilder()
                    .setTitle(getString(StringCode.VAL_TEAM_RESULT_TITLE))
                    .setDescription(getString(StringCode.VAL_TEAM_RESULT_DESC))
                    .addField(getString(StringCode.VAL_TEAM_RESULT_GROUP_A), playerGroupANames, true)
                    .addField(getString(StringCode.VAL_TEAM_RESULT_GROUP_B), playerGroupBNames, true)
                    .build()
                event.replyEmbeds(message).queue()
            }
        }
    }

    private fun checkAudioChannelValidation(
        event: SlashCommandInteractionEvent,
        perform: (members: List<Member>) -> Unit
    ) {
        when (val result = getVoiceChannelUsersByMember(event)) {
            is GetAudioChannelUsersByEventAuthorUseCase.Result.Success -> {
                perform.invoke(result.members)
            }
            is GetAudioChannelUsersByEventAuthorUseCase.Result.NotInVoiceChannel -> {
                event.reply(getString(StringCode.VAL_ABSENT_COMMAND_AUTHOR)).queue()
            }
            is GetAudioChannelUsersByEventAuthorUseCase.Result.Error -> {
                event.reply(getString(StringCode.UNKNOWN_EXCEPTION)).queue()
            }
        }
    }

    private fun checkPlayersValidation(
        event: SlashCommandInteractionEvent,
        players: List<Member>,
        perform: () -> Unit
    ) {
        when {
            (players.size < 2) -> {
                event.reply(getString(StringCode.VAL_TEAM_CANDIDATE_NEED_TO_MORE)).queue()
            }
            (players.size > 10) -> {
                event.reply(getString(StringCode.VAL_TEAM_CANDIDATE_TOO_MUCH)).queue()
            }
            else -> perform.invoke()
        }
    }

    private fun obtainIgnoredUsers(event: SlashCommandInteractionEvent): List<User> {
        return listOfNotNull(
            event.getOption(getString(StringCode.IGNORE1))?.asUser,
            event.getOption(getString(StringCode.IGNORE2))?.asUser,
            event.getOption(getString(StringCode.IGNORE3))?.asUser,
            event.getOption(getString(StringCode.IGNORE4))?.asUser,
            event.getOption(getString(StringCode.IGNORE5))?.asUser,
        )
    }

    private fun obtainPlayers(members: List<Member>, ignores: List<User>): List<Member> {
        return members
            .filterNot { ignores.contains(it.user) }
            .filterNot { it.user.isBot }
            .filterNot { it.user.isSystem }
    }

    private fun <T> splitList(list: List<T>): Pair<List<T>, List<T>> {
        val midpoint = list.size / 2
        val firstHalf = list.take(midpoint)
        val secondHalf = list.drop(midpoint)
        return Pair(firstHalf, secondHalf)
    }
}