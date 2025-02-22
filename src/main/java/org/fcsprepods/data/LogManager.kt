package org.fcsprepods.data

import org.fcsprepods.parser.MarkdownParser
import org.fcsprepods.util.TelegramUtils
import org.fcsprepods.wrapper.TelegramChatInfo
import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.methods.send.SendDocument
import org.telegram.telegrambots.meta.api.objects.InputFile
import java.io.ByteArrayInputStream
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object LogManager {
    private val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter
        .ofPattern("yyyy/MM/dd-HH:mm:ss")
        .withZone(ZoneId.systemDefault())
    private val SEPARATOR = "—".repeat(40)

    fun log(ex: Throwable, telegramChatInfo: TelegramChatInfo) {
        val dateFormat = DATE_FORMATTER.format(Instant.now())
        val stackTraceElement = ex.stackTrace.firstOrNull()
        val location = if (stackTraceElement != null) {
            "${stackTraceElement.className ?: "UnknownClass"}.${stackTraceElement.methodName ?: "UnknownMethod"}() (line ${stackTraceElement.lineNumber})"
        } else {
            "Unknown location"
        }

        val errorLog = "$SEPARATOR\nDate: $dateFormat\nError Type: ${ex.javaClass.simpleName}\nLocation: $location\n\n$telegramChatInfo"

        val inputStream = ByteArrayInputStream(stackTraceElement.toString().toByteArray())

        val document = SendDocument.builder()
            .chatId(ConfigLoader.get<Long>("bot.exceptions-channel"))
            .caption(MarkdownParser.parse(errorLog, MarkdownParser.Format.CODE_BLOCK, "log"))
            .parseMode(ParseMode.MARKDOWNV2)
            .document(InputFile(inputStream, "stacktrace.log"))
            .build()

        TelegramUtils.sendDocument(document)
    }

    fun log(ex: Throwable, message: String) {
        val dateFormat = DATE_FORMATTER.format(Instant.now())
        val stackTraceElement = ex.stackTrace.firstOrNull()
        val location = if (stackTraceElement != null) {
            "${stackTraceElement.className ?: "UnknownClass"}.${stackTraceElement.methodName ?: "UnknownMethod"}() (line ${stackTraceElement.lineNumber})"
        } else {
            "Unknown location"
        }

        val errorLog = "$SEPARATOR\nDate: $dateFormat\nError Type: ${ex.javaClass.simpleName}\nLocation: $location"

        val document = SendDocument.builder()
            .chatId(ConfigLoader.get<Long>("bot.exceptions-channel"))
            .caption(MarkdownParser.parse(errorLog, MarkdownParser.Format.CODE_BLOCK, "log"))
            .parseMode(ParseMode.MARKDOWNV2)
            .document(InputFile(ByteArrayInputStream(stackTraceElement.toString().toByteArray()), "stacktrace.log"))
            .document(InputFile(ByteArrayInputStream(message.toByteArray()), "message.txt"))
            .build()

        TelegramUtils.sendDocument(document)
    }
}