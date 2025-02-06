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
import kotlin.collections.remove
import kotlin.text.get

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
                    TelegramUtils.sendMessage(suggestionBot!!.telegramClient, message)
                } catch (ex: MarkdownV2ParserException) {
                    val errorMessage: SendMessage = SendMessage
                        .builder()
                        .text(ex.message!!)
                        .chatId(chatId)
                        .parseMode(ParseMode.MARKDOWN)
                        .build()

                    TelegramUtils.sendMessage(suggestionBot!!.telegramClient, errorMessage)
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

                        this.sendMessage(messageToChannel)
                        this.sendPoll(poll)
                        this.sendMessage(messageToChat)
                    }

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