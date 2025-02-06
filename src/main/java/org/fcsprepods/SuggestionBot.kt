package org.fcsprepods

import org.fcsprepods.Main.getConfig
import org.fcsprepods.command.HelpCommandHandler
import org.fcsprepods.command.StartCommandHandler
import org.fcsprepods.command.SuggestCommandHandler
import org.fcsprepods.util.MarkdownV2Parser
import org.fcsprepods.util.MarkdownV2Parser.MarkdownV2ParserException
import org.fcsprepods.util.MarkdownV2ParserType
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer
import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.message.Message
import org.telegram.telegrambots.meta.api.objects.polls.input.InputPollOption
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.meta.generics.TelegramClient
import java.util.ArrayList
import java.util.HashMap

class SuggestionBot(token: String) : LongPollingSingleThreadUpdateConsumer {
    val telegramClient: TelegramClient = OkHttpTelegramClient(token)
    private val dialogs = HashMap<String?, String?>()

    //TODO: Make sure to make DataProcessor.kt to get config values
    private val botConfig = getConfig().get("bot") as HashMap<String?, Any?>
    var suggestionChannel: Long = botConfig.get("channel") as Long

    override fun consume(update: Update) {
        if (update.message == null || update.message.chatId == suggestionChannel) return

        val receivedMessage = update.message.text
        val chatId = update.message.chatId
        val chatName = update.message.chat.userName

        when (receivedMessage) {
            "/start", "/start@fcs_se_quote_book_bot" -> StartCommandHandler.handleStartCommand(chatId.toString())
            "/help", "/help@fcs_se_quote_book_bot" -> HelpCommandHandler.handleHelpCommand(chatId.toString())
            "/suggest", "/suggest@fcs_se_quote_book_bot" -> SuggestCommandHandler.handleSuggestCommand(chatName, chatId.toString())
            else -> {
                if (SuggestCommandHandler.hasActiveDialog(chatName)) SuggestCommandHandler.handleSuggestion(chatName, chatId.toString(), receivedMessage)
            }

        }
            } else if (dialogs.containsKey(chatName) && dialogs.get(chatName)!!.isEmpty()) {
                this.processSuggestion(update, ProcessSuggestionType.EMPTY)
            } else if (dialogs.containsKey(chatName) && !dialogs.get(chatName)!!.isEmpty()) {
                this.processSuggestion(update, ProcessSuggestionType.SUBMITTED)
            } else {
                val message: SendMessage = SendMessage
                    .builder()
                    .parseMode(ParseMode.MARKDOWN)
                    .text("Неизвестная команда, попробуйте\n/suggest\n/start")
                    .chatId(update.getMessage().getChatId())
                    .build()

                this.sendMessage(message)
            }
        }

    private fun processSuggestion(update: Update, type: ProcessSuggestionType) {
        val chatId = update.getMessage().getChatId()
        val chatName = update.getMessage().getChat().getUserName()
        val receivedMessage = update.getMessage().getText()

        when (type) {
            ProcessSuggestionType.EMPTY -> {
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

            ProcessSuggestionType.SUBMITTED -> {
                if (receivedMessage == "Да") {
                    var username = update.getMessage().getChat().getUserName()
                    if (username == null) username = "\\username_is_null!\\"

                    val options: MutableList<InputPollOption?> = ArrayList<InputPollOption?>()
                    options.add(InputPollOption.builder().text("Блять, я заплакал (8-10 / 10)").build())
                    options.add(InputPollOption.builder().text("Заебись, четка (4-7 / 10)").build())
                    options.add(InputPollOption.builder().text("Давай по новой миша, все хуйня (0-3 / 10)").build())

                    val messageToChannel: SendMessage = SendMessage
                        .builder()
                        .chatId(botConfig.get("channel") as Long)
                        .parseMode(ParseMode.MARKDOWNV2)
                        .text("Новая цитата от @" + username + "\n" + dialogs.get(chatName) + "\n\\#цитата")
                        .build()

                    val poll: SendPoll = SendPoll
                        .builder()
                        .questionParseMode(ParseMode.MARKDOWNV2)
                        .question("Мнение о предложении от " + update.getMessage().getChat().getUserName())
                        .allowMultipleAnswers(false)
                        .isAnonymous(false)
                        .options(options)
                        .chatId(botConfig.get("channel") as Long)
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
                } else if (receivedMessage == "Нет") {
                    val message: SendMessage = SendMessage
                        .builder()
                        .chatId(chatId)
                        .parseMode(ParseMode.MARKDOWN)
                        .text("Отправка цитаты в предложку отменена..")
                        .build()

                    dialogs.remove(chatName)
                    this.sendMessage(message)
                } else {
                    val message: SendMessage = SendMessage
                        .builder()
                        .chatId(chatId)
                        .parseMode(ParseMode.MARKDOWN)
                        .text("Пожалуйста, напишите `Да` или `Нет`")
                        .build()

                    this.sendMessage(message)
                }
            }
        }
    }

    private fun sendPoll(poll: SendPoll) {
        try {
            telegramClient.execute<Message?, SendPoll?>(poll)
        } catch (ex: TelegramApiException) {
            println(ex.message)
        }
    }

    private class Suggestion(val message: String, val author: String) {
        fun sendSuggestion() {

        }
    }
}