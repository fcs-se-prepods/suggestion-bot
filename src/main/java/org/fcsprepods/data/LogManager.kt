package org.fcsprepods.data

import org.telegram.telegrambots.meta.api.objects.message.Message
import java.text.SimpleDateFormat
import kotlin.io.path.Path
import kotlin.io.path.createDirectory
import kotlin.io.path.exists

object LogManager {
    @JvmStatic
    fun log(message: Message) {
        val path = Path("/logs")
        if (!path.exists()) {
            path.createDirectory()
        }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss")
        val logFile = Path("/logs/${message.chatId}_${dateFormat}.log")

    }
}