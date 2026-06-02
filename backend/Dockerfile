# 1. Usamos Node 22 (LTS), que es la versión más estable y compatible actual
FROM node:22-bookworm-slim

# 2. Instalamos dependencias del sistema (sin python3-pip para optimizar espacio)
RUN apt-get update \
    && apt-get install -y --no-install-recommends python3 ffmpeg curl ca-certificates \
    && curl -L https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp -o /usr/local/bin/yt-dlp \
    && chmod a+rx /usr/local/bin/yt-dlp \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# 3. Instalamos pnpm globalmente de manera limpia
RUN npm install -g pnpm

WORKDIR /app

# 4. Copiamos package.json Y pnpm-lock.yaml antes de instalar
COPY package.json pnpm-lock.yaml ./

# 5. Instalamos solo dependencias de producción (--prod) de manera exacta
RUN pnpm install --prod --frozen-lockfile

# 6. Copiamos el resto del código
COPY . .

EXPOSE 3000

# 7. Ejecutamos node directamente en vez de usar npm/pnpm para evitar conflictos con devEngines
CMD ["node", "server-yt-dlp.js"]
