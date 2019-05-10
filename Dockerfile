FROM openjdk:8-jre-alpine

COPY target/quarkus-showcase-runner.jar /opt/quarkus-showcase.jar

EXPOSE 8080
ENTRYPOINT exec java -Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager -jar /opt/quarkus-showcase.jar
