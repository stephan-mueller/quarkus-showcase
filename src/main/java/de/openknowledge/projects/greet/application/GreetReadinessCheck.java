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

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;

/**
 * Readiness Health check for the application.
 */
@Readiness
@ApplicationScoped
public class GreetReadinessCheck implements HealthCheck {

  private AtomicLong readyTime = new AtomicLong(0);

  @Override
  public HealthCheckResponse call() {
    return HealthCheckResponse.named("ReadinessCheck")
        .state(isReady())
        .withData("time", readyTime.get())
        .build();
  }

  public void onStartUp(@Observes @Initialized(ApplicationScoped.class) Object init) {
    readyTime = new AtomicLong(System.currentTimeMillis());
  }

  /**
   * Become ready after 5 seconds
   *
   * @return true if application ready
   */
  private boolean isReady() {
    return Duration.ofMillis(System.currentTimeMillis() - readyTime.get()).getSeconds() >= 1;
  }
}
