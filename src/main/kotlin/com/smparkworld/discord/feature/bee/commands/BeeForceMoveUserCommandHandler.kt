package com.smparkworld.discord.feature.bee.commands

import com.smparkworld.discord.common.base.StringCode
import com.smparkworld.discord.common.base.StringsParser.getString
import com.smparkworld.discord.common.extensions.checkVoiceChannelValidation
import com.smparkworld.discord.common.extensions.requireGuild
import com.smparkworld.discord.common.extensions.sendNoticeEmbedsMessage
import com.smparkworld.discord.common.framework.CommandHandler
import com.smparkworld.discord.domain.GetVoiceChannelByEventAuthorUseCase
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class BeeForceMoveUserCommandHandler(
    private val getVoiceChannelByEventAuthor: GetVoiceChannelByEventAuthorUseCase,
) : CommandHandler() {

    override fun handle(command: String, event: SlashCommandInteractionEvent) {
        checkVoiceChannelValidation(event, result = getVoiceChannelByEventAuthor(event)) { channel ->

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

                    event.sendNoticeEmbedsMessage(getString(StringCode.BEE_CMD_FORCE_MOVE_USER_TARGET_USER_SUCCESS, targetName))
                } else {
                    event.sendNoticeEmbedsMessage(getString(StringCode.BEE_CMD_FORCE_MOVE_USER_TARGET_USER_IN_LIMIT, "3분 12초"))
                }
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
                event.sendNoticeEmbedsMessage(getString(StringCode.BEE_CMD_FORCE_MOVE_USER_TARGET_USER_EMPTY))
            }
            (targetVoiceState?.inAudioChannel() != true || targetVoiceState.channel !is VoiceChannel) -> {
                event.sendNoticeEmbedsMessage(getString(StringCode.BEE_CMD_FORCE_MOVE_USER_TARGET_USER_HAS_NOT_CHANNEL))
            }
            else -> {
                perform.invoke(targetMember)
            }
        }
    }
}