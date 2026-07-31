package com.example.utils

import kotlinx.coroutines.delay

suspend fun <T> retryWithBackoff(
    times: Int = 3,
    initialDelay: Long = 1000,
    factor: Double = 2.0,
    block: suspend () -> T
): T {
    var currentDelay = initialDelay
    var lastException: Exception? = null
    for (i in 0 until times) {
        try {
            return block()
        } catch (e: Exception) {
            lastException = e
            if (i < times - 1) {
                delay(currentDelay)
                currentDelay = (currentDelay * factor).toLong()
            }
        }
    }
    throw lastException ?: Exception("Unknown error in retryWithBackoff")
}
