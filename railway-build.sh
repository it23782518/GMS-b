#!/bin/bash
# Railway build script with timeout handling

set -e

echo "🚀 Starting Railway-optimized build..."

# Function to retry Maven commands
retry_maven() {
    local cmd="$1"
    local max_attempts=3
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        echo "Attempt $attempt/$max_attempts: $cmd"
        if eval $cmd; then
            echo "✅ Command succeeded on attempt $attempt"
            return 0
        else
            echo "❌ Command failed on attempt $attempt"
            if [ $attempt -eq $max_attempts ]; then
                echo "💥 All attempts failed"
                return 1
            fi
            attempt=$((attempt + 1))
            sleep 5
        fi
    done
}

# Step 1: Try to download dependencies with retries
echo "📦 Downloading dependencies..."
retry_maven "mvn dependency:go-offline -B -q -Dmaven.wagon.http.connectionTimeout=30000 -Dmaven.wagon.http.readTimeout=60000" || \
retry_maven "mvn dependency:resolve dependency:resolve-sources -B -q -Dmaven.wagon.http.connectionTimeout=30000 -Dmaven.wagon.http.readTimeout=60000" || \
echo "⚠️  Dependency download completed with issues, continuing..."

# Step 2: Build the application
echo "🔨 Building application..."
retry_maven "mvn package -DskipTests -B -q -o" || \
retry_maven "mvn package -DskipTests -B -q"

echo "✅ Build completed successfully!"
