FROM openjdk:17-alpine

WORKDIR /usr/src/app

ARG JAR_PATH=./build/libs
ARG BOT_TOKEN

USER 0

COPY ${JAR_PATH}/discord-bot-bee.jar ${JAR_PATH}/discord-bot-bee.jar

CMD ["java","-jar","./build/libs/discord-bot-bee.jar", "${BOT_TOKEN}"]
