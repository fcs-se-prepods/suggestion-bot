package org.fcsprepods.util

import org.fcsprepods.util.MarkdownV2Parser.MarkdownV2ParserException
import java.lang.Exception

/**
 * Class that includes methods to parse a message (line) to be safe for Markdown V2 standard
 */
object MarkdownV2Parser {
    /**
     * Parses provided message, removing symbols that are reserved by MarkdownV2
     * @param message message to parse
     * @return Parsed message as a quote
     */
    @Throws(MarkdownV2ParserException::class)
    fun parseString(message: String, type: MarkdownV2ParserType): String {
        var message = message
        val symbolsToReplace =
            arrayOf<String?>("\\", "<", ")", "(", "[", "]", "-", "/", "+", "!", "?", "#", "@", "%", ".", ",")

        if (!message.contains("#")) throw MarkdownV2ParserException("Вы не указали автора цитаты, используя `#`!")

        when (type) {
            MarkdownV2ParserType.QUOTE -> {
                for (element in symbolsToReplace) {
                    message = message.replace(element.toString(), "\\" + element)
                }

                message = ">" + message.replace("\n", "\n>")
            }

            MarkdownV2ParserType.DEFAULT -> {
            }
        }
        println(message)
        return message
    }

    class MarkdownV2ParserException(override var message: String?) : Exception()
}
