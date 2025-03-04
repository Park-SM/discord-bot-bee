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
        // 1. GuildMusicManager release 처리
        trackedMusicManagers[guildId]?.release()

        // 2. AudioChannel에서 봇 제거
        guildFinder.invoke(guildId)
            ?.audioManager
            ?.closeAudioConnection()

        // 3. Callback 호출
        onEvicted(guildId)
    }

    companion object {
        private const val INTERVAL_MILLIS = 1000L * 10               // 10s
        private const val DELAY_EVICTING_MILLIS = 1000L * 60 * 15    // 15m
    }
}