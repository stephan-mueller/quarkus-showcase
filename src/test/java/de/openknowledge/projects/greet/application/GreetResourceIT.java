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
package de.openknowledge.projects.greet.application;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;

/**
 * Integration test for the resource {@link GreetResource}.
 */
@QuarkusTest
@TestHTTPEndpoint(GreetResource.class)
class GreetResourceIT {

  @BeforeEach
  void setGreetingToHello() {
    RestAssured.given()
        .contentType(MediaType.APPLICATION_JSON)
        .body("{ \"greeting\" : \"Hello\" }")
        .when()
        .put("greeting")
        .then()
        .statusCode(Response.Status.NO_CONTENT.getStatusCode());
  }

  @Test
  void greet() {
    RestAssured.given()
        .accept(MediaType.APPLICATION_JSON)
        .pathParam("name", "Stephan")
        .when()
        .get("{name}")
        .then()
        .statusCode(Response.Status.OK.getStatusCode())
        .contentType(MediaType.APPLICATION_JSON)
        .body("message", Matchers.equalTo("Hello Stephan!"));
  }

  @Test
  void greetTheWorld() {
    RestAssured.given()
        .accept(MediaType.APPLICATION_JSON)
        .when()
        .get()
        .then()
        .statusCode(Response.Status.OK.getStatusCode())
        .contentType(MediaType.APPLICATION_JSON)
        .body("message", Matchers.equalTo("Hello World!"));
  }

  @Test
  void getGreeting() {
    RestAssured.given()
        .accept(MediaType.APPLICATION_JSON)
        .when()
        .get("greeting")
        .then()
        .statusCode(Response.Status.OK.getStatusCode())
        .contentType(MediaType.APPLICATION_JSON)
        .body("greeting", Matchers.equalTo("Hello"));
  }

  @Test
  void updateGreeting() {
    RestAssured.given()
        .contentType(MediaType.APPLICATION_JSON)
        .body("{ \"greeting\" : \"Hello\" }")
        .when()
        .put("greeting")
        .then()
        .statusCode(Response.Status.NO_CONTENT.getStatusCode());
  }
}
