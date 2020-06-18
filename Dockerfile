FROM openjdk:8-jre-alpine

RUN apk --no-cache add curl

COPY target/quarkus-showcase-runner.jar /opt/quarkus-showcase.jar

HEALTHCHECK --start-period=10s --timeout=60s --retries=10 --interval=5s CMD curl -f http://localhost:8080/health/ready || exit 1

EXPOSE 8080
ENTRYPOINT exec java -Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager -jar /opt/quarkus-showcase.jar
