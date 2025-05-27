# 1단계: 빌드
FROM node:18-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm install --frozen-lockfile
COPY . .
ARG VITE_API_BASE_URL
ARG VITE_KAKAO_CLIENT_ID
ARG VITE_KAKAO_REDIRECT_URI
ENV VITE_API_BASE_URL=http://localhost:8080/api
ENV VITE_KAKAO_CLIENT_ID=b83227d4cddb2c0bc786056e2917f961
ENV VITE_KAKAO_REDIRECT_URI=http://localhost:5173/kakao
RUN npm run build

# 2단계: Nginx로 정적 파일 서빙
FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"] 