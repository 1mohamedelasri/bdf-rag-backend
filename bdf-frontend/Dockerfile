# Stage 1: Build Angular App
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
# Build for production
RUN npm run build

# Stage 2: Serve with Nginx
FROM nginx:alpine
# Copy build output to Nginx html folder
# CHANGE THIS LINE BELOW (removed "-rag-")
COPY --from=build /app/dist/bdf-frontend/browser /usr/share/nginx/html
# Copy a simple nginx config to handle Angular routing (optional but recommended)
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
