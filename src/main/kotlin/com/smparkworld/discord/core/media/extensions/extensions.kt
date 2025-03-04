package com.smparkworld.discord.core.media.extensions

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.smparkworld.discord.common.extensions.resumeIfActive
import com.smparkworld.discord.core.media.model.LavaPlayerTrackImpl
import com.smparkworld.discord.core.media.model.TrackLoadingResult
import kotlinx.coroutines.suspendCancellableCoroutine

internal suspend fun AudioPlayerManager.loadItem(query: String): TrackLoadingResult = suspendCancellableCoroutine { continuation ->
    this.loadItem(query, object : AudioLoadResultHandler {
        override fun trackLoaded(track: AudioTrack?) {
            if (track != null) {
                continuation.resumeIfActive(TrackLoadingResult.SuccessTrackLoading(LavaPlayerTrackImpl(track)))
            } else {
                continuation.resumeIfActive(TrackLoadingResult.NoMatches)
            }
        }
        override fun playlistLoaded(playlist: AudioPlaylist?) {
            if (playlist != null) {
                continuation.resumeIfActive(TrackLoadingResult.SuccessTracksLoading(playlist.tracks.map(::LavaPlayerTrackImpl)))
            } else {
                continuation.resumeIfActive(TrackLoadingResult.NoMatches)
            }
        }
        override fun noMatches() {
            continuation.resumeIfActive(TrackLoadingResult.NoMatches)
        }
        override fun loadFailed(exception: FriendlyException?) {
            if (exception != null) {
                continuation.resumeIfActive(TrackLoadingResult.Error(exception))
            } else {
                continuation.resumeIfActive(TrackLoadingResult.UnknownException)
            }
        }
    })
}