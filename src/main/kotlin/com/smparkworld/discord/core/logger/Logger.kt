package com.smparkworld.discord.core.logger

import java.text.SimpleDateFormat

object Logger {

    private const val TAG_LENGTH = 24
    private const val CATEGORY_LENGTH = 12

    private const val INFO = "[INFO]"
    private const val DEBUG = "[DEBUG]"
    private const val SYSTEM = "[SYSTEM]"
    private const val ERROR = "[ERROR]"

    private val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    private val currentTimestamp: String
        get() = formatter.format(System.currentTimeMillis())

    fun i(tag: String, message: String) {
        println("$currentTimestamp ${formatCategory(INFO)} ${formatTag(tag)} $message")
    }

    fun d(tag: String, message: String) {
        println("$currentTimestamp ${formatCategory(DEBUG)} ${formatTag(tag)} $message")
    }

    fun s(tag: String, message: String) {
        println("$currentTimestamp ${formatCategory(SYSTEM)} ${formatTag(tag)} $message")
    }

    fun e(tag: String, message: String, exception: Exception? = null) {
        println("$currentTimestamp ${formatCategory(ERROR)} ${formatTag(tag)} $message")
        exception?.printStackTrace()
    }

    private fun formatTag(input: String): String {
        return if (input.length > TAG_LENGTH) {
            input.substring(0, TAG_LENGTH)
        } else {
            input.padEnd(TAG_LENGTH, ' ')
        }
    }

    private fun formatCategory(input: String): String {
        return if (input.length > CATEGORY_LENGTH) {
            input.substring(0, CATEGORY_LENGTH)
        } else {
            input.padEnd(CATEGORY_LENGTH, ' ')
        }
    }
}