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

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.startupcheck.OneShotStartupCheckStrategy;
import org.testcontainers.utility.MountableFile;

import java.time.Duration;

/**
 * Postman test runner for the application.
 */
public class GreetPostmanIT extends AbstractIntegrationTest {

  private static final Logger LOG = LoggerFactory.getLogger(GreetResourceIT.class);

  private static final GenericContainer<?> NEWMAN = new GenericContainer<>("postman/newman:5.1.0-alpine")
      .withNetwork(NETWORK)
      .dependsOn(APPLICATION)
      .withCopyFileToContainer(MountableFile.forClasspathResource("postman/hello-world.postman_collection.json"),
                               "/etc/newman/hello-world.postman_collection.json")
      .withCopyFileToContainer(MountableFile.forClasspathResource("postman/hello-world.postman_environment.json"),
                               "/etc/newman/hello-world.postman_environment.json")
      .withFileSystemBind("target/postman/reports", "/etc/newman/reports", BindMode.READ_WRITE)
      .withStartupCheckStrategy(new OneShotStartupCheckStrategy().withTimeout(Duration.ofSeconds(5)));

  @Test
  void run() {
    NEWMAN.withCommand("run", "hello-world.postman_collection.json",
                       "--environment=hello-world.postman_environment.json",
                       "--reporters=cli,junit",
                       "--reporter-junit-export=reports/hello-world.newman-report.xml");
    NEWMAN.start();

    LOG.info(NEWMAN.getLogs());

    assertThat(NEWMAN.getCurrentContainerInfo().getState().getExitCode()).isEqualTo(0);
  }
}
