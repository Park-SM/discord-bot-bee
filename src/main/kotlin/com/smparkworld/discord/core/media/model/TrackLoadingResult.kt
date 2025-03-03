package com.smparkworld.discord.core.media.model

sealed interface TrackLoadingResult {

    data class SuccessTrackLoading(
        val track: Track
    ) : TrackLoadingResult

    data class SuccessTracksLoading(
        val tracks: List<Track>
    ) : TrackLoadingResult

    data class Error(
        val exception: Exception
    ) : TrackLoadingResult

    object NoMatches : TrackLoadingResult

    object UnknownException : TrackLoadingResult
}