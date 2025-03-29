package com.smparkworld.discord.core.media

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame
import com.smparkworld.discord.core.logger.Logger
import com.smparkworld.discord.core.media.MusicManagerMediator.offerTrackHistory
import com.smparkworld.discord.core.media.extensions.loadItem
import com.smparkworld.discord.core.media.model.LavaPlayerTrackImpl
import com.smparkworld.discord.core.media.model.Track
import com.smparkworld.discord.core.media.model.TrackLoadingResult
import net.dv8tion.jda.api.audio.AudioSendHandler
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.managers.AudioManager
import java.nio.ByteBuffer

class GuildMusicManager internal constructor(
    private val guildId: Long,
    private val guildFinder: (guildId: Long) -> Guild?,
    private val playerManager: AudioPlayerManager
) {
    val currentTrack: Track?
        get() = player.playingTrack?.let(::LavaPlayerTrackImpl)

    val isPaused: Boolean
        get() = player.isPaused

    private var _endedAt: Long = 0
    internal val endedAt: Long get() = _endedAt

    private val player = playerManager.createPlayer()
    private val sendHandler = AudioPlayerSendHandler(player)
    private val queue = mutableListOf<AudioTrack>()

    private var isReleased = false
    private var onNextTrackLoaded: (bySkip: Boolean) -> Unit = {}

    init {
        val listener = object : AudioEventAdapter() {
            override fun onTrackException(player: AudioPlayer?, track: AudioTrack?, exception: FriendlyException?) {
                Logger.e(TAG, "An exception occurred while processing the track.", exception)
            }
            override fun onTrackEnd(player: AudioPlayer?, track: AudioTrack?, endReason: AudioTrackEndReason) {
                if (endReason.mayStartNext) { nextTrack() }
            }
        }
        player.addListener(listener)
    }

    fun getAudioManager(): AudioManager? {
        if (isReleased) return null

        return guildFinder(guildId)?.audioManager?.also { manager ->
            manager.sendingHandler = sendHandler
        }
    }

    fun getPlaylist(): List<Track> {
        return queue.map(::LavaPlayerTrackImpl)
    }

    fun setOnNextTrackLoaded(onNextTrackLoaded: (bySkip: Boolean) -> Unit) {
        this.onNextTrackLoaded = onNextTrackLoaded
    }

    suspend fun load(query: String): TrackLoadingResult? {
        if (isReleased) return null

        val isYoutubeUri = query.startsWith(YOUTUBE_URL_PREFIX_1, ignoreCase = true)
                || query.startsWith(YOUTUBE_URL_PREFIX_2, ignoreCase = true)
                || query.startsWith(YOUTUBE_URL_PREFIX_3, ignoreCase = true)

        val decoratedQuery = if (isYoutubeUri) {
            query
        } else {
            "${SEARCH_PREFIX}:${query}"
        }
        return playerManager.loadItem(decoratedQuery)
    }

    fun queue(track: Track) {
        if (isReleased) return

        if (track is LavaPlayerTrackImpl) {
            _endedAt = 0
            offerTrackHistory(guildId, track)
            resumeTrack()

            val isStarted = player.startTrack(track.audioTrack, true)
            if (!isStarted) queue.add(track.audioTrack)
        }
    }

    fun resumeTrack() {
        if (isReleased) return
        player.isPaused = false
    }

    fun pauseTrack() {
        if (isReleased) return
        player.isPaused = true
    }

    fun skipTrack() {
        if (isReleased) return
        player.stopTrack()
        nextTrack(bySkip = true)
    }

    fun release() {
        isReleased = true
        _endedAt = System.currentTimeMillis()

        queue.clear()
        player.destroy()
    }

    private fun nextTrack(bySkip: Boolean = false) {
        if (isReleased) return

        if (queue.isNotEmpty()) {
            player.startTrack(queue.removeFirst(), false)
        } else {
            _endedAt = System.currentTimeMillis()
        }
        onNextTrackLoaded(bySkip)
    }

    companion object {
        private const val TAG = "GuildMusicManager"

        private const val SEARCH_PREFIX = "ytsearch"
        private const val YOUTUBE_URL_PREFIX_1 = "https://youtube.com/"
        private const val YOUTUBE_URL_PREFIX_2 = "https://www.youtube.com/"
        private const val YOUTUBE_URL_PREFIX_3 = "https://music.youtube.com/"
    }
}

private class AudioPlayerSendHandler(
    private val player: AudioPlayer
) : AudioSendHandler {

    private val buffer = ByteBuffer.allocate(1024)

    override fun canProvide(): Boolean =
        player.provide(MutableAudioFrame(buffer))

    override fun provide20MsAudio(): ByteBuffer =
        buffer.flip()

    override fun isOpus(): Boolean = true
}