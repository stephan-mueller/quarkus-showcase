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
* Quarkus tests with Testcontainers, Rest-Assured, DbUnit (Database-Rider) and Postman/newman
* Acceptance tests with Testcontainers, Rest-Assured and Cucumber
* Code-Coverage for Testcontainer tests
* [CircleCI](https://circleci.com) Integration
* [Sonarcloud](https://sonarcloud.io) Integration


## How to run

Before running the application it needs to be compiled and packaged using `Maven`. It creates the runnable JAR and Docker image and can be 
run via `docker`:

```shell script
$ docker-compose up database
$ mvn clean package
$ docker-compose up application
```

If everything worked you can access the OpenAPI UI via [http://localhost:8080/swagger-ui](http://localhost:8080/swagger-ui). In addition to
that the Quarkus Health UI can be accessed via http://localhost:8080/health-ui/.

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
$ docker-compose up application
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
curl -X GET http://localhost:8080/greet
{"message":"Hello World!"}

curl -X GET http://localhost:8080/greet/Stephan
{"message":"Hello Stephan!"}

curl -X PUT -H "Content-Type: application/json" -d '{"greeting" : "Hola"}' http://localhost:8080/greet/greeting

curl -X GET http://localhost:8080/greet/greeting
{"greeting":"Hola"}

curl -X GET http://localhost:8080/greet/Max
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


### Integration tests with QuarkusTest, Testcontainers, REST-assured, DbUnit, Cucumber and Postman/Newman

For the application a set of integration tests is provided. The tests bases on `@QuarkusTest` combined with other testing frameworks like 
REST-assured, DbUnit (Database-Rider) and Postman/Newman.

When running a single or a set tests annotated with `@QuarkusTest` the Quarkus test extension is configured for the test, which starts 
Quarkus with profile `test`. Quarkus will then remain running for the duration of the test run. This makes testing very fast, because 
Quarkus is only started once.

#### Integration tests with Quarkus-Test, Testcontainers and DbUnit (Database-Rider)

[DbUnit](http://dbunit.sourceforge.net) is a JUnit extension targeted at database-driven projects that, among other things, puts the 
database into a known state between test runs. [Database-Rider](https://github.com/database-rider/database-rider) integrates JUnit and 
DbUnit through JUnit rules and, in case of CDI based tests, a CDI interceptor. The combination enables developers to prepare the database 
state for testing through yaml, xml, json, xls or csv files. Most inspiration of Database Rider was taken from [Arquillian extension 
persistence](https://docs.jboss.org/author/display/ARQ/Persistence.html).

The best and easiest way to provide a real database for integration tests is to use Testcontainers (e.g. `PostgreSQLContainer`). To 
integrate QuarkusTest with a Testcontainer Quarkus' notion of test resources can be used. Like described in the 
[Blog Post of Gunnar Morling](https://www.morling.dev/blog/quarkus-and-testcontainers/) an implementation of the 
`QuarkusTestResourceLifecycleManager` interface, which controls the resource’s lifecycle is a easy solution.

_DatabaseTestResource - Postgres Testcontainer for the integration tests with Quarkus-Test._
```java
public class DatabaseTestResource implements QuarkusTestResourceLifecycleManager {

  private static final PostgreSQLContainer<?> DATABASE = new PostgreSQLContainer<>("postgres:12-alpine")
      .withDatabaseName("greeting-test")
      .withUsername("postgres")
      .withPassword("postgres");

  @Override
  public Map<String, String> start() {
    DATABASE.start();
    return Collections.singletonMap("quarkus.datasource.jdbc.url", DATABASE.getJdbcUrl());
  }

  @Override
  public void stop() {
    DATABASE.stop();
  }
}
```

To enable the test resource the `@QuarkusTestResource` annotation has to be used. In addition to that the `@DBRider` CDI interceptor 
controls DbUnit to prepare and verify the database for each test case.  

_GreetRepositoryIT - Integration tests for the GreetRepository_
```java
@QuarkusTest
@QuarkusTestResource(DatabaseTestResource.class)
@DBRider
@DataSet(value = "greetings.yml", strategy = SeedStrategy.CLEAN_INSERT, skipCleaningFor = "flyway_schema_history")
class GreetingRepositoryIT {

  @Inject
  private GreetingRepository repository;

  @Test
  void findBySalutationShouldReturnEmptyOptional() {
    Optional<Greeting> optional = repository.findBySalutation("Polo");
    Assertions.assertThat(!optional.isPresent()).isTrue();
  }

  @Test
  void findBySalutationShouldReturnOptional() {
    Optional<Greeting> optional = repository.findBySalutation("Marco");
    Assertions.assertThat(optional.isPresent()).isTrue();

    Greeting greeting = optional.get();
    Assertions.assertThat(greeting.id).isEqualTo(1L);
    Assertions.assertThat(greeting.getSalutation()).isEqualTo("Marco");
    Assertions.assertThat(greeting.getResponse()).isEqualTo("Polo");
  }
}
```

#### Integration tests with Quarkus-Test, Testcontainers, REST-assured and DbUnit (Database-Rider)

[REST-assured](http://rest-assured.io) is a popular testframework for testing and validating REST services that brings the the simplicity 
of dynamic languages into the Java domain. 

_GreetResourceIT - Integration tests for the GreetResource_
```java
@QuarkusTest
@QuarkusTestResource(DatabaseTestResource.class)
@TestHTTPEndpoint(GreetResource.class)
@DBRider
@DataSet(value = "greetings.yml", strategy = SeedStrategy.CLEAN_INSERT, skipCleaningFor = "flyway_schema_history")
class GreetResourceIT {

  ... 
  
  @Test
  void greetTheWorld() {
    RestAssured.given()
        .accept(MediaType.APPLICATION_JSON)
        .when()
        .get("/api/greet")
        .then()
        .statusCode(Response.Status.OK.getStatusCode())
        .contentType(MediaType.APPLICATION_JSON)
        .body("message", Matchers.equalTo("Hello World!"));
  }
  
  ...

  @Test
  void getResponseShouldReturn200() {
    RestAssured.given()
        .accept(MediaType.APPLICATION_JSON)
        .pathParam("salutation", "Marco")
        .when()
        .get("/response/{salutation}")
        .then()
        .statusCode(Response.Status.OK.getStatusCode())
        .contentType(MediaType.APPLICATION_JSON)
        .body("response", Matchers.equalTo("Polo"));
  }

  @Test
  void getResponseShouldReturn404() {
    RestAssured.given()
        .accept(MediaType.APPLICATION_JSON)
        .pathParam("salutation", "Polo")
        .when()
        .get("/response/{salutation}")
        .then()
        .statusCode(Response.Status.NOT_FOUND.getStatusCode());
  }
  
  ...
}
```

#### API Tests with Testcontainers and Postman/Newman

Postman is an popular API client that supports automated API testing. Test collections developed in Postman can be exported and integrated 
with your CI/CD pipeline by using [Newman](https://github.com/postmanlabs/newman), Postman's command line Collection Runner. 

Newman allows you to run and test a Postman Collection directly from the command line. It is built with extensibility in mind so that it 
can easily integrate it with continuous integration servers, build systems and even Testcontainers.

To automate Postman test collections with Testcontainers the newman docker image is required. The collection and the environment file has 
to be copied to the docker image, and a file system bind has to be configured, to be able to access the test reports.

**IMPORTANT I**: The newman container is started and stopped for the execution of a single command - running the collection. To prevent that 
the containers is stopped before the test collection is executed, a `OneShotStartupCheckStrategy` with a timeout of 10 seconds has to be 
configured for the newman container.

**IMPORTANT II**: If you want to access a application running on the testcontainer host system from a testcontainer, the host ports have 
to be exposed. To configure Testcontainers to expose ports from the host system you have to call
`Testcontainers.exposeHostPorts(localServerPort);`.  

_GreetPostmanIT - Newman container that runs a Postman collection against the containerized application._
```java
@QuarkusTest
@QuarkusTestResource(DatabaseTestResource.class)
@DBRider
@DataSet(value = "greetings.yml", strategy = SeedStrategy.CLEAN_INSERT, skipCleaningFor = "flyway_schema_history")
class GreetPostmanQuarkusIT {

  private static final Logger LOG = LoggerFactory.getLogger(GreetPostmanQuarkusIT.class);

  private static final GenericContainer<?> NEWMAN = new GenericContainer<>("postman/newman:5.1.0-alpine")
      .withCopyFileToContainer(MountableFile.forClasspathResource("postman/hello-world.postman_collection.json"),
                               "/etc/newman/hello-world.postman_collection.json")
      .withCopyFileToContainer(MountableFile.forClasspathResource("postman/hello-world.postman_environment.json"),
                               "/etc/newman/hello-world.postman_environment.json")
      .withFileSystemBind("target/postman/reports", "/etc/newman/reports", BindMode.READ_WRITE)
      .withStartupCheckStrategy(new OneShotStartupCheckStrategy().withTimeout(Duration.ofSeconds(10)));

  @BeforeAll
  static void exposeTestPort() {
    Config config = ConfigProvider.getConfig();
    Integer testPort = config.getValue("quarkus.http.test-port", Integer.class);
    Testcontainers.exposeHostPorts(testPort);
  }

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

#### Acceptance tests with Testcontainers, REST-assured and Cucumber

[Cucumber](https://github.com/cucumber/cucumber-jvm) is one of the most popular tools that supports Behaviour-Driven Development(BDD) for
the Java language. Cucumber reads executable specifications written in natural language and validates that the software does what those
specifications say. The specifications consist of several examples or scenarios - which is why this approach is known as
[Specification by Example](https://en.wikipedia.org/wiki/Specification_by_example).

_Greet.feature - Acceptance tests in natural language (Gherkin syntax)_
```gherkin
Feature: Greet

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

  Scenario Outline: Get response to salutation
    When a user wants to get the response to "<salutation>"
    Then the response is "<response>"

    Examples:
      | salutation | response |
      | Marco      | Polo     |
      | Ping       | Pong     |
      | Moin       | Moin     |

  Scenario: Get response to unknown salutation
    When a user wants to get the response to "Polo"
    Then the response is not found
```

Due to missing support for Cucumber in combination with `@QuarkusTest`, the cucumber tests has to be run with as Testcontainers test. 
The docker container for the application is build by the `dockerfile-maven-plugin` during the `package` phase.

To improve the runtime of the testcontainer tests by avoid starting and stopping the container for every test class, the
[singleton container](https://www.Testcontainers.org/test_framework_integration/manual_lifecycle_control/) pattern is used.

The container is started only once when the base class is loaded. The container can then be used by all inheriting test classes. At the end
of the test suite the Ryuk container that is started by Testcontainers core will take care of stopping the singleton container.

_AbstractTestcontainersIT - Superclass for all Testcontainers tests providing the containerized application_
```java
public abstract class AbstractTestcontainersIT {

  protected static final String NETWORK_ALIAS_APPLICATION = "application";

  protected static final Network NETWORK = Network.newNetwork();

  protected static final PostgreSQLContainer<?> DATABASE = new PostgreSQLContainer<>("postgres:12-alpine")
      .withNetwork(NETWORK)
      .withNetworkAliases("DATABASE")
      .withDatabaseName("greeting-test")
      .withUsername("postgres")
      .withPassword("postgres")
      .waitingFor(
          Wait.forLogMessage(".*server started.*", 1)
      );

  protected static final GenericContainer<?> APPLICATION = new GenericContainer<>("quarkus-showcase")
      .withExposedPorts(8080)
      .withNetwork(NETWORK)
      .withNetworkAliases(NETWORK_ALIAS_APPLICATION)
      .dependsOn(DATABASE)
      .withEnv("POSTGRES_URL","jdbc:postgresql://database:5432/greeting-test")
      .withEnv("POSTGRES_USER","postgres")
      .withEnv("POSTGRES_PASSWORD","postgres")
      .waitingFor(Wait.forHealthcheck());

  static {
    DATABASE.start();
    APPLICATION.start();
  }
}
```  

To run cucumber tests, you have to to use the `Cucumber` JUnit runner.

_GreetingCucumberIT - Test runner that runs all acceptance tests of the project_
```java
@Cucumber
public class GreetingCucumberIT {
}
```

Due to its BDD-oriented nature, REST-assured seamlessly integrates with Cucumber to implement acceptance tests for RESTful APIs. To

_GreetCucumberSteps - Step definitions matching the steps in the feature file_
```java
public class GreetCucumberSteps extends AbstractIntegrationTest {

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
        .put("/greet/greeting")
        .then()
        .statusCode(Response.Status.NO_CONTENT.getStatusCode());
  }

  @When("a user wants to greet")
  public void when_a_user_wants_to_greet() {
    response = RestAssured.given(requestSpecification)
        .accept(MediaType.APPLICATION_JSON)
        .when()
        .get("/greet");
  }

  @When("a user wants to greet {string}")
  public void when_a_user_wants_to_greet(final String name) {
    response = RestAssured.given(requestSpecification)
        .accept(MediaType.APPLICATION_JSON)
        .pathParam("name", name)
        .when()
        .get("/greet/{name}");
  }

  @When("a user wants to get the response to {string}")
  public void when_a_user_wants_to_get_the_response_to(final String salutation) {
    response = RestAssured.given(requestSpecification)
        .accept(MediaType.APPLICATION_JSON)
        .pathParam("salutation", salutation)
        .when()
        .get("/greet/response/{salutation}");
  }

  @Then("the message is {string}")
  public void then_the_message_is(final String message) {
    response.then()
        .statusCode(Response.Status.OK.getStatusCode())
        .contentType(MediaType.APPLICATION_JSON)
        .body("message", Matchers.equalTo(message));
  }

  @Then("the response is {string}")
  public void then_the_response_is(final String response) {
    this.response.then()
        .statusCode(Response.Status.OK.getStatusCode())
        .contentType(MediaType.APPLICATION_JSON)
        .body("response", Matchers.equalTo(response));
  }

  @Then("the response is not found")
  public void then_the_response_is_not_found() {
    this.response.then()
        .statusCode(Response.Status.NOT_FOUND.getStatusCode());
  }
}
```

As expected the execution of the specification examples can also be easily followed in the log output

_Cucucmber log output_
```text
[INFO] Running de.openknowledge.projects.greet.GreetCucumberIT

Scenario: Greet the world                # src/test/resources/it/feature/Greet.feature:3
  Given a greeting "Hello"               # de.openknowledge.projects.greet.GreetCucumberSteps.given_a_greeting(java.lang.String)
  When a user wants to greet             # de.openknowledge.projects.greet.GreetCucumberSteps.when_a_user_wants_to_greet()
  Then the message is "Hello World!"     # de.openknowledge.projects.greet.GreetCucumberSteps.then_the_message_is(java.lang.String)

Scenario Outline: Greet someone          # src/test/resources/it/feature/Greet.feature:15
  Given a greeting "Hola"                # de.openknowledge.projects.greet.GreetCucumberSteps.given_a_greeting(java.lang.String)
  When a user wants to greet "Christian" # de.openknowledge.projects.greet.GreetCucumberSteps.when_a_user_wants_to_greet(java.lang.String)
  Then the message is "Hola Christian!"  # de.openknowledge.projects.greet.GreetCucumberSteps.then_the_message_is(java.lang.String)

Scenario Outline: Greet someone          # src/test/resources/it/feature/Greet.feature:16
  Given a greeting "Hey"                 # de.openknowledge.projects.greet.GreetCucumberSteps.given_a_greeting(java.lang.String)
  When a user wants to greet "Max"       # de.openknowledge.projects.greet.GreetCucumberSteps.when_a_user_wants_to_greet(java.lang.String)
  Then the message is "Hey Max!"         # de.openknowledge.projects.greet.GreetCucumberSteps.then_the_message_is(java.lang.String)

Scenario Outline: Greet someone          # src/test/resources/it/feature/Greet.feature:17
  Given a greeting "Moin"                # de.openknowledge.projects.greet.GreetCucumberSteps.given_a_greeting(java.lang.String)
  When a user wants to greet "Stephan"   # de.openknowledge.projects.greet.GreetCucumberSteps.when_a_user_wants_to_greet(java.lang.String)
  Then the message is "Moin Stephan!"    # de.openknowledge.projects.greet.GreetCucumberSteps.then_the_message_is(java.lang.String)

Scenario Outline: Get response to salutation       # de/openknowledge/projects/greet/Greet.feature:25
  When a user wants to get the response to "Marco" # de.openknowledge.projects.greet.GreetCucumberSteps.when_a_user_wants_to_get_the_response_to(java.lang.String)
  Then the response is "Polo"                      # de.openknowledge.projects.greet.GreetCucumberSteps.then_the_response_is(java.lang.String)

Scenario Outline: Get response to salutation      # de/openknowledge/projects/greet/Greet.feature:26
  When a user wants to get the response to "Ping" # de.openknowledge.projects.greet.GreetCucumberSteps.when_a_user_wants_to_get_the_response_to(java.lang.String)
  Then the response is "Pong"                     # de.openknowledge.projects.greet.GreetCucumberSteps.then_the_response_is(java.lang.String)

Scenario Outline: Get response to salutation      # de/openknowledge/projects/greet/Greet.feature:27
  When a user wants to get the response to "Moin" # de.openknowledge.projects.greet.GreetCucumberSteps.when_a_user_wants_to_get_the_response_to(java.lang.String)
  Then the response is "Moin"                     # de.openknowledge.projects.greet.GreetCucumberSteps.then_the_response_is(java.lang.String)

Scenario: Get response to unknown salutation      # de/openknowledge/projects/greet/Greet.feature:29
  When a user wants to get the response to "Polo" # de.openknowledge.projects.greet.GreetCucumberSteps.when_a_user_wants_to_get_the_response_to(java.lang.String)
  Then the response is not found                  # de.openknowledge.projects.greet.GreetCucumberSteps.then_the_response_is_not_found()
```