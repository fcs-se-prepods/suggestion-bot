package org.fcsprepods.command

import org.fcsprepods.wrapper.TelegramChatInfo

interface CommandContext {
    fun execute(telegramChatInfo: TelegramChatInfo)
}