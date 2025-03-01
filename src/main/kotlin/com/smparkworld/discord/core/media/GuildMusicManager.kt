package com.smparkworld.discord.core.media

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame
import net.dv8tion.jda.api.audio.AudioSendHandler
import java.nio.ByteBuffer

class GuildMusicManager(
    playerManager: AudioPlayerManager
) {

    private var _endedAt: Long = 0
    val endedAt: Long get() = _endedAt

    val currentTrack: AudioTrack?
        get() = player.playingTrack

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

    fun getSendHandler(): AudioSendHandler {
        return sendHandler
    }

    fun getPlaylist(): List<AudioTrack> {
        return queue.toList()
    }

    fun queue(track: AudioTrack) {
        val isStarted = player.startTrack(track, true)
        if (!isStarted) queue.add(track)
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