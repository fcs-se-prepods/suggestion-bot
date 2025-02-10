package org.fcsprepods

import org.fcsprepods.command.HelpCommandHandler
import org.fcsprepods.command.StartCommandHandler
import org.fcsprepods.command.SuggestCommandHandler
import org.fcsprepods.data.ConfigLoader
import org.fcsprepods.util.TelegramUtils
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer
import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.generics.TelegramClient

class SuggestionBot(token: String) : LongPollingSingleThreadUpdateConsumer {
    val telegramClient: TelegramClient = OkHttpTelegramClient(token)
    var suggestionChannel: Long = ConfigLoader.getLong("bot.channel")

    override fun consume(update: Update) {
        if (update.message == null || update.message.chatId == suggestionChannel) return

        val receivedMessage = update.message.text
        val chatId = update.message.chatId
        val chatName = update.message.chat.userName

        when (receivedMessage) {
            "/start", "/start@fcs_se_quote_book_bot" -> StartCommandHandler.handleStartCommand(chatId.toString())
            "/help", "/help@fcs_se_quote_book_bot" -> HelpCommandHandler.handleHelpCommand(chatId.toString())
            "/suggest", "/suggest@fcs_se_quote_book_bot" -> SuggestCommandHandler.handleSuggestCommand(
                chatName,
                chatId.toString()
            )

            else -> {
                if (SuggestCommandHandler.hasActiveDialog(chatName)) SuggestCommandHandler.handleSuggestion(
                    chatName,
                    chatId.toString(),
                    receivedMessage
                )
                else {
                    val text = "Неизвестная команда\\. Используйте /commands для получения списка команд"

                    val message: SendMessage = SendMessage
                        .builder()
                        .parseMode(ParseMode.MARKDOWNV2)
                        .chatId(chatId)
                        .text(text)
                        .build()

                    TelegramUtils.sendMessage(message)
                }
            }

        }
    }
}