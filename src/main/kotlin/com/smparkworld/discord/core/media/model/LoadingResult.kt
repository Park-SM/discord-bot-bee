package com.smparkworld.discord.core.media.model

sealed interface LoadingResult {

    data class OnTrackLoaded(
        val track: Track
    ) : LoadingResult

    data class OnTracksLoaded(
        val tracks: List<Track>
    ) : LoadingResult
}