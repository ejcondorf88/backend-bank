# syntax=docker/dockerfile:1

# ============================================================================
# STAGE 1: Build
# ============================================================================
FROM maven:3.9.5-eclipse-temurin-17-alpine AS builder

WORKDIR /build

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests -B

RUN java -Djarmode=layertools -jar target/*.jar extract

# ============================================================================
# STAGE 2: Runtime
# ============================================================================
FROM eclipse-temurin:17-jre-alpine

RUN addgroup -g 1000 appgroup && \
    adduser -u 1000 -G appgroup -s /bin/sh -D appuser

WORKDIR /app

COPY --from=builder --chown=appuser:appgroup /build/dependencies/ ./
COPY --from=builder --chown=appuser:appgroup /build/spring-boot-loader/ ./
COPY --from=builder --chown=appuser:appgroup /build/snapshot-dependencies/ ./
COPY --from=builder --chown=appuser:appgroup /build/application/ ./

USER appuser

EXPOSE 8080

# JVM Memory Settings (can be overridden via JAVA_OPTS env var)
# Default: 256MB heap for API Gateway
ENV JAVA_OPTS="-Xms128m -Xmx256m -Xss1m -XX:MaxMetaspaceSize=128m -XX:+UseContainerSupport"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]
