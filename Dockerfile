FROM openjdk:12-jdk-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
EXPOSE 8899
ARG JAR_FILE=target/*.war
COPY ${JAR_FILE} cyberbook.war
ENTRYPOINT ["java","-jar","/cyberbook.war"]