FROM ghcr.io/openfaas/of-watchdog:0.10.6 AS watchdog
FROM node:20-alpine

WORKDIR /home/app

COPY package*.json ./
RUN npm install --omit=dev

COPY src ./src

COPY --from=watchdog /fwatchdog /usr/bin/fwatchdog

ENV mode="http"
ENV upstream_url="http://127.0.0.1:3000"
ENV cgi_headers="true"
ENV function_process="node src/index.js"

EXPOSE 8080

HEALTHCHECK --interval=5s CMD ["wget", "--spider", "-q", "http://127.0.0.1:3000/healthz"]

CMD ["fwatchdog"]
