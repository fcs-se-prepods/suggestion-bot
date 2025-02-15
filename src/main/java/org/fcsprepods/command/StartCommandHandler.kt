package org.fcsprepods.command

import org.fcsprepods.util.TelegramUtils
import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

object StartCommandHandler {
    @JvmStatic
    fun handle(chatId: String) {
        val text = "*Добро пожаловать в предложку [лучшего цитатника](https://t.me/fcsseprepods) ФКН Программной Инженерии* \n\nДля отправки цитаты в предложку используй /suggest"

        val message: SendMessage = SendMessage
            .builder()
            .parseMode(ParseMode.MARKDOWNV2)
            .chatId(chatId)
            .text(text)
            .build()

        // ig it should check if return value of getChatById is empty string
        SuggestCommandHandler.removeDialog(TelegramUtils.getChatById(chatId))
        TelegramUtils.sendMessage(message)
    }
}