package org.fcsprepods.command.handler

import okhttp3.internal.notify
import org.fcsprepods.wrapper.TelegramChatInfo
import org.fcsprepods.command.CommandContext
import org.fcsprepods.command.CommandRoute
import org.fcsprepods.data.ConfigLoader
import org.fcsprepods.parser.MarkdownParser
import org.fcsprepods.util.TelegramUtils
import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.polls.input.InputPollOption

object SuggestCommandHandler: CommandContext {
    val activeDialogStore = ActiveDialogStore()

    override fun execute(telegramChatInfo: TelegramChatInfo) {
        activeDialogStore.removeDialog(telegramChatInfo.userId)
        activeDialogStore.addDialog(telegramChatInfo.userId, telegramChatInfo)

        val text = "Напишите цитату в таком формате:\n>`Цитата`\n>\n>`#Автор цитаты`\nАвтором цитаты может быть не только преподаватель :\\)"

        val message: SendMessage = SendMessage
            .builder()
            .parseMode(ParseMode.MARKDOWNV2)
            .text(text)
            .chatId(telegramChatInfo.chatId)
            .build()

        TelegramUtils.sendMessage(message)
    }

    fun handleSuggestion(telegramChatInfo: TelegramChatInfo) {
        if (!activeDialogStore.hasActiveDialog(telegramChatInfo.userId)) return

        if (
            activeDialogStore.getDialog(telegramChatInfo.userId)?.message in CommandRoute.SUGGEST.aliases
            && activeDialogStore.getDialog(telegramChatInfo.userId)?.chatId == telegramChatInfo.chatId
            ) {
            val text = MarkdownParser.parse(telegramChatInfo.message, MarkdownParser.Format.QUOTE)

            if (!text.contains("#")) {
                val message: SendMessage = SendMessage
                    .builder()
                    .chatId(telegramChatInfo.chatId)
                    .parseMode(ParseMode.MARKDOWN)
                    .text("Цитата должна содержать автора. Попробуйте еще раз")
                    .build()

                TelegramUtils.sendMessage(message)
                return
            }

            val message: SendMessage = SendMessage
                .builder()
                .chatId(telegramChatInfo.chatId)
                .parseMode(ParseMode.MARKDOWNV2)
                .text("Ваша цитата: \n$text\nОставляем? Напишите `Да` или `Нет`")
                .build()

            activeDialogStore.addDialog(telegramChatInfo.userId, telegramChatInfo)
            TelegramUtils.sendMessage(message)
        }

        else if (activeDialogStore.getDialog(telegramChatInfo.userId)?.chatId == telegramChatInfo.chatId) {
            when (telegramChatInfo.message.lowercase().trim()) {
                "да", "lf" -> {
                    val options: MutableList<InputPollOption?> = ArrayList()
                    options.add(InputPollOption.builder().text("Блять, я заплакал (8-10 / 10)").build())
                    options.add(InputPollOption.builder().text("Заебись, четка (4-7 / 10)").build())
                    options.add(InputPollOption.builder().text("Давай по новой миша, все хуйня (0-3 / 10)").build())

                    val messageToChannel: SendMessage = SendMessage
                        .builder()
                        .chatId(ConfigLoader.long("bot.suggestions-channel"))
                        .parseMode(ParseMode.MARKDOWNV2)
                        .text(
                            "Новая цитата от @${telegramChatInfo.userName}\n" +
                                    MarkdownParser.parse(activeDialogStore.getDialog(telegramChatInfo.userId)?.message!!, MarkdownParser.Format.QUOTE) +
                                    "\n\\#цитата"
                        )
                        .build()

                    val poll: SendPoll = SendPoll
                        .builder()
                        .questionParseMode(ParseMode.MARKDOWNV2)
                        .question("Мнение о предложении от ${telegramChatInfo.userName}")
                        .allowMultipleAnswers(false)
                        .isAnonymous(false)
                        .options(options)
                        .chatId(ConfigLoader.long("bot.suggestions-channel"))
                        .build()

                    val messageToChat: SendMessage = SendMessage
                        .builder()
                        .chatId(telegramChatInfo.chatId)
                        .parseMode(ParseMode.MARKDOWN)
                        .text("Отлично! Цитата отправлена в предложку, следи за цитатником :)")
                        .build()

                    activeDialogStore.removeDialog(telegramChatInfo.userId)

                    TelegramUtils.sendMessage(messageToChannel)
                    TelegramUtils.sendPoll(poll)
                    TelegramUtils.sendMessage(messageToChat)
                }

                "нет", "ytn" -> {
                    val message: SendMessage = SendMessage
                        .builder()
                        .chatId(telegramChatInfo.chatId)
                        .parseMode(ParseMode.MARKDOWN)
                        .text("Жаль, что вы передумали. Отправка цитаты отменена")
                        .build()

                    activeDialogStore.removeDialog(telegramChatInfo.userId)
                    TelegramUtils.sendMessage(message)
                }

                else -> {
                    val message: SendMessage = SendMessage
                        .builder()
                        .chatId(telegramChatInfo.chatId)
                        .parseMode(ParseMode.MARKDOWN)
                        .text("Напишите `Да` или `Нет`")
                        .build()

                    TelegramUtils.sendMessage(message)
                }
            }
        }
    }

    class ActiveDialogStore {
        private val dialogs = mutableMapOf<String, TelegramChatInfo>()

        // theoretically I can check if chatId contains not only digits (and "-"), but also letters => throw exception or some kind of error
        fun addDialog(chatId: String, telegramChatInfo: TelegramChatInfo) {
            require(chatId.isNotBlank()) { "Chat name must not be blank" }
            dialogs[chatId] = telegramChatInfo
        }

        fun getDialog(chatId: String): TelegramChatInfo? = dialogs[chatId]

        fun removeDialog(chatName: String) = dialogs.remove(chatName)

        fun hasActiveDialog(chatName: String): Boolean = dialogs.containsKey(chatName)
    }
}