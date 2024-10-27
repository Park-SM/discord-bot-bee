package com.smparkworld.discord.bot.valorant.commands

import com.smparkworld.discord.base.StringCode
import com.smparkworld.discord.base.StringsParser.getString
import com.smparkworld.discord.bot.CommandHandler
import com.smparkworld.discord.usecase.GetAudioChannelUsersByEventAuthorUseCase
import com.smparkworld.discord.bot.valorant.ValorantMapType
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import java.util.*

class ValorantRandomMapCommandHandler(
    private val getVoiceChannelUsersByMember: GetAudioChannelUsersByEventAuthorUseCase
) : CommandHandler {

    override fun handle(command: String, event: SlashCommandInteractionEvent) {
        checkAudioChannelValidation(event) {

            val ignores = listOfNotNull(
                event.getOption(getString(StringCode.IGNORE1))?.asString,
                event.getOption(getString(StringCode.IGNORE2))?.asString,
                event.getOption(getString(StringCode.IGNORE3))?.asString,
                event.getOption(getString(StringCode.IGNORE4))?.asString,
                event.getOption(getString(StringCode.IGNORE5))?.asString,
                event.getOption(getString(StringCode.IGNORE6))?.asString,
                event.getOption(getString(StringCode.IGNORE7))?.asString,
                event.getOption(getString(StringCode.IGNORE8))?.asString,
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
                    .setTitle(getString(StringCode.VAL_RANDOM_MAP_TITLE))
                    .setDescription(getString(StringCode.VAL_RANDOM_MAP_DESCRIPTION))
                    .setImage(map.thumbnailUrl)
                    .addField(getString(StringCode.SELECTED_MAP), "\"${map.typeName}\"", true)
                    .apply {
                        if (ignores.isNotEmpty()) addField(getString(StringCode.IGNORED_MAP), ignoresName, false)
                    }
                    .build()

                event.replyEmbeds(message).queue()
            } else {
                event.reply(getString(StringCode.VAL_RANDOM_MAP_TOO_MUCH_IGNORED)).queue()
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
}