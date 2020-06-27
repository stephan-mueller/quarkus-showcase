# Quarkus Showcase

[![GitHub last commit](https://img.shields.io/github/last-commit/stephan-mueller/quarkus-showcase)](https://github.com/stephan-mueller/quarkus-showcase/commits) 
[![GitHub](https://img.shields.io/github/license/stephan-mueller/quarkus-showcase)](https://github.com/stephan-mueller/quarkus-showcase/blob/master/LICENSE)
[![CircleCI](https://circleci.com/gh/stephan-mueller/quarkus-showcase.svg?style=shield)](https://app.circleci.com/pipelines/github/stephan-mueller/quarkus-showcase)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=stephan-mueller_quarkus-showcase&metric=alert_status)](https://sonarcloud.io/dashboard?id=stephan-mueller_quarkus-showcase)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=stephan-mueller_quarkus-showcase&metric=coverage)](https://sonarcloud.io/dashboard?id=stephan-mueller_quarkus-showcase)

This is a showcase for the microservice framework [Quarkus](https://quarkus.io). It contains a hello world application, which demonstrates several features of Quarkus and Eclipse Microprofile

Software requirements to run the samples are `maven`, `openjdk-1.8` (or any other 1.8 JDK) and `docker`.
When running the Maven lifecycle it will create the war package and use the `quarkus-maven-plugin` to create a runnable 
jar (fat jar) which contains the application and the Quarkus application server. The fat jar will be copied into a
Docker image using Spotify's `dockerfile-maven-plugin` during the package phase.

## How to run

Before running the application it needs to be compiled and packaged using Maven. It creates the required war,
jar and Docker image and can be run via `docker`:

```shell script
$ mvn clean package
$ docker run --rm -p 8080:8080 quarkus-showcase:0
```

If everything worked you can access the OpenAPI UI via http://localhost:8080/swagger-ui.

## Resolving issues

Sometimes it may happen that the containers did not stop as expected when trying to stop the pipeline early. This may
result in running containers although they should have been stopped and removed. To detect them you need to check
Docker:

```shell script
$ docker ps -a | grep quarkus-showcase
```

If there are containers remaining although the application has been stopped you can remove them:

````shell script
$ docker rm <ids of the containers>
````