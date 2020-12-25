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

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;

import java.net.URI;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

/**
 * Health check for the resource {@link GreetResource}.
 */
@Readiness
@ApplicationScoped
public class GreetResourceHealthCheck implements HealthCheck {

  @Inject
  private Config config;

  @Override
  public HealthCheckResponse call() {
    HealthCheckResponseBuilder builder = HealthCheckResponse.named(GreetResource.class.getSimpleName());

    Client client = ClientBuilder.newClient();

    Response response = client
        .target(getResourceUri())
        .request()
        .header("ORIGIN", String.format("%s:%d", "localhost", getPort()))
        .options();

    boolean up = Response.Status.OK.equals(response.getStatusInfo().toEnum());

    if (up) {
      builder.withData("resource", "available").up();
    } else {
      builder.withData("resource", "not available").down();
    }

    client.close();

    return builder.build();
  }

  private URI getResourceUri() {
    return UriBuilder.fromPath(config.getValue("quarkus.http.root-path", String.class))
        .path(GreetApplication.class.getAnnotation(ApplicationPath.class).value())
        .path(GreetResource.class.getAnnotation(Path.class).value())
        .scheme("http")
        .host("localhost")
        .port(getPort())
        .build();
  }

  private Integer getPort() {
    return getQuarkusProfile().equals("test") ? getQuarkusHttpTestPort() : getQuarkusHttpPort();
  }

  private String getQuarkusProfile() {
    return config.getValue("quarkus.profile", String.class);
  }

  private Integer getQuarkusHttpTestPort() {
    return config.getValue("quarkus.http.test-port", Integer.class);
  }

  private Integer getQuarkusHttpPort() {
    return config.getValue("quarkus.http.port", Integer.class);
  }
}
