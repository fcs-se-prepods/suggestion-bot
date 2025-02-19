package org.fcsprepods

import kotlinx.coroutines.*
import org.fcsprepods.data.ConfigLoader
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication
import kotlin.system.exitProcess

object Application {
    private val logger: Logger = LoggerFactory.getLogger(Application::class.java)
    private val token: String = ConfigLoader.string("bot.token")
    val suggestionBot = SuggestionBot(token)

    @JvmStatic
    fun main(args: Array<String>) {
        runBot()
    }

    private fun runBot() = runBlocking {
        val started = System.currentTimeMillis()
        logger.info("Starting @fcs_se_quote_book_bot...")

        supervisorScope {
            val botScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

            val botsApplication = TelegramBotsLongPollingApplication()

            val botJob = botScope.launch {
                try {
                    botsApplication.registerBot(token, suggestionBot)
                    logger.info("Bot is running!")
                    awaitCancellation()
                } catch (ex: Exception) {
                    logger.error("An error occurred while running the bot: ${ex.message}", ex)
                }
            }

            val consoleJob = launch(Dispatchers.IO) {
                while (isActive) {
                    logger.info("Enter command (type 'exit' to stop, 'status' to show bot's uptime): ")
                    when (readlnOrNull()?.trim()?.lowercase()) {
                        "exit" -> {
                            logger.info("Stopping bot...")
                            botScope.cancel()
                            botsApplication.unregisterBot(token)
                            botsApplication.close()
                            exitProcess(0)
                        }
                        "status" -> {
                            val uptime = System.currentTimeMillis() - started
                            val hours = (uptime / (1000 * 60 * 60))
                            val minutes = (uptime / (1000 * 60)) % 60
                            val seconds = (uptime / 1000) % 60
                            logger.info("Bot is running for ${"%02d:%02d:%02d".format(hours, minutes, seconds)}")
                        }
                        else -> logger.warn("Unknown command!")
                    }
                }
            }

            botJob.join()
            consoleJob.cancelAndJoin()
        }
    }
}
