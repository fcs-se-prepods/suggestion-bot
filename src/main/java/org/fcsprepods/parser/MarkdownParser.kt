package org.fcsprepods.parser

/**
 * Class that includes methods to parse a message (line) to be safe for Markdown V2 standard
 */
object MarkdownParser {
    fun parse(input: String): Message {
        var input = input.replace("\\", "\\\"")

        val symbols = arrayOf("_", "*", "[", "]", "(", ")", "~", ">", "`", "#", "+", "-", "=", "|", "{", "}", ".", "!")
        for (element in symbols) {
            input = input
                .replace("\\$element", "ALREADY_PARSED_ELEMENT")
                .replace(element, "\\" + element)
                .replace("ALREADY_PARSED_ELEMENT", "\\" + element)
        }

        return Message(input)
    }

    class Message(var text: String) {
        fun quote(): String = ">" + this.text.replace("\n", "\n>")
        fun codeBlock(lang: String): String = "```${lang}\n${text}```"
        fun string(): String = this.text
    }
}
