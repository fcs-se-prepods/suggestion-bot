package org.fcsprepods

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.fcsprepods.data.ConfigLoader
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication
import kotlin.system.exitProcess

object Application {
    private val logger: Logger = LoggerFactory.getLogger(Application::class.java)

    val token: String = ConfigLoader.getString("bot.token")
    val suggestionBot = SuggestionBot(token)

    @JvmStatic
    fun main(args: Array<String>) {
        runBot()
        exitProcess(0)
    }

    // coroutine should be used here one day :)
    @JvmStatic
    fun runBot() = runBlocking {
        logger.info("Starting @fcs_se_quote_book_bot...")

        val bot = launch(Dispatchers.IO) {
            TelegramBotsLongPollingApplication().use { botsApplication ->
                botsApplication.registerBot(token, suggestionBot)
                logger.info("Bot is running!")

                // Держим корутину активной, пока бот работает
                awaitCancellation()
            }
        }

        val consoleInput = launch {
            while (true) {
                logger.info("Enter command (type 'exit' to stop): ")
                when (readlnOrNull()?.trim()?.lowercase()) {
                    "exit" -> {
                        logger.info("Stopping bot...")
                        bot.cancel()
                        break
                    }
                    "status" -> logger.info("Bot is running...") // bot's uptime
                    else -> logger.info("Unknown command!")
                }
            }
        }

        bot.join()
        consoleInput.cancel()
    }
}
