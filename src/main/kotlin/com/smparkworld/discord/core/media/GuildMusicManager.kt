package com.smparkworld.discord.core.media

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame
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

    private var _endedAt: Long = 0
    internal val endedAt: Long get() = _endedAt

    private val player = playerManager.createPlayer()
    private val sendHandler = AudioPlayerSendHandler(player)
    private val queue = mutableListOf<AudioTrack>()

    private var onNextTrackLoaded: () -> Unit = {}

    init {
        val listener = object : AudioEventAdapter() {
            override fun onTrackEnd(player: AudioPlayer?, track: AudioTrack?, endReason: AudioTrackEndReason) {
                if (endReason.mayStartNext) { nextTrack() }
            }
        }
        player.addListener(listener)
    }

    fun getAudioManager(): AudioManager? {
        return guildFinder(guildId)?.audioManager?.also { manager ->
            manager.sendingHandler = sendHandler
        }
    }

    fun getPlaylist(): List<AudioTrack> {
        return queue.toList()
    }

    fun setOnNextTrackLoaded(onNextTrackLoaded: () -> Unit) {
        this.onNextTrackLoaded = onNextTrackLoaded
    }

    suspend fun load(query: String): TrackLoadingResult {
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
        if (track is LavaPlayerTrackImpl) {
            _endedAt = 0

            val isStarted = player.startTrack(track.audioTrack, true)
            if (!isStarted) queue.add(track.audioTrack)
        }
    }

    fun skipTrack() {
        player.stopTrack()
        nextTrack()
    }

    fun clearQueue() {
        queue.clear()
        player.stopTrack()
    }

    private fun nextTrack() {
        if (queue.isNotEmpty()) {
            player.startTrack(queue.removeFirst(), false)
        } else {
            _endedAt = System.currentTimeMillis()
        }
        onNextTrackLoaded()
    }

    companion object {
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