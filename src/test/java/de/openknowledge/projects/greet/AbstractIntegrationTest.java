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
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;

/**
 * Provides testcontainers for integration tests.
 */
public abstract class AbstractIntegrationTest {

  protected static final String NETWORK_ALIAS_APPLICATION = "application";

  protected static final Network NETWORK = Network.newNetwork();

  protected static final GenericContainer<?> APPLICATION = new GenericContainer<>("quarkus-showcase")
      .withExposedPorts(8080)
      .withNetwork(NETWORK)
      .withNetworkAliases(NETWORK_ALIAS_APPLICATION)
      .withEnv("JAVA_OPTS", "-Djava.net.preferIPv4Stack=true -Djava.net.preferIPv4Addresses=true -javaagent:/jacoco-agent/org.jacoco.agent-runtime.jar=destfile=/jacoco-testcontainers/jacoco-testcontainers.exec")
      .withFileSystemBind("./target/jacoco-agent", "/jacoco-agent")
      .withFileSystemBind("./target/jacoco-testcontainers", "/jacoco-testcontainers")
      .waitingFor(Wait.forHealthcheck());

  static {
    APPLICATION.start();

    Runtime.getRuntime().addShutdownHook(new Thread(() -> APPLICATION.getDockerClient().stopContainerCmd(APPLICATION.getContainerId()).withTimeout(10).exec()));
  }
}
