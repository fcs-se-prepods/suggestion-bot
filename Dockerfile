FROM openjdk:21-slim

WORKDIR /app

COPY build/libs/suggestion-bot-v1.1.5-release.jar suggestion-bot.jar

COPY config.yml config.yml

EXPOSE 8089

ENTRYPOINT ["java", "-jar", "suggestion-bot.jar"]
