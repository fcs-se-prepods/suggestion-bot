package org.fcsprepods.command.handler

import org.fcsprepods.wrapper.TelegramChatInfo
import org.fcsprepods.command.CommandContext
import org.fcsprepods.util.TelegramUtils
import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

object StartCommandHandler : CommandContext {
    override fun execute(telegramChatInfo: TelegramChatInfo) {
        val text = "*Добро пожаловать в предложку [лучшего цитатника](https://t.me/fcsseprepods) ФКН Программной Инженерии* \n\nДля отправки цитаты в предложку используй /suggest"

        val message: SendMessage = SendMessage
            .builder()
            .parseMode(ParseMode.MARKDOWNV2)
            .chatId(telegramChatInfo.chatId)
            .text(text)
            .build()

        // ig it should check if return value of getChatById is empty string
        SuggestCommandHandler.activeDialogStore.removeDialog(telegramChatInfo.chatId)
        TelegramUtils.sendMessage(message)
    }
}