package it.mat20018562.meetus

import android.util.Log

object LoggingConstants {
    const val tag = "MeetUs"
}

fun logDebug(message: String) {
    Log.d(LoggingConstants.tag, message)
}

fun logError(message: String, exception: Exception? = null) {
    Log.e(LoggingConstants.tag, message, exception)
}