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

import com.github.database.rider.cdi.api.DBRider;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.SeedStrategy;

import de.openknowledge.projects.greet.DatabaseTestResource;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;

/**
 * Integration test for the resource {@link GreetResource}.
 */
@QuarkusTest
@QuarkusTestResource(DatabaseTestResource.class)
@TestHTTPEndpoint(GreetResource.class)
@DBRider
@DataSet(value = "greetings.yml", strategy = SeedStrategy.CLEAN_INSERT, skipCleaningFor = "flyway_schema_history")
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
  void greetShouldReturn200() {
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
  void greetTheWorldShouldReturn200() {
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
  void getGreetingShouldReturn200() {
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

  @Test
  void updateGreetingShouldReturn204() {
    RestAssured.given()
        .contentType(MediaType.APPLICATION_JSON)
        .body("{ \"greeting\" : \"Hello\" }")
        .when()
        .put("greeting")
        .then()
        .statusCode(Response.Status.NO_CONTENT.getStatusCode());
  }
}
