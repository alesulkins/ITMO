FROM node:20-alpine
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm install
RUN npm install -g serve
COPY . .
ARG REACT_APP_API_BASE_URL=http://localhost:8080/api
RUN npm run build
EXPOSE 3000
CMD ["serve", "-s", "build", "-l", "3000"]
