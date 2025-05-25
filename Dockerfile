# Optimized Dockerfile for Railway deployment
FROM eclipse-temurin:21-jdk-alpine AS builder

# Set working directory
WORKDIR /app

# Copy Maven files for dependency caching
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw mvnw.cmd ./

# Make mvnw executable
RUN chmod +x ./mvnw

# Download dependencies (this layer will be cached)
RUN ./mvnw dependency:go-offline -B --no-transfer-progress --quiet

# Copy source code
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests -B --no-transfer-progress --quiet

# Runtime stage - minimal image
FROM eclipse-temurin:21-jre-alpine

# Install wget for health checks
RUN apk add --no-cache wget

# Create non-root user
RUN addgroup -g 1001 appgroup && adduser -u 1001 -G appgroup -s /bin/sh -D appuser

WORKDIR /app

# Copy the jar file
COPY --from=builder /app/target/*.jar app.jar

# Change ownership
RUN chown appuser:appgroup app.jar

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8080

# Health check for Railway
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Optimized JVM settings for Railway's memory constraints
ENV JAVA_OPTS="-Xmx400m -Xms200m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+UseContainerSupport -Djava.security.egd=file:/dev/./urandom"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
