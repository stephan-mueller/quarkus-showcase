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

import de.openknowledge.projects.greet.domain.Greeting;
import de.openknowledge.projects.greet.domain.GreetingRepository;

import org.apache.commons.lang3.Validate;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * Service for greeting message.
 */
@ApplicationScoped
public class GreetingApplicationService {

  private final AtomicReference<String> greeting = new AtomicReference<>();

  private GreetingRepository repository;

  public GreetingApplicationService() {
    super();
  }

  /**
   * Create a new greeting service, reading the message from configuration.
   *
   * @param greeting greeting to use
   * @param repository the greeting repository
   */
  @Inject
  public GreetingApplicationService(@ConfigProperty(name = "app.greeting") final String greeting, final GreetingRepository repository) {
    this();
    this.greeting.set(Validate.notNull(greeting, "greeting must not be null"));
    this.repository = Validate.notNull(repository, "repository must not be null");
  }

  public String getGreeting() {
    return greeting.get();
  }

  public String getGreet(final String who) {
    return String.format("%s %s!", getGreeting(), who);
  }

  public Optional<Greeting> getResponse(final String salutation) {
    return repository.findBySalutation(salutation);
  }

  public void updateGreeting(final String greeting) {
    this.greeting.set(greeting);
  }
}