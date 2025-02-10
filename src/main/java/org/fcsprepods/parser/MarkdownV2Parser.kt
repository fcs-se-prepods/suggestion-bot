package org.fcsprepods.parser

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
    @Deprecated("Need to be replaced with better parser")
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
        return message
    }

    class MarkdownV2ParserException(message: String??) : Exception(message)
}
