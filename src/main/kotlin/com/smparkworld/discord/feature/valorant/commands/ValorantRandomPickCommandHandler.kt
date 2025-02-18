package com.smparkworld.discord.feature.valorant.commands

import com.smparkworld.discord.common.base.StringCode
import com.smparkworld.discord.common.base.StringsParser.getString
import com.smparkworld.discord.common.extensions.checkVoiceChannelValidation
import com.smparkworld.discord.common.extensions.sendEmbedsMessage
import com.smparkworld.discord.common.extensions.sendNoticeEmbedsMessage
import com.smparkworld.discord.common.framework.CommandHandler
import com.smparkworld.discord.domain.GetVoiceChannelUsersByEventAuthorUseCase
import com.smparkworld.discord.feature.valorant.ValorantAgentType
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import java.util.*

class ValorantRandomPickCommandHandler(
    private val getVoiceChannelUsersByMember: GetVoiceChannelUsersByEventAuthorUseCase
) : CommandHandler() {

    override fun handle(command: String, event: SlashCommandInteractionEvent) {
        checkVoiceChannelValidation(event, result = getVoiceChannelUsersByMember(event)) { members ->

            val ignores: List<User> = obtainIgnoredUsers(event)
            val players: List<String> = obtainPlayerNames(members, ignores)

            checkPlayersValidation(event, players) {
                val agentTypeValues = StringBuilder()
                    .also { builder ->
                        ValorantAgentType.values()
                            .map { it.typeName }
                            .toMutableList()
                            .also { it.add(getString(StringCode.WILDCARD)) }
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
                    .setTitle(getString(StringCode.VAL_RANDOM_PICK_TITLE))
                    .setDescription(getString(StringCode.VAL_RANDOM_PICK_DESCRIPTION))
                    .addField(getString(StringCode.NAME), playersValue, true)
                    .addField("", "→\n".repeat(players.size), true)
                    .addField(getString(StringCode.VAL_AGENT_TYPE), agentTypeValues, true)
                    .apply {
                        if (ignores.isNotEmpty()) addField(getString(StringCode.IGNORED_USER), ignoresValue, false)
                    }
                    .build()
                event.sendEmbedsMessage(message)
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
                event.sendNoticeEmbedsMessage(getString(StringCode.VAL_RANDOM_PICK_CANDIDATE_NEED_TO_MORE))
            }
            (players.size > 5) -> {
                event.sendNoticeEmbedsMessage(getString(StringCode.VAL_RANDOM_PICK_CANDIDATE_TOO_MUCH))
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