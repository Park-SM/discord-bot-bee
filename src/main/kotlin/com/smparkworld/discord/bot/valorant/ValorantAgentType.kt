package com.smparkworld.discord.bot.valorant

enum class ValorantAgentType(
    val typeName: String,
    val agentNames: List<String>
){
    // 타격대
    DUELIST(
        typeName = "타격대",
        agentNames = listOf("네온", "레이나", "레이즈", "아이소", "요루", "제트", "피닉스")
    ),

    // 척후대
    INITIATOR(
        typeName = "척후대",
        agentNames = listOf("게코", "브리치", "소바", "스카이", "케이/오", "페이드")
    ),

    // 감시자
    SENTINEL(
        typeName = "감시자",
        agentNames = listOf("데드록", "바이스", "사이퍼", "세이지", "체임버", "킬조이")
    ),

    // 전략가
    CONTROLLER(
        typeName = "전략가",
        agentNames = listOf("바이퍼", "브림스톤", "아스트라", "오멘", "클로브", "하버")
    )
    ;

    companion object {
        val AGENT_ALL =  DUELIST.agentNames + INITIATOR.agentNames + SENTINEL.agentNames + CONTROLLER.agentNames
    }
}