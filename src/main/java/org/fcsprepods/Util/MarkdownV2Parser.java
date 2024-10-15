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
    public static @NotNull String parseString(@NotNull String message, @NotNull MarkdownV2ParserType type) throws MarkdownV2ParserException {
        String[] symbolsToReplace = {"\\", "<", ")", "(", "[", "]", "-", "/", "+", "!", "?", "#", "@", "%", ".", ","};

        if (!message.contains("#")) throw new MarkdownV2ParserException("Вы не указали автора цитаты, используя `#`!");

        switch (type) {
            case QUOTE -> {
                for (String element : symbolsToReplace) {
                    message = message.replace(element, "\\" + element);
                }

                message = ">" + message.replace("\n", "\n>");
            }

            case DEFAULT -> {

            }
        }
        System.out.println(message);
        return message;
    }

    public static class MarkdownV2ParserException extends Exception {
        String message;
        public MarkdownV2ParserException(String message) {
            this.message = message;
        }

        @Override
        public String getMessage() {
            return this.message;
        }
    }
}
