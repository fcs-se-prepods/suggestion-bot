package org.fcsprepods.command

import org.fcsprepods.command.handler.*

enum class CommandRoute(val aliases: Set<String>, val context: CommandContext) {
    START(setOf("/start", "/start@fcs_se_quote_book_bot"), context = StartCommandHandler),
    HELP(setOf("/help", "/help@fcs_se_quote_book_bot"), context = HelpCommandHandler),
    SUGGEST(setOf("/suggest", "/suggest@fcs_se_quote_book_bot"), context = SuggestCommandHandler),
    SUPPORT(setOf("/support", "/support@fcs_se_quote_book_bot"), context = SupportCommandHandler),
    UNKNOWN(emptySet(), context = UnknownCommandHandler);

    companion object {
        fun fromAlias(alias: String): CommandRoute {
            return CommandRoute.entries.firstOrNull { route -> route.aliases.contains(alias) } ?: UNKNOWN
        }
    }
}