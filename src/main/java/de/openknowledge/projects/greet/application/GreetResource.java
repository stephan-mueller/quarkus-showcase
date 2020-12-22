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

import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Metered;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * A resource that provides access to the world.
 */
@Path("greet")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class GreetResource {

  private static final Logger LOG = LoggerFactory.getLogger(GreetResource.class);

  @Inject
  private GreetingApplicationService service;

  @GET
  @Path("{name}")
  @Operation(operationId = "greetSomeone", description = "Greet someone")
  @APIResponse(responseCode = "200", description = "Ok", content = @Content(schema = @Schema(implementation = GreetDTO.class)))
  @Counted(name = "greetCount", absolute = true)
  @Metered(name = "greetMeter", absolute = true)
  @Timed(name = "greetTimer", absolute = true, unit = MetricUnits.MILLISECONDS)
  public Response greet(@Parameter(description = "name") @PathParam("name") final String name) {
    LOG.info("Greet {}", name);

    GreetDTO message = new GreetDTO(service.getMessage(name));

    LOG.info("{}", message);

    return Response.status(Response.Status.OK)
        .entity(message)
        .build();
  }

  @GET
  @Operation(operationId = "greetTheWorld", description = "Greet the world")
  @APIResponse(responseCode = "200", description = "Ok", content = @Content(schema = @Schema(implementation = GreetDTO.class)))
  public Response greetTheWorld() {
    return greet("World");
  }

  @Path("greeting")
  @GET
  @Operation(operationId = "getGreeting", description = "Get greeting")
  @APIResponse(responseCode = "200", description = "Ok", content = @Content(schema = @Schema(implementation = GreetingDTO.class)))
  public Response getGreeting() {
    LOG.info("Get greeting");

    GreetingDTO greeting = new GreetingDTO(service.getGreeting());

    LOG.info("{}", greeting);

    return Response.status(Response.Status.OK)
        .entity(greeting)
        .build();
  }

  @Path("greeting")
  @PUT
  @Operation(operationId = "updateGreeting", description = "Update greeting")
  @RequestBody(name = "greeting", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
      schema = @Schema(implementation = GreetingDTO.class, type = SchemaType.OBJECT, example = "{\"greeting\" : \"Hola\"}")))
  @APIResponse(responseCode = "204", description = "Greeting updated")
  @APIResponse(responseCode = "400", description = "Invalid 'greeting' request")
  public Response updateGreeting(@Valid final GreetingDTO greeting) {
    LOG.info("Set greeting to {}", greeting.getGreeting());

    service.updateGreeting(greeting.getGreeting());

    LOG.info("Greeting updated");

    return Response.status(Response.Status.NO_CONTENT).build();
  }
}
