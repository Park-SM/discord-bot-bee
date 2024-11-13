package com.smparkworld.discord.bot.valorant.commands

import com.smparkworld.discord.base.StringCode
import com.smparkworld.discord.base.StringsParser
import com.smparkworld.discord.base.StringsParser.getString
import com.smparkworld.discord.bot.CommandHandler
import com.smparkworld.discord.usecase.GetVoiceChannelUsersByEventAuthorUseCase
import com.smparkworld.discord.bot.valorant.ValorantAgentType
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import java.util.*

class ValorantRandomPickHardCommandHandler(
    private val getVoiceChannelUsersByMember: GetVoiceChannelUsersByEventAuthorUseCase
) : CommandHandler() {

    override fun handle(command: String, event: SlashCommandInteractionEvent) {
        checkAudioChannelValidation(event) { members ->

            val ignores: List<User> = obtainIgnoredUsers(event)
            val players: List<String> = obtainPlayerNames(members, ignores)

            checkPlayersValidation(event, players) {
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
                                candidates.add(wildcard to getString(StringCode.WILDCARD))
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
                    .setTitle(getString(StringCode.VAL_RANDOM_PICK_HARD_TITLE))
                    .setDescription(getString(StringCode.VAL_RANDOM_PICK_HARD_DESCRIPTION))
                    .addField(getString(StringCode.NAME), playersValue, true)
                    .addField("", "→\n".repeat(players.size), true)
                    .addField(getString(StringCode.VAL_AGENT), agentTypeValues, true)
                    .apply {
                        if (ignores.isNotEmpty()) addField(getString(StringCode.IGNORED_USER), ignoresValue, false)
                    }
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
            is GetVoiceChannelUsersByEventAuthorUseCase.Result.Success -> {
                perform.invoke(result.members)
            }
            is GetVoiceChannelUsersByEventAuthorUseCase.Result.NotInVoiceChannel -> {
                val message = EmbedBuilder()
                    .setDescription(getString(StringCode.VAL_ABSENT_COMMAND_AUTHOR))
                    .build()
                event.replyEmbeds(message).queue()
            }
            is GetVoiceChannelUsersByEventAuthorUseCase.Result.Error -> {
                val message = EmbedBuilder()
                    .setDescription(getString(StringCode.UNKNOWN_EXCEPTION))
                    .build()
                event.replyEmbeds(message).queue()
            }
        }
    }

    private fun checkPlayersValidation(
        event: SlashCommandInteractionEvent,
        players: List<String>,
        perform: () -> Unit
    ) {
        when {
            (players.isEmpty()) -> {
                val message = EmbedBuilder()
                    .setDescription(getString(StringCode.VAL_RANDOM_PICK_CANDIDATE_NEED_TO_MORE))
                    .build()
                event.replyEmbeds(message).queue()
            }
            (players.size > 5) -> {
                val message = EmbedBuilder()
                    .setDescription(getString(StringCode.VAL_RANDOM_PICK_CANDIDATE_TOO_MUCH))
                    .build()
                event.replyEmbeds(message).queue()
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

    private fun obtainPlayerNames(members: List<Member>, ignores: List<User>): List<String> {
        return members
            .filterNot { ignores.contains(it.user) }
            .filterNot { it.user.isBot }
            .filterNot { it.user.isSystem }
            .map { it.user.globalName ?: it.user.name }
    }
}