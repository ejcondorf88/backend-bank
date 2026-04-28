# syntax=docker/dockerfile:1

# ============================================================================
# STAGE 1: Build
# ============================================================================
FROM maven:3.8.6-eclipse-temurin-8-alpine AS builder

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
FROM eclipse-temurin:8-jre-alpine

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

# Expose port (Eureka typically uses 8761)
EXPOSE 8761

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8761/actuator/health || exit 1

# Run the application using the Spring Boot launcher
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
