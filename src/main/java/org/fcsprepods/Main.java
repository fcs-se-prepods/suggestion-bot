package org.fcsprepods;

import org.checkerframework.checker.signature.qual.BinaryNameOrPrimitiveType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

public class Main {
    private static final @NotNull Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static HashMap<String, Object> config;

    public static void main(String[] args) {
        config = loadConfig();
        if (config == null) {
            LOGGER.atError().log("config.yml not found. Contact the developer. Startup aborted");
            return;
        }

        @SuppressWarnings("unchecked")   // Аннотацию стоит убрать и добавить проверку на корректность config.yml
        HashMap<String, Object> botConfig = (HashMap<String, Object>) config.get("bot");

        String botToken = (String) botConfig.get("token");

        try (TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication()) {
            botsApplication.registerBot(botToken, new SuggestionBot(botToken));
            System.out.println("@fcs_se_quote_book_bot successfully started!");
            Thread.currentThread().join();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static @Nullable HashMap<String, Object> loadConfig() {
        File file = new File("./config.yml");

        if (!file.exists()) {
            try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.yml")) {
                if (inputStream == null) {
                    System.out.println("config.yml not found. Contact the developer");
                    return null;
                }
                Files.copy(inputStream, Path.of("./config.yml"), StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        Yaml yaml = new Yaml();
        try (InputStream input = new FileInputStream("./config.yml")) {
            return yaml.load(input);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

    public static @NotNull Map<String, Object> getConfig() {
        return config;
    }
}
