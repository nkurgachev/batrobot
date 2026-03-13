# syntax=docker/dockerfile:1

FROM gradle:8.10.2-jdk21 AS builder
WORKDIR /workspace

# Copy full multi-module source tree (filtered by .dockerignore)
# so Gradle can resolve all included projects during image build.
COPY . .

RUN chmod +x gradlew && ./gradlew --no-daemon bootJar

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S app && adduser -S app -G app

COPY --from=builder /workspace/build/libs/*.jar /app/app.jar

RUN mkdir -p /app/data && chown -R app:app /app

USER app

ENV JAVA_OPTS="-XX:MaxRAMPercentage=70.0 -XX:InitialRAMPercentage=20.0 -Dfile.encoding=UTF-8"

EXPOSE 8081
VOLUME ["/app/data"]

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
