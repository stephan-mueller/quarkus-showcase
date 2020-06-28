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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for the service {@link GreetingApplicationService}.
 */
class GreetingApplicationServiceTest {

  private GreetingApplicationService service;

  @BeforeEach
  void setUp() {
    service = new GreetingApplicationService("Hello");
  }

  @Test
  void getGreeting() {
    assertThat(service.getGreeting()).isEqualTo("Hello");
  }

  @Test
  void updateGreeting() {
    service.updateGreeting("Hola");
    assertThat(service.getGreeting()).isEqualTo("Hola");
  }

  @Test
  void getMessage() {
    assertThat(service.getMessage("World")).isEqualTo("Hello World!");
  }
}