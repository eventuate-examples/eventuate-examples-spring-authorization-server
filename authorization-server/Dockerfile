FROM amazoncorretto:17.0.1-al2
HEALTHCHECK --start-period=30s --interval=5s CMD curl -f http://localhost:9000/actuator/health || exit 1
CMD java ${JAVA_OPTS:-"-Xmx64m"} -jar ${JAR:-"service.jar"}
COPY build/libs/authorization-server-0.1.0-SNAPSHOT.jar service.jar
