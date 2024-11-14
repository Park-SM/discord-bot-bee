package com.smparkworld.discord.bot.bee.commands

import com.smparkworld.discord.base.StringCode
import com.smparkworld.discord.base.StringsParser.getString
import com.smparkworld.discord.bot.CommandHandler
import com.smparkworld.discord.extensions.requireGuild
import com.smparkworld.discord.usecase.GetVoiceChannelByEventAuthorUseCase
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class BeeForceMoveUserCommandHandler(
    private val getVoiceChannelByEventAuthor: GetVoiceChannelByEventAuthorUseCase,
) : CommandHandler() {

    override fun handle(command: String, event: SlashCommandInteractionEvent) {
        checkVoiceChannelValidation(event) { channel ->

            val targetUser = event.getOption(getString(StringCode.TARGET_USER))?.asUser

            checkTargetMemberVoiceChannelValidation(event, targetUser) { targetMember ->

                /* Spec.
                 *  1. TargetUser가 봇이면 항상 명령어 호출자가 있는 음성 채널로 데려올 수 있다.
                 *  2. TargetUser가 일반 유저일 때는 제한 시간 3분을 기준으로 한 번만 데려올 수 있다.
                 */
                val canForceMoveUserToAuthorChannel = true   // 추후 작업하기
                if (canForceMoveUserToAuthorChannel) {

                    event.requireGuild().moveVoiceMember(targetMember, channel).queue()

                    val targetName = targetMember.user.globalName
                        ?: targetMember.user.name

                    val message = EmbedBuilder()
                        .setDescription(getString(StringCode.BEE_CMD_FORCE_MOVE_USER_TARGET_USER_SUCCESS, targetName))
                        .build()
                    event.replyEmbeds(message).queue()
                } else {
                    val message = EmbedBuilder()
                        .setDescription(getString(StringCode.BEE_CMD_FORCE_MOVE_USER_TARGET_USER_IN_LIMIT, "3분 12초"))
                        .build()
                    event.replyEmbeds(message).queue()
                }
            }
        }
    }

    private fun checkVoiceChannelValidation(
        event: SlashCommandInteractionEvent,
        perform: (channel: VoiceChannel) -> Unit
    ) {
        when (val result = getVoiceChannelByEventAuthor(event)) {
            is GetVoiceChannelByEventAuthorUseCase.Result.Success -> {
                perform.invoke(result.voiceChannel)
            }
            is GetVoiceChannelByEventAuthorUseCase.Result.NotInVoiceChannel -> {
                val message = EmbedBuilder()
                    .setDescription(getString(StringCode.ABSENT_COMMAND_AUTHOR))
                    .build()
                event.replyEmbeds(message).queue()
            }
            is GetVoiceChannelByEventAuthorUseCase.Result.Error -> {
                val message = EmbedBuilder()
                    .setDescription(getString(StringCode.UNKNOWN_EXCEPTION))
                    .build()
                event.replyEmbeds(message).queue()
            }
        }
    }

    private fun checkTargetMemberVoiceChannelValidation(
        event: SlashCommandInteractionEvent,
        targetUser: User?,
        perform: (targetMember: Member) -> Unit
    ) {
        val guild = event.requireGuild()
        val targetMember = targetUser?.id?.let(guild::getMemberById)
        val targetVoiceState = targetMember?.voiceState

        when {
            (targetMember == null) -> {
                val message = EmbedBuilder()
                    .setDescription(getString(StringCode.BEE_CMD_FORCE_MOVE_USER_TARGET_USER_EMPTY))
                    .build()
                event.replyEmbeds(message).queue()
            }
            (targetVoiceState?.inAudioChannel() != true || targetVoiceState.channel !is VoiceChannel) -> {
                val message = EmbedBuilder()
                    .setDescription(getString(StringCode.BEE_CMD_FORCE_MOVE_USER_TARGET_USER_HAS_NOT_CHANNEL))
                    .build()
                event.replyEmbeds(message).queue()
            }
            else -> {
                perform.invoke(targetMember)
            }
        }
    }
}