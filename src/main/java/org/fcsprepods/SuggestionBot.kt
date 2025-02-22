package org.fcsprepods

import org.fcsprepods.command.*
import org.fcsprepods.data.LogManager
import org.fcsprepods.wrapper.TelegramChatInfo
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.generics.TelegramClient

class SuggestionBot(token: String) : LongPollingSingleThreadUpdateConsumer {
    val telegramClient: TelegramClient = OkHttpTelegramClient(token)

    override fun consume(update: Update) {
        var telegramChatInfo: TelegramChatInfo? = null
        try {
            if (update.message?.text == null || update.message.chat?.type?.lowercase()?.trim() == "group") return

            telegramChatInfo = TelegramChatInfo(update)

            CommandRoute.fromAlias(telegramChatInfo.message).context.execute(telegramChatInfo)
        } catch (e: Exception) {
            val fallbackChatInfo = telegramChatInfo ?: TelegramChatInfo.fallback()
            LogManager.log(e, fallbackChatInfo)
        }
    }
}