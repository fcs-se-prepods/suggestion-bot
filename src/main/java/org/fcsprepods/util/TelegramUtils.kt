package org.fcsprepods.util

import org.fcsprepods.Main
import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.message.Message
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.meta.generics.TelegramClient

object TelegramUtils {
    @JvmStatic
    fun sendMessage(message: SendMessage, providedTelegramClient: TelegramClient? = null) {
        val telegramClient = providedTelegramClient ?: Main.suggestionBot!!.telegramClient
        try {
            telegramClient.execute<Message?, SendMessage?>(message)
        } catch (ex: TelegramApiException) {
            println(ex.message)
            sendErrorMessage(message.chatId)
        }
    }

    @JvmStatic
    fun sendPoll(poll: SendPoll, providedTelegramClient : TelegramClient? = null) {
        val telegramClient = providedTelegramClient ?: Main.suggestionBot!!.telegramClient
        try {
            telegramClient.execute<Message?, SendPoll?>(poll)
        } catch (ex: TelegramApiException) {
            println(ex.message)
            sendErrorMessage(poll.chatId)
        }
    }

    @JvmStatic
    fun sendErrorMessage(chatId: String, providedTelegramClient: TelegramClient? = null) {
        val telegramClient = providedTelegramClient ?: Main.suggestionBot!!.telegramClient
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

    @JvmStatic
    fun getChatById(chatId: String): String {
        val getChat = GetChat
            .builder()
            .chatId(chatId)
            .build()

        try {
            return Main.suggestionBot!!.telegramClient.execute(getChat).userName
        } catch (ex: TelegramApiException) {
            println(ex.message)
        }

        return ""
    }
}