package org.fcsprepods.command

import org.fcsprepods.Main
import org.fcsprepods.SuggestionBot
import org.fcsprepods.util.TelegramUtils
import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

object HelpCommandHandler {
    @JvmStatic
    fun handleHelpCommand(chatId: String) {
        val text = "Если у вас возникли проблемы, связанные с ботом, обращайтесь к @neverwhatlose"

        val message: SendMessage = SendMessage
            .builder()
            .parseMode(ParseMode.MARKDOWN)
            .text(text)
            .chatId(chatId)
            .build()

        SuggestCommandHandler.removeDialog(TelegramUtils.getChatById(chatId))
        TelegramUtils.sendMessage(message)
    }
}