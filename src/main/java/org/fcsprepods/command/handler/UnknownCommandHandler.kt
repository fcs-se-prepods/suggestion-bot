package org.fcsprepods.command.handler

import org.fcsprepods.wrapper.TelegramChatInfo
import org.fcsprepods.command.CommandContext
import org.fcsprepods.util.TelegramUtils
import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

object UnknownCommandHandler: CommandContext {
    override fun execute(telegramChatInfo: TelegramChatInfo) {
        if (SuggestCommandHandler.activeDialogStore.hasActiveDialog(telegramChatInfo.userId)) {
            SuggestCommandHandler.handleSuggestion(telegramChatInfo)
            return
        }

        if (telegramChatInfo.chatType != "private") return

        val text = "Неизвестная команда\\. Используйте /help для получения списка команд"

        val message: SendMessage = SendMessage
            .builder()
            .parseMode(ParseMode.MARKDOWNV2)
            .chatId(telegramChatInfo.chatId)
            .text(text)
            .build()

        TelegramUtils.sendMessage(message)
    }
}