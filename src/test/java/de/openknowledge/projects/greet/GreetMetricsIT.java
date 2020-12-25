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

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;

/**
 * Integration test for the application metrics.
 */
@QuarkusTest
@QuarkusTestResource(DatabaseTestResource.class)
class GreetMetricsIT {

  @Test
  void checkApplicationMetrics() {
    RestAssured.given()
        .accept(MediaType.APPLICATION_JSON)
        .when()
        .get("/metrics/application")
        .then()
        .contentType(MediaType.APPLICATION_JSON)
        .statusCode(Response.Status.OK.getStatusCode())
        .body("size()", Matchers.equalTo(4))
        .body("greetCount", Matchers.notNullValue())
        .body("greetMeter", Matchers.notNullValue())
        .body("greetTimer", Matchers.notNullValue());
  }
}
