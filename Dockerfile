FROM gradle:8.8.0-jdk21-alpine AS builder

WORKDIR /app

COPY /app .

RUN gradle installDist --no-daemon -x test

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/build/install/app ./

EXPOSE 8080

CMD ["./bin/app"]
