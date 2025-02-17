# # Build Stage
# FROM maven:3.8.4-openjdk-17 AS build
# WORKDIR /app

# # Copy pom.xml and run dependency:go-offline to cache dependencies
# COPY pom.xml .
# RUN mvn dependency:go-offline

# # Copy source code and package the application, skipping tests for faster builds
# COPY src ./src
# RUN mvn clean package -DskipTests

# # Final Stage
# FROM openjdk:17.0.1-jdk-slim
# WORKDIR /app

# # Copy the jar from the build stage
# COPY --from=build /app/target/InventoryMgtSystem-0.0.1-SNAPSHOT.jar /app/target/InventoryMgtSystem-0.0.1-SNAPSHOT.jar

# # Expose port for the application (assuming the app listens on port 8080)
# EXPOSE 8080

# # Command to run the application
# ENTRYPOINT [ "java", "-jar", "/app/target/InventoryMgtSystem-0.0.1-SNAPSHOT.jar" ]
