package org.fcsprepods;

import org.fcsprepods.Util.MarkdownV2Parser;
import org.fcsprepods.Util.MarkdownV2ParserType;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.HashMap;

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

            if (receivedMessage.equals("/start") || receivedMessage.equals("/start@fcs_se_quote_book_bot")) {
                String text = "*Добро пожаловать в предложку [лучшего цитатника](https://t.me/fcsseprepods) ФКН Программной Инженерии* \n\nДля отправки цитаты в предложку используй /suggest";

                SendMessage message = SendMessage
                        .builder()
                        .parseMode(ParseMode.MARKDOWNV2)
                        .chatId(chatId)
                        .text(text)
                        .build();

                this.sendMessage(message);

            } else if (receivedMessage.equals("/suggest") || receivedMessage.equals("/suggest@fcs_se_quote_book_bot")) {
                dialogs.remove(chatId);

                dialogs.put(chatId, "");
                SendMessage message = SendMessage
                        .builder()
                        .parseMode(ParseMode.MARKDOWNV2)
                        .text("Напишите цитату в таком формате:\n>`Цитата`\n>\n>`#Автор цитаты`\nАвтором цитаты может быть не только преподаватель :\\)")
                        .chatId(update.getMessage().getChatId())
                        .build();

                this.sendMessage(message);

            } else if (dialogs.containsKey(chatId) && dialogs.get(chatId).isEmpty()) {
                dialogs.put(chatId, MarkdownV2Parser.parseString(receivedMessage, MarkdownV2ParserType.QUOTE));
                SendMessage message = SendMessage
                        .builder()
                        .chatId(chatId)
                        .parseMode(ParseMode.MARKDOWNV2)
                        .text("Ваша цитата: \n" + MarkdownV2Parser.parseString(receivedMessage, MarkdownV2ParserType.QUOTE) + "\nОставляем? Напишите `Да` или `Нет`")
                        .build();

                this.sendMessage(message);

            } else if (dialogs.containsKey(chatId) && !dialogs.get(chatId).isEmpty()) {
                if (receivedMessage.equals("Да")) {
                    SendMessage messageToChannel = SendMessage
                            .builder()
                            .chatId((long) botConfig.get("channel"))
                            .parseMode(ParseMode.MARKDOWNV2)
                            .text("Новая цитата от @" + update.getMessage().getChat().getUserName() + "\n" + dialogs.get(chatId) + "\n")
                            .build();
                    SendMessage messageToChat = SendMessage
                            .builder()
                            .chatId(chatId)
                            .parseMode(ParseMode.MARKDOWN)
                            .text("Отлично! Цитата отправлена в предложку, следи за цитатником :)")
                            .build();

                    this.sendMessage(messageToChannel);
                    this.sendMessage(messageToChat);

                    dialogs.remove(chatId);
                } else if (receivedMessage.equals("Нет")) {
                    SendMessage message = SendMessage
                            .builder()
                            .chatId(chatId)
                            .parseMode(ParseMode.MARKDOWN)
                            .text("Отправка цитаты в предложку отменена..")
                            .build();

                    this.sendMessage(message);

                    dialogs.remove(chatId);
                } else {
                    SendMessage message = SendMessage
                            .builder()
                            .chatId(chatId)
                            .parseMode(ParseMode.MARKDOWN)
                            .text("Пожалуйста, напишите `Да` или `Нет`")
                            .build();

                    this.sendMessage(message);
                }
            } else {
                SendMessage message = SendMessage
                        .builder()
                        .parseMode(ParseMode.MARKDOWN)
                        .text("Неизвестная команда, попробуйте\n/suggest\n/start")
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
