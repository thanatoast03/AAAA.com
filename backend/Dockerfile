# jdk image
FROM openjdk:17-jdk-slim

# working directory
WORKDIR /app

# copy maven file over
COPY pom.xml .

# copy source code from maven 
COPY src ./src

# Install Maven
RUN apt-get update && apt-get install -y maven

# Build the application
RUN mvn clean package -DskipTests

# Expose the port the app runs on
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java","-jar","/app/target/myapp-0.0.1-SNAPSHOT.jar"]