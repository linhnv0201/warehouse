# --- Build stage ---
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app

# Copy source code vào container để build
COPY . .

# Build project, bỏ qua test cho nhanh
RUN mvn clean package -DskipTests

# --- Run stage ---
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copy file JAR từ stage build vào
COPY --from=build /app/target/warehouse-0.0.1-SNAPSHOT.jar app.jar

# Expose cổng Spring Boot
EXPOSE 8080

# Lệnh khởi chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]
