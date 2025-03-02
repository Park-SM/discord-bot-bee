package com.smparkworld.discord.core.media.model

sealed interface LoadingFailureReason {

    object NoMatches : LoadingFailureReason

    data class Error(
        val exception: Exception
    ) : LoadingFailureReason

    object Unknown : LoadingFailureReason
}