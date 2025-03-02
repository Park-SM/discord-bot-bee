package com.smparkworld.discord.core.media

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.smparkworld.discord.core.media.model.LavaPlayerTrackImpl
import com.smparkworld.discord.core.media.model.LoadingFailureReason
import com.smparkworld.discord.core.media.model.LoadingResult

internal class MusicResultListenerLavaPlayerAdapter(
    private val listener: MusicResultListener
) : AudioLoadResultHandler {

    override fun trackLoaded(track: AudioTrack?) {
        if (track != null) {
            listener.onLoadSuccess(LoadingResult.OnTrackLoaded(LavaPlayerTrackImpl(track)))
        } else {
            listener.onLoadFailure(LoadingFailureReason.NoMatches)
        }
    }

    override fun playlistLoaded(playlist: AudioPlaylist?) {
        if (playlist != null) {
            listener.onLoadSuccess(LoadingResult.OnTracksLoaded(playlist.tracks.map(::LavaPlayerTrackImpl)))
        } else {
            listener.onLoadFailure(LoadingFailureReason.NoMatches)
        }
    }

    override fun noMatches() {
        listener.onLoadFailure(LoadingFailureReason.NoMatches)
    }

    override fun loadFailed(exception: FriendlyException?) {
        if (exception != null) {
            listener.onLoadFailure(LoadingFailureReason.Error(exception))
        } else {
            listener.onLoadFailure(LoadingFailureReason.Unknown)
        }
    }
}