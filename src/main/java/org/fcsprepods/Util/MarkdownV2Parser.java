package org.fcsprepods.Util;

import org.jetbrains.annotations.NotNull;

/**
 * Class that includes methods to parse a message (line) to be safe for Markdown V2 standard
 */
public class MarkdownV2Parser {
    /**
     * Parses provided message, removing symbols that are reserved by MarkdownV2
     * @param message message to parse
     * @return Parsed message as a quote
     */
    public static @NotNull String parseString(@NotNull String message, @NotNull MarkdownV2ParserType type) {
        switch (type) {
            case QUOTE -> {
                String[] symbolsToReplace = {"<", ")", "(", "[", "]", "-", "/", "+", "!", "?", "#", "@", "%"};
                for (String element : symbolsToReplace) {
                    message = message.replace(element, "\\" + element);
                }

                message = ">" + message.replace("\n", "\n>");
            }
        }
        return message;
    }
}
