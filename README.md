# Quarkus Showcase

[![GitHub last commit](https://img.shields.io/github/last-commit/stephan-mueller/quarkus-showcase)](https://github.com/stephan-mueller/quarkus-showcase/commits) 
[![GitHub](https://img.shields.io/github/license/stephan-mueller/quarkus-showcase)](https://github.com/stephan-mueller/quarkus-showcase/blob/master/LICENSE)
[![CircleCI](https://circleci.com/gh/stephan-mueller/quarkus-showcase.svg?style=shield)](https://app.circleci.com/pipelines/github/stephan-mueller/quarkus-showcase)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=stephan-mueller_quarkus-showcase&metric=alert_status)](https://sonarcloud.io/dashboard?id=stephan-mueller_quarkus-showcase)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=stephan-mueller_quarkus-showcase&metric=coverage)](https://sonarcloud.io/dashboard?id=stephan-mueller_quarkus-showcase)

This is a showcase for the microservice framework [Quarkus](https://quarkus.io). It contains a hello world application, 
which demonstrates several features of Quarkus and Eclipse Microprofile.

Software requirements to run the samples are `maven`, `openjdk-8` (or any other JDK 8) and `docker`.
When running the Maven lifecycle it will create the war package and use the `quarkus-maven-plugin` to create a runnable 
JAR (fat JAR) which contains the application and the Quarkus application server. The fat JAR will be copied into a
Docker image using Spotify's `dockerfile-maven-plugin` during the package phase.

**Notable Features:**
* Dockerfiles for runnable JAR & Native Executable 
* Integration of MP Health, MP Metrics and MP OpenAPI
* Testcontainer tests with Rest-Assured, Cucumber and Postman/newman
* Code-Coverage for Testcontainer tests
* [CircleCI](https://circleci.com) Integration
* [Sonarcloud](https://sonarcloud.io) Integration


## How to run

Before running the application it needs to be compiled and packaged using `Maven`. It creates the runnable JAR and Docker image and can be 
run via `docker`:

```shell script
$ mvn clean package
$ docker run --rm -p 8080:8080 quarkus-showcase
```

If everything worked you can access the OpenAPI UI via [http://localhost:8080/swagger-ui](http://localhost:8080/swagger-ui).

### How to run a native image 

Before building a native executable and a native image, you have to install the GraalVM on your machine.

1. Install GraalVM (_Hint: check the Quarkus guide [building a native executable](https://quarkus.io/guides/building-native-image) for further details_)
2. Set GRAALVM_HOME environment variable to the GraalVM installation directory
3. Install the `native-image` tool using `gu install`

```shell script
$ export GRAALVM_HOME=/Library/Java/JavaVirtualMachines/graalvm-ce-java11-20.1.0/Contents/Home/
$ ${GRAALVM_HOME}/bin/gu install native-image
```

To create a docker image with a native executable you have to build the application with the Maven profile `native` 
```shell script
$ mvn clean package -Pnative
$ docker run --rm -p 8080:8080 quarkus-showcase
```

**Please note:**
 
The native executable is tailored for a specific operating system (Linux, macOS, Windows etc). 
If you build the native executable on macOS or Windows, you will not be able to use it inside a Linux based docker container. 

You can check the machine architecture with the following command:
```
$ file target/quarkus-showcase-runner
```
For a native executable build on macOs the output is:
> target/quarkus-showcase-runner: Mach-O 64-bit executable x86_64

For a native executable build in a Linux container the output is: 
> target/quarkus-showcase-runner: ELF 64-bit LSB executable, x86-64, version 1 (SYSV), dynamically linked, interpreter /lib64/l, for GNU/Linux 3.2.0


To build a native executable which is usable inside a docker container you have to set the property 
`quarkus.native.container-build=true` (the property is set by default when using the Maven profile `native`). 
Instead of your machine a Linux container runtime is used for the build.    

If you want to try out the native executable on your machine use the following commands:
```shell script
$ mvn clean package -Pnative -Dquarkus.native.container-build=false -Ddockerfile.skip
$ ./target/quarkus-showcase-runner
```

### Resolving issues

Sometimes it may happen that the containers did not stop as expected when trying to stop the pipeline early. This may
result in running containers although they should have been stopped and removed. To detect them you need to check
Docker:

```shell script
$ docker ps -a | grep quarkus-showcase
```

If there are containers remaining although the application has been stopped you can remove them:

```shell script
$ docker rm <ids of the containers>
```


## Features

### Application 

The application is a very simple "Hello World" greeting service. It supports GET requests for generating a greeting message, and a PUT 
request for changing the greeting itself. The response is encoded using JSON.

Try the application
```shell script
curl -X GET http://localhost:8080/api/greet
{"message":"Hello World!"}

curl -X GET http://localhost:8080/api/greet/Stephan
{"message":"Hello Stephan!"}

curl -X PUT -H "Content-Type: application/json" -d '{"greeting" : "Hola"}' http://localhost:8080/api/greet/greeting

curl -X GET http://localhost:8080/api/greet/greeting
{"greeting":"Hola"}

curl -X GET http://localhost:8080/api/greet/Max
{"message":"Hola Max!"}
```

### Health, Metrics and OpenAPI

The application server provides built-in support for health, metrics and openapi endpoints.

Health liveness and readiness
```shell script
curl -s -X GET http://localhost:8080/health

curl -s -X GET http://localhost:8080/health/live

curl -s -X GET http://localhost:8080/health/ready
```

Metrics in Prometheus / JSON Format
```shell script
curl -s -X GET http://localhost:8080/metrics

curl -H 'Accept: application/json' -X GET http://localhost:8080/metrics
```

OpenAPI in YAML / JSON Format
```shell script
curl -s -X GET http://localhost:8080/openapi

curl -H 'Accept: application/json' -X GET http://localhost:8080/openapi
```

### Testcontainer tests with REST-assured, Cucumber and Postman/Newman

For the application a set of integration tests is provided. The tests bases on Testcontainers combined with other testing frameworks like 
REST-assured, Cucumber and Postman/Newman. The docker container for the application is build by the `dockerfile-maven-plugin` during the 
`package` phase.

To improve the runtime of the testcontainer tests by avoid starting and stopping the container for every test class, the 
[singleton container](https://www.Testcontainers.org/test_framework_integration/manual_lifecycle_control/) pattern is used.

The container is started only once when the base class is loaded. The container can then be used by all inheriting test classes. At the end 
of the test suite the Ryuk container that is started by Testcontainers core will take care of stopping the singleton container.

AbstractIntegrationTest - Superclass for all Testcontainers tests providing the containerized application
```java
public abstract class AbstractIntegrationTest {

  protected static final String NETWORK_ALIAS_APPLICATION = "application";

  protected static final Network NETWORK = Network.newNetwork();

  protected static final GenericContainer<?> APPLICATION = new GenericContainer<>("quarkus-showcase")
      .withExposedPorts(8080)
      .withNetwork(NETWORK)
      .withNetworkAliases(NETWORK_ALIAS_APPLICATION)
      .waitingFor(Wait.forHealthcheck());

  static {
    APPLICATION.start();
  }
}
```  

#### Integration tests with Testcontainer and REST-assured

[REST-assured](http://rest-assured.io) is a popular testframework for testing and validating REST services that brings the the simplicity 
of dynamic languages into the Java domain. 

To ease making HTTP requests to the containerized application, REST-assured provides specifications to reuse response expectations and/or 
request parameters for different tests. The `RequestSpecBuilder` is used to define the dynamic port of the application for all requests only 
once. 

GreetingResourceIT - Integration tests for the GreetingResource
```java
class GreetResourceIT extends AbstractIntegrationTest {

  private static final Logger LOG = LoggerFactory.getLogger(GreetResourceIT.class);

  private static RequestSpecification requestSpecification;

  @BeforeAll
  static void setUpUri() {
    APPLICATION.withLogConsumer(new Slf4jLogConsumer(LOG));

    requestSpecification = new RequestSpecBuilder()
        .setPort(APPLICATION.getFirstMappedPort())
        .build();

    RestAssured.given(requestSpecification)
        .contentType(MediaType.APPLICATION_JSON)
        .body("{ \"greeting\" : \"Hello\" }")
        .when()
        .put("/api/greet/greeting")
        .then()
        .statusCode(Response.Status.NO_CONTENT.getStatusCode());
  }

  @Test
  void greetTheWorld() {
    RestAssured.given(requestSpecification)
        .accept(MediaType.APPLICATION_JSON)
        .when()
        .get("/api/greet")
        .then()
        .statusCode(Response.Status.OK.getStatusCode())
        .contentType(MediaType.APPLICATION_JSON)
        .body("message", Matchers.equalTo("Hello World!"));
  }
}
```

#### Acceptance tests with Testcontainer, REST-assured and Cucumber

[Cucumber](https://github.com/cucumber/cucumber-jvm) is one of the most popular tools that supports Behaviour-Driven Development(BDD) for 
the Java language. Cucumber reads executable specifications written in natural language and validates that the software does what those 
specifications say. The specifications consist of several examples or scenarios - which is why this approach is known as 
[Specification by Example](https://en.wikipedia.org/wiki/Specification_by_example).

Greeting.feature - Acceptance tests in natural language (Gherkin syntax)
```gherkin
Feature: Greeting

  Scenario: Greet the world
    Given a greeting "Hello"
    When a user wants to greet
    Then the message is "Hello World!"

  Scenario Outline: Greet someone
    Given a greeting "<greeting>"
    When a user wants to greet "<name>"
    Then the message is "<greeting> <name>!"

    Examples:
      | greeting | name      |
      | Hola     | Christian |
      | Hey      | Max       |
      | Moin     | Stephan   |
```

To run cucumber tests, you have to to use the `Cucumber` JUnit runner.

GreetingCucumberIT - Test runner that runs all acceptance tests of the project
```java
@Cucumber
public class GreetingCucumberIT {
}
```

Due to its BDD-oriented nature, REST-assured seamlessly integrates with Cucumber to implement acceptance tests for RESTful APIs. To 

GreetingCucumberSteps - Step definitions matching the steps in the feature file
```java
public class GreetingCucumberSteps extends AbstractIntegrationTest {

  private RequestSpecification requestSpecification;

  private io.restassured.response.Response response;

  @Before
  public void beforeScenario() {
    requestSpecification = new RequestSpecBuilder()
        .setPort(APPLICATION.getFirstMappedPort())
        .build();
  }

  @Given("a greeting {string}")
  public void given_a_greeting(final String greeting) {
    RestAssured.given(requestSpecification)
        .contentType(MediaType.APPLICATION_JSON)
        .body(new GreetingDTO(greeting))
        .when()
        .put("/api/greet/greeting")
        .then()
        .statusCode(Response.Status.NO_CONTENT.getStatusCode());
  }

  @When("a user wants to greet")
  public void when_a_user_wants_to_greet() {
    response = RestAssured.given(requestSpecification)
        .accept(MediaType.APPLICATION_JSON)
        .when()
        .get("/api/greet");
  }

  @When("a user wants to greet {string}")
  public void when_a_user_wants_to_greet(final String name) {
    response = RestAssured.given(requestSpecification)
        .accept(MediaType.APPLICATION_JSON)
        .pathParam("name", name)
        .when()
        .get("/api/greet/{name}");
  }

  @Then("the message is {string}")
  public void then_the_message_is(final String message) {
    response.then()
        .statusCode(Response.Status.OK.getStatusCode())
        .contentType(MediaType.APPLICATION_JSON)
        .body("message", Matchers.equalTo(message));
  }
}
```

As expected the execution of the specification examples can also be easily followed in the log output

Cucucmber log output
```text
[INFO] Running de.openknowledge.projects.greet.GreetingCucumberIT

Scenario: Greet the world                # src/test/resources/it/feature/Greeting.feature:3
  Given a greeting "Hello"               # de.openknowledge.projects.greet.GreetingCucumberSteps.given_a_greeting(java.lang.String)
  When a user wants to greet             # de.openknowledge.projects.greet.GreetingCucumberSteps.when_a_user_wants_to_greet()
  Then the message is "Hello World!"     # de.openknowledge.projects.greet.GreetingCucumberSteps.then_the_message_is(java.lang.String)

Scenario Outline: Greet someone          # src/test/resources/it/feature/Greeting.feature:15
  Given a greeting "Hola"                # de.openknowledge.projects.greet.GreetingCucumberSteps.given_a_greeting(java.lang.String)
  When a user wants to greet "Christian" # de.openknowledge.projects.greet.GreetingCucumberSteps.when_a_user_wants_to_greet(java.lang.String)
  Then the message is "Hola Christian!"  # de.openknowledge.projects.greet.GreetingCucumberSteps.then_the_message_is(java.lang.String)

Scenario Outline: Greet someone          # src/test/resources/it/feature/Greeting.feature:16
  Given a greeting "Hey"                 # de.openknowledge.projects.greet.GreetingCucumberSteps.given_a_greeting(java.lang.String)
  When a user wants to greet "Max"       # de.openknowledge.projects.greet.GreetingCucumberSteps.when_a_user_wants_to_greet(java.lang.String)
  Then the message is "Hey Max!"         # de.openknowledge.projects.greet.GreetingCucumberSteps.then_the_message_is(java.lang.String)

Scenario Outline: Greet someone          # src/test/resources/it/feature/Greeting.feature:17
  Given a greeting "Moin"                # de.openknowledge.projects.greet.GreetingCucumberSteps.given_a_greeting(java.lang.String)
  When a user wants to greet "Stephan"   # de.openknowledge.projects.greet.GreetingCucumberSteps.when_a_user_wants_to_greet(java.lang.String)
  Then the message is "Moin Stephan!"    # de.openknowledge.projects.greet.GreetingCucumberSteps.then_the_message_is(java.lang.String)

[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.916 s - in de.openknowledge.projects.greet.GreetingCucumberIT
```

#### API Tests with Testcontainer and Postman/Newman

Postman is an popular API client that supports automated API testing. Test collections developed in Postman can be exported and integrated 
with your CI/CD pipeline by using [Newman](https://github.com/postmanlabs/newman), Postman's command line Collection Runner. 

Newman allows you to run and test a Postman Collection directly from the command line. It is built with extensibility in mind so that it 
can easily integrate it with continuous integration servers, build systems and even Testcontainers.

To automate Postman test collections with Testcontainers the newman docker image is required. The collection and the environment file has 
to be copied to the docker image, and a file system bind has to be configured, to be able to access the test reports.

**IMPORTANT**: The newman container is started and stopped for the execution of a single command - running the collection. To prevent that 
the containers is stopped before the test collection is executed, a `OneShotStartupCheckStrategy` with a timeout of 5 to 10 seconds has to 
be configured for the newman container.

GreetingPostmanIT - Newman container that runs a Postman collection against the containerized application.
```java
class GreetingPostmanIT extends AbstractIntegrationTest {

  private static final Logger LOG = LoggerFactory.getLogger(GreetResourceIT.class);

  private static final GenericContainer<?> NEWMAN = new GenericContainer<>("postman/newman:5.1.0-alpine")
      .withNetwork(NETWORK)
      .dependsOn(APPLICATION)
      .withCopyFileToContainer(MountableFile.forClasspathResource("postman/hello-world.postman_collection.json"),
                               "/etc/newman/hello-world.postman_collection.json")
      .withCopyFileToContainer(MountableFile.forClasspathResource("postman/hello-world.postman_environment.json"),
                               "/etc/newman/hello-world.postman_environment.json")
      .withFileSystemBind("target/postman/reports", "/etc/newman/reports", BindMode.READ_WRITE)
      .withStartupCheckStrategy(new OneShotStartupCheckStrategy().withTimeout(Duration.ofSeconds(5)));

  @Test
  void run() {
    NEWMAN.withCommand("run", "hello-world.postman_collection.json",
                       "--environment=hello-world.postman_environment.json",
                       "--reporters=cli,junit",
                       "--reporter-junit-export=reports/hello-world.newman-report.xml");
    NEWMAN.start();

    LOG.info(NEWMAN.getLogs());

    assertThat(NEWMAN.getCurrentContainerInfo().getState().getExitCode()).isZero();
  }
}
```