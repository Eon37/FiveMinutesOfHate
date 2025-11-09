FROM gradle:8.13-jdk17 AS builder
RUN mkdir -p /home/gradle/cache_home
ENV GRADLE_USER_HOME=/home/gradle/cache_home
WORKDIR /home/gradle/java-code
COPY build.gradle settings.gradle ./
COPY src ./src
RUN gradle clean build -x test -i --stacktrace

FROM ibm-semeru-runtimes:open-17-jre-focal
WORKDIR /app
COPY --from=builder /home/gradle/java-code/build/libs/fmh-1.0.0.jar fmh-1.0.0.jar
ENV _JAVA_OPTIONS="-XX:MaxRAM=512m"
CMD java -Dspring.profiles.active=prod $_JAVA_OPTIONS -jar fmh-1.0.0.jar
EXPOSE 8080
