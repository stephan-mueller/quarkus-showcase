FROM openjdk:15-alpine

RUN apk --no-cache add curl

ARG JAR_FILE=quarkus-showcase-runner.jar
COPY target/${JAR_FILE} /opt/application.jar

HEALTHCHECK --start-period=10s --timeout=60s --retries=10 --interval=5s CMD curl -f http://localhost:8080/health/ready || exit 1

ENV JAVA_OPTS="-Djava.net.preferIPv4Stack=true -Djava.net.preferIPv4Addresses=true"
ENV QUARKUS_OPTS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"

EXPOSE 8080
ENTRYPOINT exec java $JAVA_OPTS $QUARKUS_OPTS -jar /opt/application.jar
