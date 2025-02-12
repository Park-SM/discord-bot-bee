package com.smparkworld.discord.base

enum class StringCode(
    val key: String
) {
    NAME("name"),
    IGNORED_USER("ignored_user"),
    WILDCARD("wildcard"),
    SELECTED_MAP("selected_map"),
    IGNORED_MAP("ignored_map"),
    STATUS("status"),

    MUSIC_KEYWORD("music_keyword"),
    MUSIC_KEYWORD_DESC("music_keyword_desc"),

    TARGET_USER("target_user"),
    TARGET_USER_DESC("target_user_desc"),

    IGNORE1("ignore1"),
    IGNORE2("ignore2"),
    IGNORE3("ignore3"),
    IGNORE4("ignore4"),
    IGNORE5("ignore5"),
    IGNORE6("ignore6"),
    IGNORE7("ignore7"),
    IGNORE8("ignore8"),

    IGNORE_USER_DESC("ignore_user_desc"),
    IGNORE_MAP_DESC("ignore_map_desc"),

    ABSENT_COMMAND_AUTHOR("absent_command_author"),
    UNKNOWN_EXCEPTION("unknown_exception"),

    /* 꿀벌 봇 관련 START */
    BEE_CMD("bee_cmd"),
    BEE_CMD_DESC("bee_cmd_desc"),

    BEE_CMD_HELP("bee_cmd_help"),
    BEE_CMD_HELP_DESC("bee_cmd_help_desc"),

    BEE_CMD_FORCE_MOVE_USER("bee_cmd_force_move_user"),
    BEE_CMD_FORCE_MOVE_USER_DESC("bee_cmd_force_move_user_desc"),
    BEE_CMD_FORCE_MOVE_USER_TARGET_USER_EMPTY("bee_cmd_force_move_user_target_user_empty"),
    BEE_CMD_FORCE_MOVE_USER_TARGET_USER_HAS_NOT_CHANNEL("bee_cmd_force_move_user_target_user_has_not_channel"),
    BEE_CMD_FORCE_MOVE_USER_TARGET_USER_IN_LIMIT("bee_cmd_force_move_user_target_user_in_limit"),
    BEE_CMD_FORCE_MOVE_USER_TARGET_USER_SUCCESS("bee_cmd_force_move_user_target_user_success"),

    BEE_CMD_MUSIC_PLAY_BY_SEARCH("bee_cmd_music_play_by_search"),
    BEE_CMD_MUSIC_PLAY_BY_SEARCH_DESC("bee_cmd_music_play_by_search_desc"),
    BEE_CMD_MUSIC_PLAY_INPUT_EMPTY("bee_cmd_music_play_input_empty"),
    BEE_CMD_MUSIC_PLAY_NOT_FOUND("bee_cmd_music_play_not_found"),

    BEE_CMD_MUSIC_PLAY_LEAVE("bee_cmd_music_play_leave"),
    BEE_CMD_MUSIC_PLAY_LEAVE_DESC("bee_cmd_music_play_leave_desc"),
    BEE_CMD_MUSIC_PLAY_LEAVE_MSG_1("bee_cmd_music_play_leave_msg_1"),
    BEE_CMD_MUSIC_PLAY_LEAVE_MSG_2("bee_cmd_music_play_leave_msg_2"),
    BEE_CMD_MUSIC_PLAY_LEAVE_MSG_3("bee_cmd_music_play_leave_msg_3"),

    BEE_CMD_MUSIC_PLAY_TITLE("bee_cmd_music_play_title"),
    BEE_CMD_MUSIC_PLAY_DESC("bee_cmd_music_play_desc"),
    BEE_CMD_MUSIC_PLAY_PUT_PLAYLIST_DESC("bee_cmd_music_play_put_playlist_desc"),
    BEE_CMD_MUSIC_PLAY_CURRENT_TITLE("bee_cmd_music_play_current_title"),
    BEE_CMD_MUSIC_PLAY_CURRENT_TITLE_EXCEPTION("bee_cmd_music_play_current_title_exception"),
    BEE_CMD_MUSIC_PLAY_PLAYLIST("bee_cmd_music_play_playlist"),

    BEE_CMD_HELP_QUESTION("bee_cmd_help_question"),
    BEE_CMD_HELP_QUESTION_DESC("bee_cmd_help_question_desc"),

    BEE_CMD_HELP_FOR_BEE("bee_cmd_help_for_bee"),
    BEE_CMD_HELP_FOR_BEE_DESC("bee_cmd_help_for_bee_desc"),

    BEE_CMD_HELP_FOR_VAL("bee_cmd_help_for_val"),
    BEE_CMD_HELP_FOR_VAL_DESC("bee_cmd_help_for_val_desc"),

    BEE_CMD_HELP_FOR_BEE_HELP_EXAMPLE("bee_cmd_help_for_bee_help_example"),
    BEE_CMD_HELP_FOR_BEE_FORCE_MOVE_USER_EXAMPLE("bee_cmd_help_for_bee_force_move_user_example"),
    BEE_CMD_HELP_FOR_BEE_MUSIC_PLAY_BY_SEARCH_EXAMPLE("bee_cmd_help_for_bee_music_play_by_search_example"),
    BEE_CMD_HELP_FOR_BEE_MUSIC_PLAY_LEAVE_EXAMPLE("bee_cmd_help_for_bee_music_play_leave_example"),
    BEE_CMD_HELP_FOR_VAL_RANDOM_PICK_EXAMPLE("bee_cmd_help_for_val_random_pick_example"),
    BEE_CMD_HELP_FOR_VAL_RANDOM_PICK_HARD_EXAMPLE("bee_cmd_help_for_val_random_pick_hard_example"),
    BEE_CMD_HELP_FOR_VAL_RANDOM_MAP_EXAMPLE("bee_cmd_help_for_val_random_map_example"),
    BEE_CMD_HELP_FOR_VAL_TEAM_EXAMPLE("bee_cmd_help_for_val_team_example"),
    /* 꿀벌 봇 관련 END */

    /* 발로란트 봇 관련 START */
    VAL_CMD("val_cmd"),
    VAL_CMD_DESC("val_cmd_desc"),

    VAL_CMD_RANDOM_PICK("val_cmd_random_pick"),
    VAL_CMD_RANDOM_PICK_DESC("val_cmd_random_pick_desc"),

    VAL_CMD_RANDOM_PICK_HARD("val_cmd_random_pick_hard"),
    VAL_CMD_RANDOM_PICK_HARD_DESC("val_cmd_random_pick_hard_desc"),

    VAL_CMD_RANDOM_MAP("val_cmd_random_map"),
    VAL_CMD_RANDOM_MAP_DESC("val_cmd_random_map_desc"),

    VAL_CMD_TEAM("val_cmd_team"),
    VAL_CMD_TEAM_DESC("val_cmd_team_desc"),

    VAL_AGENT("val_agent"),
    VAL_AGENT_TYPE("val_agent_type"),

    VAL_RANDOM_PICK_CANDIDATE_NEED_TO_MORE("val_random_pick_candidate_need_to_more"),
    VAL_RANDOM_PICK_CANDIDATE_TOO_MUCH("val_random_pick_candidate_too_much"),

    VAL_RANDOM_PICK_TITLE("val_random_pick_title"),
    VAL_RANDOM_PICK_DESCRIPTION("val_random_pick_description"),

    VAL_RANDOM_PICK_HARD_TITLE("val_random_pick_hard_title"),
    VAL_RANDOM_PICK_HARD_DESCRIPTION("val_random_pick_hard_description"),

    VAL_RANDOM_MAP_TITLE("val_random_map_title"),
    VAL_RANDOM_MAP_DESCRIPTION("val_random_map_description"),
    VAL_RANDOM_MAP_TOO_MUCH_IGNORED("val_random_map_too_much_ignored"),

    VAL_TEAM_CANDIDATE_NEED_TO_MORE("val_team_candidate_need_to_more"),
    VAL_TEAM_CANDIDATE_TOO_MUCH("val_team_candidate_too_much"),

    VAL_TEAM_AUDIO_CHANNEL_NAME("val_team_audio_channel_name"),
    VAL_TEAM_AUDIO_CHANNEL_NAME_A("val_team_audio_channel_name_a"),
    VAL_TEAM_AUDIO_CHANNEL_NAME_B("val_team_audio_channel_name_b"),

    VAL_TEAM_RESULT_TITLE("val_team_result_title"),
    VAL_TEAM_RESULT_DESC("val_team_result_desc"),
    VAL_TEAM_RESULT_GROUP_A("val_team_result_group_a"),
    VAL_TEAM_RESULT_GROUP_B("val_team_result_group_b"),

    VAL_TEAM_FINISH_TITLE("val_team_finish_title"),
    VAL_TEAM_FINISH_DESC("val_team_finish_desc"),
    /* 발로란트 봇 관련 END */
}