package com.smparkworld.discord.core.media.model

import com.sedmelluq.discord.lavaplayer.track.AudioTrack

interface Track {

    val title: String?

    val uri: String?
}

internal class LavaPlayerTrackImpl(
    val audioTrack: AudioTrack
) : Track {

    override val title: String?
        get() = audioTrack.info?.title

    override val uri: String?
        get() = audioTrack.info?.uri
}