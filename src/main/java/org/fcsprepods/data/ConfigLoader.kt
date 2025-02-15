package org.fcsprepods.data

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.system.exitProcess

object ConfigLoader {
    private val logger: Logger = LoggerFactory.getLogger(ConfigLoader::class.java)
    private val yaml = Yaml()
    private var config: Map<String, Any?>

    init {
        try {
            val configFile = File("./config.yml")

            if (!configFile.exists()) {
                // throws exception if file not found
                logger.info("Copying default configuration fields...")

                var inputStream: InputStream = this.javaClass.getResourceAsStream("/config.yml")
                    ?: error("config.yml not found in resources")

                inputStream.use {
                    Files.copy(inputStream, Path.of("/config.yml"), StandardCopyOption.REPLACE_EXISTING)
                }

                logger.info("config.yml successfully created!")
            }

            configFile.inputStream().use { input ->
                config = yaml.loadAs<Map<String, Any>>(input, Map::class.java) ?: error("Wrong config.yml format")
            }
        } catch (ex: Exception) {
            logger.error("An error occurred during copying configuration file: ${ex.message}. Startup aborted")
            exitProcess(1)
        }
    }

    fun string(path: String): String {
        try {
            return getValue(path) as String
        } catch (ex: Exception) {
            logger.error("Error occurred while getting string value from config: ${ex.message}")
            exitProcess(1)
        }
    }

    fun long(path: String): Long {
        try {
            return getValue(path) as Long
        } catch (ex: Exception) {
            logger.error("Error occurred while getting long value from config: ${ex.message}")
            exitProcess(1)
        }
    }

    private fun getValue(path: String): Any? {
        val keys = path.split(".")
        var value: Any? = config

        for (key in keys) {
            if (value is Map<*, *>) {
                value = value[key] ?: return null
            } else {
                return null
            }
        }

        return value
    }
}