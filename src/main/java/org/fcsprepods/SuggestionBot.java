package org.fcsprepods;

import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public class SuggestionBot implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;
    public SuggestionBot(@NotNull String token) {
        this.telegramClient = new OkHttpTelegramClient(token);
    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String receivedMessage = update.getMessage().getText();
            if (receivedMessage.equals("/suggest") || receivedMessage.equals("/suggest@fcs_se_quote_book_bot")) {
                SendMessage message = SendMessage
                        .builder()
                        .text("Предъяви мне")
                        .chatId(update.getMessage().getChatId())
                        .build();
                try {
                    telegramClient.execute(message);
                    System.out.println(update.getMessage().getChatId());
                } catch (TelegramApiException ex) {
                    System.out.println(ex.getMessage());
                }
            } else {
                SendMessage message = SendMessage
                        .builder()
                        .text("В душе не ебу че ты мне написал")
                        .chatId(update.getMessage().getChatId())
                        .build();
                try {
                    telegramClient.execute(message);
                } catch (TelegramApiException ex) {
                    System.out.println(ex.getMessage());
                }
            }

            SendMessage message = SendMessage
                    .builder()
                    .chatId(-1002471777184L)
                    .text("ГОЙДА")
                    .build();
            try {
                telegramClient.execute(message);
            } catch (TelegramApiException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

}
