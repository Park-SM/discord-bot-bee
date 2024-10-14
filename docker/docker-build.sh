docker build -t smparkworld/discord-bot-bee:1.0 \
  --build-arg BUILD_DATE=`date -u +"%Y-%m-%dT%H:%M:%SZ"` \
  --build-arg VERSION=1.0 \
  --build-arg BOT_TOKEN=$BOT_TOKEN \
  ../../