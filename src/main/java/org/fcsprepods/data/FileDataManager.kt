package org.fcsprepods.data

import okio.FileNotFoundException
import org.fcsprepods.Application
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

object FileDataManager {
    // only .yml files are supported
    @JvmStatic
    fun readData(path: String): HashMap<String, Any?>? {
        val file = File(path)

        if (!file.exists()) {
            // throws exception if file not found
            println("Copying default configuration fields...")

            var inputStream: InputStream = this.javaClass.getResourceAsStream("/config.yml")
                ?: throw FileNotFoundException("config.yml not found in resources")

            inputStream.use {
                Files.copy(inputStream, Path.of(path), StandardCopyOption.REPLACE_EXISTING)
            }

            println("config.yml successfully created!")
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
}