package com.smparkworld.discord.core.media

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import dev.lavalink.youtube.YoutubeAudioSourceManager

private typealias DeprecatedYoutubeSourceManager = com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager

object MusicManagerMediator {

    private val playerManager: DefaultAudioPlayerManager = DefaultAudioPlayerManager()
    private val musicManagers: MutableMap<Long, GuildMusicManager> = mutableMapOf()

    init {
        playerManager.registerSourceManager(YoutubeAudioSourceManager())
        AudioSourceManagers.registerRemoteSources(playerManager, DeprecatedYoutubeSourceManager::class.java)
    }

    fun obtainGuildTracker(guildId: Long): GuildMusicManager {
        return musicManagers.computeIfAbsent(guildId) { GuildMusicManager(playerManager) }
    }

    fun loadItem(query: String, handler: AudioLoadResultHandler) {
        playerManager.loadItem(query, handler)
    }
}