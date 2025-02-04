package org.fcsprepods.command

import org.fcsprepods.Main
import org.fcsprepods.SuggestionBot
import org.fcsprepods.util.TelegramUtils
import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

object StartCommandHandler {
    private val suggestionBot: SuggestionBot? = Main.suggestionBot
    @JvmStatic
    fun handleStartCommand(chatId: String) {
        val text = "*Добро пожаловать в предложку [лучшего цитатника](https://t.me/fcsseprepods) ФКН Программной Инженерии* \n\nДля отправки цитаты в предложку используй /suggest"

        val message: SendMessage = SendMessage
            .builder()
            .parseMode(ParseMode.MARKDOWNV2)
            .chatId(chatId)
            .text(text)
            .build()

        TelegramUtils.sendMessage(suggestionBot!!.telegramClient, message)
    }
}