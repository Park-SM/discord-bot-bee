package com.smparkworld.discord.core.media

import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.smparkworld.discord.core.logger.Logger
import com.smparkworld.discord.core.media.model.Track
import dev.lavalink.youtube.YoutubeAudioSourceManager
import net.dv8tion.jda.api.entities.Guild

private typealias DeprecatedYoutubeSourceManager = com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager

object MusicManagerMediator {

    private const val TAG = "MusicManagerMediator"

    private val playerManager: DefaultAudioPlayerManager = DefaultAudioPlayerManager()
    private val musicManagers: MutableMap<Long, GuildMusicManager> = mutableMapOf()
    private val trackHistories: MutableMap<Long, MutableList<Track>> = mutableMapOf()

    private var isInitialized = false
    private var guildFinder: (guildId: Long) -> Guild? = { null }
    private var evictor: MusicManagerEvictor? = null

    fun initialize(
        guildFinder: (guildId: Long) -> Guild?
    ) {
        if (this.isInitialized) {
            throw IllegalStateException("MusicManagerMediator has already been initialized.")
        }
        this.isInitialized = true
        this.guildFinder = guildFinder
        this.evictor = MusicManagerEvictor(guildFinder, onEvicted = ::onSteamingEnd)

        playerManager.registerSourceManager(YoutubeAudioSourceManager())
        AudioSourceManagers.registerRemoteSources(playerManager, DeprecatedYoutubeSourceManager::class.java)
        Logger.i(TAG, "MusicManagerMediator has been initialized.")
    }

    fun obtainGuildMusicManager(guildId: Long): GuildMusicManager {
        return musicManagers.computeIfAbsent(guildId) {
            Logger.i(TAG, "A new GuildMusicManager has been created. GuildID is $guildId.")

            GuildMusicManager(
                guildId = guildId,
                guildFinder = guildFinder,
                playerManager = playerManager
            ).also {
                evictor?.trackMusicManagerToEvict(guildId, it)
            }
        }
    }

    fun getTrackHistoryBy(guildId: Long): List<Track> {
        return trackHistories[guildId].orEmpty().toList()
    }

    internal fun offerTrackHistory(guildId: Long, track: Track) {
        val history = (trackHistories[guildId] ?: mutableListOf())
        if (history.size >= 10) {
            history.removeFirstOrNull()
        }
        history.add(track)
        trackHistories[guildId] = history
    }

    private fun onSteamingEnd(guildId: Long) {
        musicManagers.remove(guildId)
        Logger.i(TAG, "The GuildMusicManager has been removed. GuildID is $guildId")
    }
}