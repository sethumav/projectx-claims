FROM maven:3.5.0-jdk-8 AS builder

# speed up Maven JVM a bit
ENV MAVEN_OPTS="-XX:+TieredCompilation -XX:TieredStopAtLevel=1"

# make source folder
RUN mkdir -p /app
WORKDIR /app

# copy other source files (keep code snapshot in image)
COPY ../../../pom.xml /app
COPY ../../../src /app/src

# run packaging
RUN mvn package -T 1C

# customize base JDK version here
FROM openjdk:8-jre-alpine AS target

EXPOSE 8081

# copy Java application WAR file: package app bytecode and libs into single WAR
COPY --from=builder /app/target/*.war /app.war

# default command to run Java application
CMD ["/usr/bin/java", "-jar", "/app.war"]
