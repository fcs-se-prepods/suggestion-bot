package org.fcsprepods

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

object Main {
    private val LOGGER: Logger = LoggerFactory.getLogger(Main::class.java)
    private var config: HashMap<String, Any>? = null

    fun main(args: Array<String>) {
        config = loadConfig()
        if (config == null) {
            LOGGER.atError().log("config.yml not found. Contact the developer. Startup aborted")
            return
        }

        val botConfig = config!!["bot"] as HashMap<*, *>?

        val botToken = botConfig!!["token"] as String?

        try {
            TelegramBotsLongPollingApplication().use { botsApplication ->
                botsApplication.registerBot(botToken, SuggestionBot(botToken!!))
                println("@fcs_se_quote_book_bot successfully started!")
                Thread.currentThread().join()
            }
        } catch (ex: Exception) {
            println(ex.message)
        }
    }

    private fun loadConfig(): HashMap<String, Any>? {
        val file = File("./config.yml")

        if (!file.exists()) {
            try {
                Thread.currentThread().contextClassLoader.getResourceAsStream("config.yml").use { inputStream ->
                    if (inputStream == null) {
                        println("config.yml not found. Contact the developer")
                        return null
                    }
                    Files.copy(inputStream, Path.of("./config.yml"), StandardCopyOption.REPLACE_EXISTING)
                }
            } catch (e: Exception) {
                println(e.message)
            }
        }

        val yaml = Yaml()
        try {
            FileInputStream("./config.yml").use { input ->
                return yaml.load(input)
            }
        } catch (ex: IOException) {
            println(ex.message)
        }
        return null
    }

    fun getConfig(): Map<String, Any> {
        return config!!
    }
}
