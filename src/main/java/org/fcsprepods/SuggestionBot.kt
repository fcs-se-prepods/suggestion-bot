package org.fcsprepods

import org.fcsprepods.command.*
import org.fcsprepods.wrapper.TelegramChatInfo
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.generics.TelegramClient

class SuggestionBot(token: String) : LongPollingSingleThreadUpdateConsumer {
    val telegramClient: TelegramClient = OkHttpTelegramClient(token)

    override fun consume(update: Update) {
        if (update.message.text == null || update.message.chat.type.lowercase().trim() == "group") return
        val telegramChatInfo = TelegramChatInfo(update)

        println(telegramChatInfo)

        CommandRoute.fromAlias(telegramChatInfo.message).context.execute(telegramChatInfo)
    }
}