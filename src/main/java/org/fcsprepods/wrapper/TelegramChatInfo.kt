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
    constructor(update: Update?) : this(
        userId = update?.message?.from?.id?.toString() ?: "unknown",
        userName = update?.message?.from?.userName ?: update?.message?.from?.firstName ?: "unknown",
        chatId = update?.message?.chatId?.toString() ?: "unknown",
        chatName = update?.message?.chat?.userName ?: update?.message?.chat?.title ?: "unknown",
        chatType = update?.message?.chat?.type ?: "unknown",
        message = update?.message?.text ?: "unknown"
    )

    override fun toString(): String {
        return "TelegramChatInfo {\n" +
                "    userId='$userId',\n" +
                "    userName='$userName',\n" +
                "    chatId='$chatId',\n" +
                "    chatName='$chatName',\n" +
                "    chatType='$chatType',\n" +
                "    message='$message'\n" +
                "}"
    }

    companion object {
        fun fallback() = TelegramChatInfo(update = null)
    }
}