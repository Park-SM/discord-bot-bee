package com.smparkworld.discord.bot.bee.commands

import com.smparkworld.discord.base.StringCode
import com.smparkworld.discord.base.StringsParser.getString
import com.smparkworld.discord.bot.CommandHandler
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu

class BeeHelpCommandHandler : CommandHandler() {

    override fun handle(command: String, event: SlashCommandInteractionEvent) {
        val menu = StringSelectMenu.create(COMPONENT_ID)
            .addOption(getString(StringCode.BEE_CMD), getString(StringCode.BEE_CMD), getString(StringCode.BEE_CMD_DESC))
            .addOption(getString(StringCode.VAL_CMD), getString(StringCode.VAL_CMD), getString(StringCode.VAL_CMD_DESC))
            .build()

        val message = EmbedBuilder()
            .setTitle(getString(StringCode.BEE_CMD_HELP_QUESTION))
            .setDescription(getString(StringCode.BEE_CMD_HELP_QUESTION_DESC))
            .build()

        event.replyEmbeds(message)
            .addActionRow(menu)
            .queue()
    }

    override fun handleInteractionByStringSelectMenu(event: StringSelectInteractionEvent) {
        if (event.componentId != COMPONENT_ID) return

        when (event.selectedOptions.firstOrNull()?.value) {
            getString(StringCode.BEE_CMD) -> {
                val message = EmbedBuilder()
                    .setTitle(getString(StringCode.BEE_CMD_HELP_FOR_BEE))
                    .setDescription(getString(StringCode.BEE_CMD_HELP_FOR_BEE_DESC))
                    .addBlankField(false)
                    .addField(HELP_PREFIX + getString(StringCode.BEE_CMD_HELP), getString(StringCode.BEE_CMD_HELP_FOR_BEE_HELP_EXAMPLE), false)
                    .addBlankField(false)
                    .addField(HELP_PREFIX + getString(StringCode.BEE_CMD_FORCE_MOVE_USER), getString(StringCode.BEE_CMD_HELP_FOR_BEE_FORCE_MOVE_USER_EXAMPLE), false)
                    .addBlankField(false)
                    .addField(HELP_PREFIX + getString(StringCode.BEE_CMD_MUSIC_PLAY_BY_SEARCH), getString(StringCode.BEE_CMD_HELP_FOR_BEE_MUSIC_PLAY_BY_SEARCH_EXAMPLE), false)
                    .addBlankField(false)
                    .addField(HELP_PREFIX + getString(StringCode.BEE_CMD_MUSIC_PLAY_LEAVE), getString(StringCode.BEE_CMD_HELP_FOR_BEE_MUSIC_PLAY_LEAVE_EXAMPLE), false)
                    .build()
                event.replyEmbeds(message).queue()
            }
            getString(StringCode.VAL_CMD) -> {
                val message = EmbedBuilder()
                    .setTitle(getString(StringCode.BEE_CMD_HELP_FOR_VAL))
                    .setDescription(getString(StringCode.BEE_CMD_HELP_FOR_VAL_DESC))
                    .addBlankField(false)
                    .addField(HELP_PREFIX + getString(StringCode.VAL_CMD_RANDOM_PICK), getString(StringCode.BEE_CMD_HELP_FOR_VAL_RANDOM_PICK_EXAMPLE), false)
                    .addBlankField(false)
                    .addField(HELP_PREFIX + getString(StringCode.VAL_CMD_RANDOM_PICK_HARD), getString(StringCode.BEE_CMD_HELP_FOR_VAL_RANDOM_PICK_HARD_EXAMPLE), false)
                    .addBlankField(false)
                    .addField(HELP_PREFIX + getString(StringCode.VAL_CMD_RANDOM_MAP), getString(StringCode.BEE_CMD_HELP_FOR_VAL_RANDOM_MAP_EXAMPLE), false)
                    .addBlankField(false)
                    .addField(HELP_PREFIX + getString(StringCode.VAL_CMD_TEAM), getString(StringCode.BEE_CMD_HELP_FOR_VAL_TEAM_EXAMPLE), false)
                    .build()
                event.replyEmbeds(message).queue()
            }
            else -> {
                val message = EmbedBuilder()
                    .setDescription(getString(StringCode.UNKNOWN_EXCEPTION))
                    .build()
                event.replyEmbeds(message).queue()
            }
        }
    }

    companion object {
        private const val COMPONENT_ID = "menu:question-type"
        private const val HELP_PREFIX = "▸ "
    }
}