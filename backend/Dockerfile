# build stage
FROM eclipse-temurin:21 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src

# install Maven
RUN apt-get update && apt-get install -y maven
# build application
RUN mvn clean package -DskipTests

# last stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]