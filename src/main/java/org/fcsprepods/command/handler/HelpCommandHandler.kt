package org.fcsprepods.command.handler

import org.fcsprepods.wrapper.TelegramChatInfo
import org.fcsprepods.command.CommandContext
import org.fcsprepods.parser.MarkdownParser
import org.fcsprepods.util.TelegramUtils
import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

object HelpCommandHandler: CommandContext {
    override fun execute(telegramChatInfo: TelegramChatInfo) {
        val text = "Доступные команды:\n\n" +
            "/start - начать работу с ботом\n" +
            "/suggest - отправить цитату в предложку\n" +
            "/support - связаться с разработчиком"

        val message: SendMessage = SendMessage
            .builder()
            .parseMode(ParseMode.MARKDOWNV2)
            .text(MarkdownParser.parse((text)).string())
            .chatId(telegramChatInfo.chatId)
            .build()

        TelegramUtils.sendMessage(message)
    }
}