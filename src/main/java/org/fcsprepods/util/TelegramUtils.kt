package org.fcsprepods.util

import org.fcsprepods.Application
import org.fcsprepods.command.handler.SuggestCommandHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.meta.generics.TelegramClient

object TelegramUtils {
    private val logger: Logger = LoggerFactory.getLogger(TelegramUtils::class.java)

    @JvmStatic
    fun sendMessage(message: SendMessage, providedTelegramClient: TelegramClient? = null) {
        val telegramClient = providedTelegramClient ?: Application.suggestionBot.telegramClient
        try {
            telegramClient.execute(message)
        } catch (ex: TelegramApiException) {
            logger.error(ex.message)

            if (SuggestCommandHandler.activeDialogStore.hasActiveDialog(message.chatId)) {
                SuggestCommandHandler.activeDialogStore.removeDialog(message.chatId)
            }

            sendErrorMessage(message.chatId)
        }
    }

    @JvmStatic
    fun sendPoll(poll: SendPoll, providedTelegramClient : TelegramClient? = null) {
        val telegramClient = providedTelegramClient ?: Application.suggestionBot.telegramClient
        try {
            telegramClient.execute(poll)
        } catch (ex: TelegramApiException) {
            logger.error(ex.message)
            sendErrorMessage(poll.chatId)
        }
    }

    @JvmStatic
    fun sendErrorMessage(chatId: String, providedTelegramClient: TelegramClient? = null) {
        val telegramClient = providedTelegramClient ?: Application.suggestionBot.telegramClient
        val errorMessage: SendMessage = SendMessage
            .builder()
            .chatId(chatId)
            .parseMode(ParseMode.MARKDOWN)
            .text("Возникла непредвиденная ошибка... Будем благодарны, если вы дадите нам об этом знать: /support")
            .build()

        try {
            telegramClient.execute(errorMessage)
        } catch (ex: TelegramApiException) {
            logger.error(ex.message)
        }
    }
}