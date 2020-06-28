/*
 * Copyright (C) open knowledge GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
package de.openknowledge.projects.greet;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.output.Slf4jLogConsumer;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

/**
 * Integration test for the resource {@link GreetResource}.
 */
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
  void greet() {
    RestAssured.given(requestSpecification)
        .accept(MediaType.APPLICATION_JSON)
        .pathParam("name", "Stephan")
        .when()
        .get("/api/greet/{name}")
        .then()
        .statusCode(Response.Status.OK.getStatusCode())
        .contentType(MediaType.APPLICATION_JSON)
        .body("message", Matchers.equalTo("Hello Stephan!"));
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

  @Test
  void getGreeting() {
    RestAssured.given(requestSpecification)
        .accept(MediaType.APPLICATION_JSON)
        .when()
        .get("/api/greet/greeting")
        .then()
        .statusCode(Response.Status.OK.getStatusCode())
        .contentType(MediaType.APPLICATION_JSON)
        .body("greeting", Matchers.equalTo("Hello"));
  }

  @Test
  void updateGreeting() {
    RestAssured.given(requestSpecification)
        .contentType(MediaType.APPLICATION_JSON)
        .body("{ \"greeting\" : \"Hello\" }")
        .when()
        .put("/api/greet/greeting")
        .then()
        .statusCode(Response.Status.NO_CONTENT.getStatusCode());
  }
}
