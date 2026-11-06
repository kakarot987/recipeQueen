FROM node:22.22.3-bookworm-slim AS build
WORKDIR /app
COPY package*.json .npmrc ./
RUN npm install --no-audit --no-fund
COPY . .
RUN npm run build

FROM nginx:stable-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist/QueenKitchen/browser /usr/share/nginx/html
EXPOSE 80
