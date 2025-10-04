# === Build Stage ===
FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app
# Copy pom.xml and download dependencies (caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B
# Copy source code and build
COPY src ./src
RUN mvn -q -DskipTests clean package

# === Runtime Stage ===
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copy JAR from builder
COPY --from=builder /app/target/event-service-*.jar app.jar

# Expose port
EXPOSE 8080

# Run the app
ENTRYPOINT ["java","-jar","/app/app.jar"]