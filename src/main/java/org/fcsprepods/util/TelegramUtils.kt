package org.fcsprepods.util

import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.message.Message
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.meta.generics.TelegramClient

object TelegramUtils {
    @JvmStatic
    fun sendMessage(telegramClient: TelegramClient, message: SendMessage) {
        try {
            telegramClient.execute<Message?, SendMessage?>(message)
        } catch (ex: TelegramApiException) {
            println(ex.message)
            sendErrorMessage(telegramClient, message.chatId)
        }
    }

    @JvmStatic
    fun sendErrorMessage(telegramClient: TelegramClient, chatId: String) {
        val errorMessage: SendMessage = SendMessage
            .builder()
            .chatId(chatId)
            .parseMode(ParseMode.MARKDOWN)
            .text("Возникла непредвиденная ошибка... Будем благодарны, если вы дадите нам об этом знать: /help ")
            .build()

        try {
            telegramClient.execute<Message?, SendMessage?>(errorMessage)
        } catch (ex: TelegramApiException) {
            println(ex.message)
        }
    }
}