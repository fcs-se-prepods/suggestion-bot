package org.fcsprepods

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.fcsprepods.data.ConfigLoader
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication
import java.text.SimpleDateFormat
import java.util.TimeZone
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

    @JvmStatic
    fun runBot() = runBlocking {
        val started = System.currentTimeMillis()
        logger.info("Starting @fcs_se_quote_book_bot...")

        val bot = launch(Dispatchers.IO) {
            TelegramBotsLongPollingApplication().use { botsApplication ->
                botsApplication.registerBot(token, suggestionBot)
                logger.info("Bot is running!")

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
                    "status" -> {
                        val uptime = System.currentTimeMillis() - started
                        val hours = (uptime / (1000 * 60 * 60)) % 24
                        val minutes = (uptime / (1000 * 60)) % 60
                        val seconds = (uptime / 1000) % 60
                        logger.info("Bot is running for ${"%02d:%02d:%02d".format(hours, minutes, seconds)}")
                    }
                    else -> logger.info("Unknown command!")
                }
            }
        }

        bot.join()
        consoleInput.cancel()
    }
}
