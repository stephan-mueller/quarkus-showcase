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

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * Liveness Health check for the application.
 */
@Liveness
@ApplicationScoped
public class GreetLivenessCheck implements HealthCheck {

  private GreetingApplicationService service;

  @Inject
  public GreetLivenessCheck(GreetingApplicationService service) {
    this.service = service;
  }

  @Override
  public HealthCheckResponse call() {
    String greeting = service.getGreeting();
    return HealthCheckResponse.named("LivenessCheck")
        .state(StringUtils.isNotBlank(greeting))
        .withData("greeting", greeting)
        .build();
  }
}
