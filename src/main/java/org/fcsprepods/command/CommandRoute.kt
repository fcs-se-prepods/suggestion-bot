package org.fcsprepods.command

import org.fcsprepods.command.handler.*

enum class CommandRoute(vararg val aliases: String, val context: CommandContext) {
    START("/start", "/start@fcs_se_quote_book_bot", context = StartCommandHandler),
    HELP("/help", "/help@fcs_se_quote_book_bot", context = HelpCommandHandler),
    SUGGEST("/suggest", "/suggest@fcs_se_quote_book_bot", context = SuggestCommandHandler),
    SUPPORT("/support", "/support@fcs_se_quote_book_bot", context = SupportCommandHandler),
    UNKNOWN(context = UnknownCommandHandler);

    companion object {
        fun fromAlias(alias: String): CommandRoute {
            return CommandRoute.entries.firstOrNull { route -> route.aliases.contains(alias) } ?: UNKNOWN
        }
    }
}