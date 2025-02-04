package org.fcsprepods.command

import org.fcsprepods.Main
import org.fcsprepods.SuggestionBot
import org.fcsprepods.util.MarkdownV2Parser
import org.fcsprepods.util.MarkdownV2Parser.MarkdownV2ParserException
import org.fcsprepods.util.MarkdownV2ParserType
import org.fcsprepods.util.TelegramUtils
import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

object SuggestCommandHandler {
    private val suggestionBot: SuggestionBot? = Main.suggestionBot
    private val dialogs = HashMap<String, String>()

    @JvmStatic
    fun handleSuggestCommand(chatName: String, chatId: String) {
        dialogs.remove(chatName)
        dialogs.put(chatName, "")

        val text = "Напишите цитату в таком формате:\n>`Цитата`\n>\n>`#Автор цитаты`\nАвтором цитаты может быть не только преподаватель :\\)"

        val message: SendMessage = SendMessage
            .builder()
            .parseMode(ParseMode.MARKDOWNV2)
            .text(text)
            .chatId(chatId)
            .build()

        TelegramUtils.sendMessage(suggestionBot!!.telegramClient, message)
    }

    @JvmStatic
    fun handleSuggestion(chatName: String, chatId: String, receivedMessage: String) {
        when (dialogs.get(chatName)) {
            "" ->  {
                // TODO: Should be replaced when the parser is replaced
                try {
                    val message: SendMessage = SendMessage
                        .builder()
                        .chatId(chatId)
                        .parseMode(ParseMode.MARKDOWNV2)
                        .text(
                            "Ваша цитата: \n" + MarkdownV2Parser.parseString(
                                receivedMessage,
                                MarkdownV2ParserType.QUOTE
                            ) + "\nОставляем? Напишите `Да` или `Нет`"
                        )
                        .build()

                    dialogs.put(chatName, MarkdownV2Parser.parseString(receivedMessage, MarkdownV2ParserType.QUOTE))
                    this.sendMessage(message)
                } catch (ex: MarkdownV2ParserException) {
                    val errorMessage: SendMessage = SendMessage
                        .builder()
                        .text(ex.message!!)
                        .chatId(chatId)
                        .parseMode(ParseMode.MARKDOWN)
                        .build()

                    this.sendMessage(errorMessage)
                }
            }
        }
    }

    @JvmStatic
    fun hasActiveDialog(chatName: String): Boolean = dialogs.containsKey(chatName)

    enum class ProcessSuggestionType {
        EMPTY,
        SUGGESTED
    }
}