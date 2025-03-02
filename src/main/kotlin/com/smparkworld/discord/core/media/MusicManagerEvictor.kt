package com.smparkworld.discord.core.media

import net.dv8tion.jda.api.entities.Guild

internal class MusicManagerEvictor(
    private val guildFinder: (guildId: Long) -> Guild?,
    private val onEvicted: (guildId: Long) -> Unit
) {
    private val trackedMusicManagers: MutableMap<Long, GuildMusicManager> = mutableMapOf()
    private var thread: Thread? = null

    init {
        thread = Thread {
            try {
                while(!Thread.currentThread().isInterrupted) {
                    Thread.sleep(INTERVAL_MILLIS)
                    onTick()
                }
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
            }
        }
        thread?.start()
    }

    fun trackMusicManagerToEvict(guildId: Long, manager: GuildMusicManager) {
        trackedMusicManagers[guildId]?.let { evict(guildId) }
        trackedMusicManagers[guildId] = manager
    }

    fun release() {
        thread?.interrupt()
    }

    private fun onTick() {
        trackedMusicManagers.filter { it.value.endedAt > 0 }
            .filter { it.value.endedAt + DELAY_EVICTING_MILLIS < System.currentTimeMillis() }
            .keys
            .onEach(::evict)
            .forEach(trackedMusicManagers::remove)
    }

    private fun evict(guildId: Long) {
        guildFinder.invoke(guildId)
            ?.audioManager
            ?.closeAudioConnection()
        onEvicted(guildId)
    }

    companion object {
        private const val INTERVAL_MILLIS = 1000L * 10               // 10s
        private const val DELAY_EVICTING_MILLIS = 1000L * 60 * 15    // 15m
    }
}