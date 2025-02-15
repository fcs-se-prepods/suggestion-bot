package org.fcsprepods.command

import org.fcsprepods.parser.MarkdownParser
import org.fcsprepods.util.TelegramUtils
import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

object HelpCommandHandler {
    fun handle(chatId: String) {
        val text = "Доступные команды:\n\n" +
            "/start - начать работу с ботом\n" +
            "/suggest - отправить цитату в предложку\n" +
            "/help - связаться с разработчиком"

        val message: SendMessage = SendMessage
            .builder()
            .parseMode(ParseMode.MARKDOWNV2)
            .text(MarkdownParser.parse((text)).string())
            .chatId(chatId)
            .build()

        TelegramUtils.sendMessage(message)
    }
}