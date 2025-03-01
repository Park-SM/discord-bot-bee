package com.smparkworld.discord.core.media

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.smparkworld.discord.core.logger.Logger
import dev.lavalink.youtube.YoutubeAudioSourceManager
import net.dv8tion.jda.api.entities.Guild

private typealias DeprecatedYoutubeSourceManager = com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager

object MusicManagerMediator {

    private const val TAG = "MusicManagerMediator"

    private val playerManager: DefaultAudioPlayerManager = DefaultAudioPlayerManager()
    private val musicManagers: MutableMap<Long, GuildMusicManager> = mutableMapOf()

    private var isInitialized = false
    private var evictor: MusicManagerEvictor? = null

    fun initialize(
        guildFinder: (guildId: Long) -> Guild?
    ) {
        if (this.isInitialized) {
            throw IllegalStateException("MusicManagerMediator has already been initialized.")
        }
        this.isInitialized = true
        this.evictor = MusicManagerEvictor(guildFinder, onEvicted = ::onSteamingEnd)

        playerManager.registerSourceManager(YoutubeAudioSourceManager())
        AudioSourceManagers.registerRemoteSources(playerManager, DeprecatedYoutubeSourceManager::class.java)
        Logger.i(TAG, "MusicManagerMediator has been initialized.")
    }

    fun obtainGuildTracker(guildId: Long): GuildMusicManager {
        return musicManagers.computeIfAbsent(guildId) {
            Logger.i(TAG, "A new GuildMusicManager has been created. GuildID is $guildId.")

            GuildMusicManager(playerManager = playerManager).also { manager ->
                evictor?.trackMusicManagerToEvict(guildId, manager)
            }
        }
    }

    fun loadItem(query: String, handler: AudioLoadResultHandler) {
        playerManager.loadItem(query, handler)
    }

    private fun onSteamingEnd(guildId: Long) {
        musicManagers.remove(guildId)
        Logger.i(TAG, "The GuildMusicManager has been removed. GuildID is $guildId")
    }
}