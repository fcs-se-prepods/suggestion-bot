package org.fcsprepods.wrapper

import org.telegram.telegrambots.meta.api.objects.Update

data class TelegramChatInfo(
    val userId: String,
    val userName: String,
    val chatId: String,
    val chatName: String,
    val chatType: String,
    val message: String
) {
    constructor(update: Update) : this(
        userId = update.message.from.id.toString(),
        userName = update.message.from.userName ?: update.message.from.firstName,
        chatId = update.message.chatId.toString(),
        chatName = update.message.chat.userName ?: update.message.chat.title,
        chatType = update.message.chat.type,
        message = update.message.text
    )
}