package com.smparkworld.discord.core.logger

import java.text.SimpleDateFormat

object Logger {

    private const val TAG_LENGTH = 25

    private val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    private val currentTimestamp: String
        get() = formatter.format(System.currentTimeMillis())

    fun d(tag: String, message: String) {
        println("$currentTimestamp [DEBUG] ${formatTag(tag)} $message")
    }

    fun i(tag: String, message: String) {
        println("$currentTimestamp [INFO] ${formatTag(tag)} $message")
    }

    private fun formatTag(input: String): String {
        return if (input.length > TAG_LENGTH) {
            input.substring(0, TAG_LENGTH)
        } else {
            input.padEnd(TAG_LENGTH, ' ')
        }
    }
}