package com.smparkworld.discord.feature.bee.commands.player

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
    val player = playerManager.createPlayer()
    val scheduler = TrackScheduler(player)
    val sendHandler = AudioPlayerSendHandler(player)

    init {
        player.addListener(scheduler)
    }


}

class TrackScheduler(
    private val player: AudioPlayer
) : AudioEventAdapter() {

    val currentAudioTrack: AudioTrack?
        get() = player.playingTrack

    private val queue: MutableList<AudioTrack> = mutableListOf()

    fun queue(track: AudioTrack) {
        if (!player.startTrack(track, true)) {
            queue.add(track)
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

    fun getPlaylist(): List<AudioTrack> = queue.toList()

    override fun onTrackEnd(player: AudioPlayer, track: AudioTrack, endReason: AudioTrackEndReason) {
        if (endReason.mayStartNext) {
            nextTrack()
        }
    }

    private fun nextTrack() {
        if (queue.isNotEmpty()) {
            player.startTrack(queue.removeAt(0), false)
        }
    }
}


class AudioPlayerSendHandler(
    private val audioPlayer: AudioPlayer
) : AudioSendHandler {

    private val buffer = ByteBuffer.allocate(1024)

    override fun canProvide(): Boolean =
        audioPlayer.provide(MutableAudioFrame(buffer))

    override fun provide20MsAudio(): ByteBuffer =
        buffer.flip()

    override fun isOpus(): Boolean = true
}