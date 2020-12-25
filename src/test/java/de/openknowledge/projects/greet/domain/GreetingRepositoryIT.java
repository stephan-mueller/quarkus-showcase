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
package de.openknowledge.projects.greet.domain;

import com.github.database.rider.cdi.api.DBRider;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.SeedStrategy;

import de.openknowledge.projects.greet.DatabaseTestResource;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import javax.inject.Inject;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

/**
 * Integration test for the repository {@link GreetingRepository}.
 */
@QuarkusTest
@QuarkusTestResource(DatabaseTestResource.class)
@DBRider
@DataSet(value = "greetings.yml", strategy = SeedStrategy.CLEAN_INSERT, skipCleaningFor = "flyway_schema_history")
class GreetingRepositoryIT {

  @Inject
  private GreetingRepository repository;

  @Test
  void findBySalutationShouldReturnEmptyOptional() {
    Optional<Greeting> optional = repository.findBySalutation("Polo");
    Assertions.assertThat(!optional.isPresent()).isTrue();
  }

  @Test
  void findBySalutationShouldReturnOptional() {
    Optional<Greeting> optional = repository.findBySalutation("Marco");
    Assertions.assertThat(optional.isPresent()).isTrue();

    Greeting greeting = optional.get();
    Assertions.assertThat(greeting.id).isEqualTo(1L);
    Assertions.assertThat(greeting.getSalutation()).isEqualTo("Marco");
    Assertions.assertThat(greeting.getResponse()).isEqualTo("Polo");
  }
}
