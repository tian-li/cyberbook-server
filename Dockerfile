FROM openjdk:12-jdk-alpine
#RUN addgroup -S spring && adduser -S spring -G spring
#USER spring:spring
EXPOSE 8080
ARG WAR_FILE=target/*.war
COPY ${WAR_FILE} cyberbook.war
ENTRYPOINT ["java","-jar","/cyberbook.war"]