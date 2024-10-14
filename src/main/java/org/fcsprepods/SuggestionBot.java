package org.fcsprepods;

import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.HashMap;
import java.util.Map;

public class SuggestionBot implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;
    private final @NotNull HashMap<Long, String> dialogs = new HashMap<>();

    private final @NotNull @SuppressWarnings("unchecked") HashMap<String, Object> botConfig = (HashMap<String, Object>) Main.getConfig().get("bot");

    public SuggestionBot(@NotNull String token) {
        this.telegramClient = new OkHttpTelegramClient(token);
    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String receivedMessage = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            if (receivedMessage.equals("/suggest") || receivedMessage.equals("/suggest@fcs_se_quote_book_bot")) {
                dialogs.remove(chatId);

                dialogs.put(chatId, "");
                SendMessage message = SendMessage
                        .builder()
                        .text("Напишите цитату!!!")
                        .chatId(update.getMessage().getChatId())
                        .build();

                this.sendMessage(message);
            } else if (dialogs.containsKey(chatId) && dialogs.get(chatId).isEmpty()) {
                dialogs.put(chatId, receivedMessage);
                SendMessage message = SendMessage
                        .builder()
                        .chatId(chatId)
                        .text("Ваша цитата: " + receivedMessage + "\nОставляем?")
                        .build();

                this.sendMessage(message);
            } else if (dialogs.containsKey(chatId) && !dialogs.get(chatId).isEmpty()) {
                if (receivedMessage.equals("Да")) {
                    SendMessage message = SendMessage
                            .builder()
                            .chatId((long) botConfig.get("channel"))
                            .text(dialogs.get(chatId))
                            .build();

                    sendMessage(message);
                    System.out.println("posted");
                } else if (receivedMessage.equals("Нет")) {
                    System.out.println("removed");
                }
                dialogs.remove(chatId);
            } else {
                SendMessage message = SendMessage
                        .builder()
                        .text("В душе не ебу че ты мне написал")
                        .chatId(update.getMessage().getChatId())
                        .build();

                this.sendMessage(message);
            }
        }
    }

    private void sendMessage(@NotNull SendMessage message) {
        try {
            telegramClient.execute(message);
        } catch (TelegramApiException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
