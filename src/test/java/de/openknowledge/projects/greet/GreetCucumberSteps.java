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

import de.openknowledge.projects.greet.application.GreetingDTO;

import org.hamcrest.Matchers;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

/**
 * Step Definitions for the cucumber test {@link GreetCucumberIT}.
 */
public class GreetCucumberSteps extends AbstractTestcontainersIT {

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
