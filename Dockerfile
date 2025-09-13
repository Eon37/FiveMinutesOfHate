FROM ibm-semeru-runtimes:open-17-jre-focal
COPY build/libs/fmh-1.0.0.jar fmh-1.0.0.jar
ENV _JAVA_OPTIONS="-XX:MaxRAM=100m"
CMD java $_JAVA_OPTIONS -jar fmh-1.0.0.jar
EXPOSE 8080/tcp
EXPOSE 80
EXPOSE 443