package org.fcsprepods

import org.fcsprepods.Main.getConfig
import org.fcsprepods.command.HelpCommandHandler
import org.fcsprepods.command.StartCommandHandler
import org.fcsprepods.command.SuggestCommandHandler
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.generics.TelegramClient
import java.util.HashMap

class SuggestionBot(token: String) : LongPollingSingleThreadUpdateConsumer {
    val telegramClient: TelegramClient = OkHttpTelegramClient(token)

    //TODO: Make sure to make DataProcessor.kt to get config values
    private val botConfig = getConfig().get("bot") as HashMap<String?, Any?>
    var suggestionChannel: Long = botConfig.get("channel") as Long

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
            }

        }
    }
}