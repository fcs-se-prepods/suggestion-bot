package org.fcsprepods.command.handler

import org.fcsprepods.wrapper.TelegramChatInfo
import org.fcsprepods.command.CommandContext
import org.fcsprepods.util.TelegramUtils
import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

object SupportCommandHandler: CommandContext {
    override fun execute(telegramChatInfo: TelegramChatInfo) {
        val text = "Если у вас возникли проблемы, связанные с ботом, обращайтесь к @neverwhatlose"

        val message: SendMessage = SendMessage
            .builder()
            .parseMode(ParseMode.MARKDOWN)
            .text(text)
            .chatId(telegramChatInfo.chatId)
            .build()

        SuggestCommandHandler.activeDialogStore.removeDialog(telegramChatInfo.chatId)
        TelegramUtils.sendMessage(message)
    }
}