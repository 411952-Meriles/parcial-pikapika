# Stage 1: Build Angular app
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

# Stage 2: NGINX to serve static files
FROM node:20-alpine
WORKDIR /app
# Instala un servidor estático ligero
RUN npm install -g serve
# Copia la salida de build (ajusta la ruta si tu dist es diferente)
COPY --from=build /app/dist/frontend/browser /app/dist
EXPOSE 4201
CMD ["serve", "-s", "/app/dist", "-l", "4201"]
