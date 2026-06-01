FROM node:26-slim

RUN apt-get update && \
    apt-get install -y python3 python3-pip ffmpeg && \
    pip3 install --break-system-packages yt-dlp && \
    npm install -g pnpm

WORKDIR /witube-api

COPY . .

RUN pnpm install 

CMD ["pnpm", "start"]
