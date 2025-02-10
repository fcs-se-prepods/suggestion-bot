package org.fcsprepods

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okio.FileNotFoundException
import org.fcsprepods.data.FileDataManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication
import kotlin.system.exitProcess

object Application {
    private val logger: Logger = LoggerFactory.getLogger(Application::class.java)
    private var config: Map<String, Any?>? = null

    init {
        try {
            this.config = FileDataManager.readData("./config.yml")
            if (this.config == null) {
                logger.error("config.yml not found or empty. Startup aborted")
                exitProcess(1)
            }
        } catch (ex: FileNotFoundException) {
            logger.error("An error occurred during copying configuration file: ${ex.message}. Startup aborted")
            exitProcess(1)
        }
    }

    val botConfig: Map<String, *> = config?.get("bot") as? HashMap<String, *> ?: error("")
    val token: String = botConfig["token"] as String
    val suggestionChannel: String = botConfig["channel"] as String

    val suggestionBot = SuggestionBot(token)


    @JvmStatic
    fun main(args: Array<String>) {
        runBot()
    }

    @JvmStatic
    fun runBot() = runBlocking {
        logger.info("Starting @fcs_se_quote_book_bot...")

        launch {
            TelegramBotsLongPollingApplication().use { botsApplication ->
                botsApplication.registerBot(token, suggestionBot)
                println("Bot is running!")
            }
        }
    }
}
