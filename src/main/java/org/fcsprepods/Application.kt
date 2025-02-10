package org.fcsprepods

import org.fcsprepods.data.ConfigLoader
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication

object Application {
    private val logger: Logger = LoggerFactory.getLogger(Application::class.java)

    val token: String = ConfigLoader.getString("bot.token")
    val suggestionBot = SuggestionBot(token)

    @JvmStatic
    fun main(args: Array<String>) {
        runBot()
    }

    // coroutine should be used here one day :)
    @JvmStatic
    fun runBot() {
        logger.info("Starting @fcs_se_quote_book_bot...")

        TelegramBotsLongPollingApplication().use { botsApplication ->
            botsApplication.registerBot(token, suggestionBot)
            logger.info("Bot is running!")
            Thread.currentThread().join()
        }
    }
}
