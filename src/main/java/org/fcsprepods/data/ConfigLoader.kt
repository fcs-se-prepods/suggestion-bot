package org.fcsprepods.data

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import kotlin.system.exitProcess

object ConfigLoader {
    val logger: Logger = LoggerFactory.getLogger(ConfigLoader::class.java)
    private val yaml = Yaml()
    var config: Map<String, Any?> = emptyMap()

    init {
        kotlin.runCatching {
            val configFile = File("./config.yml")

            if (!configFile.exists()) {
                logger.info("Copying default config.yml...")

                this::class.java.getResourceAsStream("/config.yml")?.use { inputStream ->
                    Files.copy(inputStream, configFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
                } ?: throw IllegalStateException("config.yml not found in resources")
            }

            config = configFile.inputStream().use { yaml.load<Map<String, Any>>(it) ?: emptyMap() }
        }.onFailure {
            logger.error("An error occurred during copying configuration file: ${it.message}. Startup aborted")
            exitProcess(1)
        }

    }

    inline fun <reified T> get(path: String, default: T? = null): T = kotlin.runCatching {
        val value = path.split(".").fold(config as? Any) { acc, key -> if (acc is Map<*, *>) acc[key] else null }
        if (value is T) value else default ?: throw IllegalStateException("Value at path $path is not of type ${T::class.simpleName} or does not exist")
    }.getOrElse {
        logger.error("An error occurred while getting value at path $path: ${it.message}")
        LogManager.log(it, "An error occurred while getting value at path $path: ${it.message}")
        default ?: null as T
    }
}