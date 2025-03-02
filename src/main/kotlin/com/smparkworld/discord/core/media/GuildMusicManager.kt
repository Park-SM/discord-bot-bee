package com.smparkworld.discord.core.media

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame
import com.smparkworld.discord.core.media.model.LavaPlayerTrackImpl
import com.smparkworld.discord.core.media.model.Track
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

    fun load(query: String, listener: MusicResultListener) {
        playerManager.loadItem(query, MusicResultListenerLavaPlayerAdapter(listener))
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
            player.startTrack(queue.removeAt(0), false)
        } else {
            _endedAt = System.currentTimeMillis()
        }
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