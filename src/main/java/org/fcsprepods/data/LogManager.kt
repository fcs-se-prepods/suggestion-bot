package org.fcsprepods.data

import org.fcsprepods.parser.MarkdownParser
import org.fcsprepods.util.TelegramUtils
import org.fcsprepods.wrapper.TelegramChatInfo
import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import java.text.SimpleDateFormat

object LogManager {
    fun log(ex: Throwable, telegramChatInfo: TelegramChatInfo) {
        val dateFormat = SimpleDateFormat("yyyy/MM/dd-HH:mm:ss")
        val stackTraceElement = ex.stackTrace.firstOrNull()
        val errorLog = "------------------------------------\n" +
                dateFormat.format(System.currentTimeMillis()) + "\n" +
                "An error occurred at ${ex.javaClass.simpleName}: ${ex.message}\n" +
                "In ${stackTraceElement?.className}.${stackTraceElement?.methodName} at line ${stackTraceElement?.lineNumber}\n\n" +
                "Message that caused an error: ${telegramChatInfo.message}"

        val message: SendMessage = SendMessage
            .builder()
            .parseMode(ParseMode.MARKDOWNV2)
            .chatId(ConfigLoader.long("bot.exceptions-channel"))
            .text(
                MarkdownParser.parse("$telegramChatInfo", MarkdownParser.Format.CODE_BLOCK, "log") + "\n" +
                        MarkdownParser.parse(errorLog, MarkdownParser.Format.CODE_BLOCK, "log")
            )
            .build()

        TelegramUtils.sendMessage(message)
    }
}