package com.smparkworld.discord.core.media.model

import com.sedmelluq.discord.lavaplayer.track.AudioTrack

interface Track {

    val title: String?

    val uri: String?

    val thumbnailUrl: String?
}

internal class LavaPlayerTrackImpl(
    val audioTrack: AudioTrack
) : Track {

    override val title: String?
        get() = audioTrack.info?.title

    override val uri: String?
        get() = audioTrack.info?.uri

    override val thumbnailUrl: String?
        get() = audioTrack.info?.identifier
            ?.let { "https://img.youtube.com/vi/${it}/hqdefault.jpg" }
}