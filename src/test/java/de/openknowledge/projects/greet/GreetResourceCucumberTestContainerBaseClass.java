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

import org.testcontainers.containers.GenericContainer;

import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.EventHandler;
import io.cucumber.plugin.event.EventPublisher;
import io.cucumber.plugin.event.TestRunFinished;
import io.cucumber.plugin.event.TestRunStarted;

/**
 * Base class which starts a testcontainer for the cucumber test.
 */
public class GreetResourceCucumberTestContainerBaseClass extends AbstractIntegrationTest implements ConcurrentEventListener {

  @Override
  public void setEventPublisher(EventPublisher eventPublisher) {
    eventPublisher.registerHandlerFor(TestRunStarted.class, setup);
    eventPublisher.registerHandlerFor(TestRunFinished.class, teardown);
  }

  private EventHandler<TestRunStarted> setup = event -> beforeAll();

  private void beforeAll() {
    APPLICATION.start();
  }

  private EventHandler<TestRunFinished> teardown = event -> afterAll();

  private void afterAll() {
    APPLICATION.stop();
  }

  public static GenericContainer<?> getContainer() {
    return APPLICATION;
  }
}
