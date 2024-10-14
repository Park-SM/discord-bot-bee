FROM openjdk:17-alpine

WORKDIR /usr/src/app

ARG JAR_PATH=./build/libs
ARG BOT_TOKEN

ENV BOT_TOKEN=${BOT_TOKEN}

USER 0

COPY ${JAR_PATH}/discord-bot-bee.jar ${JAR_PATH}/discord-bot-bee.jar

CMD ["sh", "-c", "java -jar ./build/libs/discord-bot-bee.jar ${BOT_TOKEN}"]
