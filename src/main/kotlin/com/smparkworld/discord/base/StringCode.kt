package com.smparkworld.discord.base

enum class StringCode(
    val key: String
) {
    NAME("name"),
    IGNORED_USER("ignored_user"),
    WILDCARD("wildcard"),
    SELECTED_MAP("selected_map"),
    IGNORED_MAP("ignored_map"),

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

    UNKNOWN_EXCEPTION("unknown_exception"),

    /* 꿀벌 봇 관련 START */
    BEE_CMD("bee_cmd"),
    BEE_CMD_DESC("bee_cmd_desc"),

    BEE_CMD_HELP("bee_cmd_help"),
    BEE_CMD_HELP_DESC("bee_cmd_help_desc"),

    BEE_CMD_HELP_QUESTION("bee_cmd_help_question"),
    BEE_CMD_HELP_QUESTION_DESC("bee_cmd_help_question_desc"),

    BEE_CMD_HELP_FOR_BEE("bee_cmd_help_for_bee"),
    BEE_CMD_HELP_FOR_BEE_DESC("bee_cmd_help_for_bee_desc"),
    BEE_CMD_HELP_FOR_BEE_HELP_EXAMPLE("bee_cmd_help_for_bee_help_example"),

    BEE_CMD_HELP_FOR_VAL("bee_cmd_help_for_val"),
    BEE_CMD_HELP_FOR_VAL_DESC("bee_cmd_help_for_val_desc"),
    BEE_CMD_HELP_FOR_VAL_RANDOM_PICK_EXAMPLE("bee_cmd_help_for_val_random_pick_example"),
    BEE_CMD_HELP_FOR_VAL_RANDOM_PICK_HARD_EXAMPLE("bee_cmd_help_for_val_random_pick_hard_example"),
    BEE_CMD_HELP_FOR_VAL_RANDOM_MAP_EXAMPLE("bee_cmd_help_for_val_random_map_example"),
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

    VAL_AGENT("val_agent"),
    VAL_AGENT_TYPE("val_agent_type"),

    VAL_ABSENT_COMMAND_AUTHOR("val_absent_command_author"),
    VAL_RANDOM_PICK_CANDIDATE_NEED_TO_MORE("val_random_pick_candidate_need_to_more"),
    VAL_RANDOM_PICK_CANDIDATE_TOO_MUCH("val_random_pick_candidate_too_much"),

    VAL_RANDOM_PICK_TITLE("val_random_pick_title"),
    VAL_RANDOM_PICK_DESCRIPTION("val_random_pick_description"),

    VAL_RANDOM_PICK_HARD_TITLE("val_random_pick_hard_title"),
    VAL_RANDOM_PICK_HARD_DESCRIPTION("val_random_pick_hard_description"),

    VAL_RANDOM_MAP_TITLE("val_random_map_title"),
    VAL_RANDOM_MAP_DESCRIPTION("val_random_map_description"),
    VAL_RANDOM_MAP_TOO_MUCH_IGNORED("val_random_map_too_much_ignored"),
    /* 발로란트 봇 관련 END */
}