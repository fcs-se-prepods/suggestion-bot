package org.fcsprepods.command

import org.fcsprepods.Main
import org.fcsprepods.SuggestionBot
import org.fcsprepods.util.MarkdownV2Parser
import org.fcsprepods.util.MarkdownV2Parser.MarkdownV2ParserException
import org.fcsprepods.util.MarkdownV2ParserType
import org.fcsprepods.util.TelegramUtils
import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.polls.input.InputPollOption

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

        TelegramUtils.sendMessage(message)
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
                    TelegramUtils.sendMessage(message)
                } catch (ex: MarkdownV2ParserException) {
                    val errorMessage: SendMessage = SendMessage
                        .builder()
                        .text(ex.message!!)
                        .chatId(chatId)
                        .parseMode(ParseMode.MARKDOWN)
                        .build()

                    TelegramUtils.sendMessage(errorMessage)
                }
            }
            else -> {
                when (receivedMessage) {
                    "Да", "да", "ДА", "Lf", "lf", "LF" -> {
                        val options: MutableList<InputPollOption?> = ArrayList<InputPollOption?>()
                        options.add(InputPollOption.builder().text("Блять, я заплакал (8-10 / 10)").build())
                        options.add(InputPollOption.builder().text("Заебись, четка (4-7 / 10)").build())
                        options.add(InputPollOption.builder().text("Давай по новой миша, все хуйня (0-3 / 10)").build())

                        val messageToChannel: SendMessage = SendMessage
                            .builder()
                            .chatId(suggestionBot!!.suggestionChannel)
                            .parseMode(ParseMode.MARKDOWNV2)
                            // TODO: should be parsed)))
                            .text("Новая цитата от @" + chatName + "\n" + dialogs.get(chatName) + "\n\\#цитата")
                            .build()

                        val poll: SendPoll = SendPoll
                            .builder()
                            .questionParseMode(ParseMode.MARKDOWNV2)
                            .question("Мнение о предложении от $chatName")
                            .allowMultipleAnswers(false)
                            .isAnonymous(false)
                            .options(options)
                            .chatId(suggestionBot.suggestionChannel)
                            .build()

                        val messageToChat: SendMessage = SendMessage
                            .builder()
                            .chatId(chatId)
                            .parseMode(ParseMode.MARKDOWN)
                            .text("Отлично! Цитата отправлена в предложку, следи за цитатником :)")
                            .build()

                        dialogs.remove(chatName)
                        TelegramUtils.sendMessage(messageToChannel)
                        TelegramUtils.sendPoll(poll)
                        TelegramUtils.sendMessage(messageToChat)
                    }
                    "Нет", "нет", "НЕТ", "Nth", "nth", "NTH" -> {
                        val message: SendMessage = SendMessage
                            .builder()
                            .chatId(chatId)
                            .parseMode(ParseMode.MARKDOWN)
                            .text("Жаль, что вы передумали. Отправка цитаты отменена")
                            .build()

                        dialogs.remove(chatName)
                        TelegramUtils.sendMessage(message)
                    }
                    else -> {
                        val message: SendMessage = SendMessage
                            .builder()
                            .chatId(chatId)
                            .parseMode(ParseMode.MARKDOWN)
                            .text("Напишите `Да` или `Нет`")
                            .build()

                        TelegramUtils.sendMessage(message)
                    }
                }
            }
        }
    }

    @JvmStatic
    fun hasActiveDialog(chatName: String): Boolean = dialogs.containsKey(chatName)

    @JvmStatic
    fun removeDialog(chatName: String) = dialogs.remove(chatName)
}