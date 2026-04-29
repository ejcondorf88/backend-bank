# syntax=docker/dockerfile:1

# ============================================================================
# STAGE 1: Build
# ============================================================================
FROM maven:3.9.5-eclipse-temurin-17-alpine AS builder

WORKDIR /build

# Copy only pom.xml first to cache dependencies layer
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests -B

# Extract layers for Spring Boot Layered JAR (optimized caching)
RUN java -Djarmode=layertools -jar target/*.jar extract

# ============================================================================
# STAGE 2: Runtime
# ============================================================================
FROM eclipse-temurin:17-jre-alpine

# Create non-root user for security
RUN addgroup -g 1000 appgroup && \
    adduser -u 1000 -G appgroup -s /bin/sh -D appuser

WORKDIR /app

# Copy extracted layers (dependencies first for better caching)
COPY --from=builder --chown=appuser:appgroup /build/dependencies/ ./
COPY --from=builder --chown=appuser:appgroup /build/spring-boot-loader/ ./
COPY --from=builder --chown=appuser:appgroup /build/snapshot-dependencies/ ./
COPY --from=builder --chown=appuser:appgroup /build/application/ ./

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8082

# JVM Memory Settings (can be overridden via JAVA_OPTS env var)
# Default: 512MB heap for business microservice with database
ENV JAVA_OPTS="-Xms256m -Xmx512m -Xss1m -XX:MaxMetaspaceSize=128m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Run the application using the Spring Boot launcher
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]
