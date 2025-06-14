# Railway-optimized Dockerfile with pre-built dependencies
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Install Maven with timeout optimizations
RUN apk add --no-cache maven

# Copy Maven configuration for Railway optimizations
COPY .mvn .mvn
COPY pom.xml mvnw mvnw.cmd ./

# Critical: Download dependencies with Railway-specific timeouts
RUN mvn dependency:go-offline -B -q \
    -Dmaven.wagon.http.connectionTimeout=30000 \
    -Dmaven.wagon.http.readTimeout=60000 \
    -Dmaven.wagon.httpconnectionManager.ttlSeconds=60 \
    -Dmaven.wagon.http.retryHandler.count=2 \
    || mvn dependency:resolve dependency:resolve-sources -B -q \
    -Dmaven.wagon.http.connectionTimeout=30000 \
    -Dmaven.wagon.http.readTimeout=60000 \
    || echo "Dependency download completed with warnings"

# Copy source and build (now with cached dependencies)
COPY src ./src
RUN mvn package -DskipTests -B -q -o \
    || mvn package -DskipTests -B -q

# Production stage - minimal runtime image
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create non-root user for security
RUN addgroup -g 1001 -S railway && \
    adduser -S railway -u 1001

# Copy built JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Change ownership to non-root user
RUN chown railway:railway app.jar

USER railway

# Expose port
EXPOSE 8080

# Railway-optimized JVM settings for 512MB limit
CMD ["java", \
     "-Xmx400m", \
     "-Xms200m", \
     "-XX:+UseSerialGC", \
     "-XX:MaxRAMPercentage=75", \
     "-XX:+TieredCompilation", \
     "-XX:TieredStopAtLevel=1", \
     "-Djava.security.egd=file:/dev/./urandom", \
     "-jar", "app.jar"]
