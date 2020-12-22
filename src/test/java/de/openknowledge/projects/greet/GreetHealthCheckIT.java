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
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;

/**
 * Integration test for the health checks.
 */
@QuarkusTest
class GreetHealthCheckIT {

  @Test
  void checkHealth() {
    RestAssured.given()
        .accept(MediaType.APPLICATION_JSON)
        .when()
        .get("/health")
        .then()
        .contentType(MediaType.APPLICATION_JSON)
        .statusCode(Response.Status.OK.getStatusCode())
        .body("status", Matchers.equalTo("UP"));
  }

  @Test
  void checkLiveness() {
    ValidatableResponse response = RestAssured.given()
        .accept(MediaType.APPLICATION_JSON)
        .when()
        .get("/health/live")
        .then()
        .contentType(MediaType.APPLICATION_JSON)
        .statusCode(Response.Status.OK.getStatusCode())
        .body("status", Matchers.equalTo("UP"));

    response.rootPath("checks.find{ it.name == 'LivenessCheck' }")
        .body("status", Matchers.equalTo("UP"));
  }

  @Test
  void checkReadiness() {
    ValidatableResponse response = RestAssured.given()
        .accept(MediaType.APPLICATION_JSON)
        .when()
        .get("/health/ready")
        .then()
        .contentType(MediaType.APPLICATION_JSON)
        .statusCode(Response.Status.OK.getStatusCode())
        .body("status", Matchers.equalTo("UP"));

    response.rootPath("checks.find{ it.name == 'GreetResource' }")
        .body("status", Matchers.equalTo("UP"));

    response.rootPath("checks.find{ it.name == 'ReadinessCheck' }")
        .body("status", Matchers.equalTo("UP"));
  }
}
