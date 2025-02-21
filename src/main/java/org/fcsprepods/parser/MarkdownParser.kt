package org.fcsprepods.parser

/**
 * Class that includes methods to parse a message (line) to be safe for Markdown V2 standard
 */
object MarkdownParser {
    private val symbols = setOf('_', '*', '[', ']', '(', ')', '~', '>', '`', '#', '+', '-', '=', '|', '{', '}', '.', '!')

    fun parse(input: String, format: Format, lang: String? = null): String {
        val escapedText = buildString {
            input.forEachIndexed { index, char ->
                if (char in symbols) {
                    if (index == 0 || input[index - 1] != '\\') {
                        append("\\")
                    }
                }
                append(char)
            }
        }

        return if (format == Format.CODE_BLOCK && lang != null) {
            format.apply(escapedText, lang)
        } else {
            format.apply(escapedText)
        }
    }

    enum class Format {
        PLAIN {
            override fun apply(text: String) = text
        },

        QUOTE {
            override fun apply(text: String) =
                text.lines().joinToString("\n>") { ">$it" }
        },

        CODE_BLOCK {
            override fun apply(text: String) = "```\n$text\n```"
            override fun apply(text: String, lang: String) = "```${lang}\n${text}\n```"
        };

        abstract fun apply(text: String): String
        open fun apply(text: String, lang: String): String {
            throw UnsupportedOperationException("This format does not support a language parameter.")
        }
    }
}
