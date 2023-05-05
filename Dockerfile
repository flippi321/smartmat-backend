FROM openjdk:17-jdk-slim
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
#ENTRYPOINT ["sh", "-c", "sleep 30 && java -jar /app.jar"]
