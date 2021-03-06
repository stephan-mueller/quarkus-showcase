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

import static org.assertj.core.api.Assertions.assertThat;

import de.openknowledge.projects.greet.domain.Greeting;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import javax.ws.rs.core.Response;

/**
 * Test class for the resource {@link GreetResource}.
 */
@ExtendWith(MockitoExtension.class)
class GreetResourceTest {

  @InjectMocks
  private GreetResource resource;

  @Mock
  private GreetingApplicationService service;

  @BeforeEach
  void setUp() {
    Mockito.lenient().doCallRealMethod().when(service).getGreet(Mockito.anyString());
  }

  @Test
  void greetShouldReturn200() {
    Mockito.doReturn("Hola").when(service).getGreeting();

    Response response = resource.greet("Stephan");
    assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    assertThat(response.getEntity()).isEqualTo(new GreetDTO("Hola Stephan!"));

    Mockito.verify(service).getGreeting();
    Mockito.verify(service).getGreet(Mockito.anyString());
    Mockito.verifyNoMoreInteractions(service);
  }

  @Test
  void greetTheWorldShouldReturn200() {
    Mockito.doReturn("Hello").when(service).getGreeting();

    Response response = resource.greetTheWorld();
    assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    assertThat(response.getEntity()).isEqualTo(new GreetDTO("Hello World!"));

    Mockito.verify(service).getGreeting();
    Mockito.verify(service).getGreet(Mockito.anyString());
    Mockito.verifyNoMoreInteractions(service);
  }

  @Test
  void getGreetingShouldReturn200() {
    Mockito.doReturn("Hola").when(service).getGreeting();

    Response response = resource.getGreeting();
    assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    assertThat(response.getEntity()).isEqualTo(new GreetingDTO("Hola"));

    Mockito.verify(service).getGreeting();
    Mockito.verifyNoMoreInteractions(service);
  }

  @Test
  void getResponseShouldReturn200() {
    Mockito.doReturn(Optional.of(new Greeting("Marco", "Polo"))).when(service).getResponse(Mockito.anyString());

    Response response = resource.getResponse("Macro");
    assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    assertThat(response.getEntity()).isEqualTo(new ResponseDTO("Polo"));

    Mockito.verify(service).getResponse(Mockito.anyString());
    Mockito.verifyNoMoreInteractions(service);
  }

  @Test
  void getResponseShouldReturn400ForUnknownResponse() {
    Mockito.doReturn(Optional.empty()).when(service).getResponse(Mockito.anyString());

    Response response = resource.getResponse("Hello");
    assertThat(response.getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());

    Mockito.verify(service).getResponse(Mockito.anyString());
    Mockito.verifyNoMoreInteractions(service);
  }

  @Test
  void updateGreetingShouldReturn204() {
    Mockito.doNothing().when(service).updateGreeting(Mockito.anyString());

    GreetingDTO newGreeting = new GreetingDTO("Hola");

    Response response = resource.updateGreeting(newGreeting);

    assertThat(response.getStatus()).isEqualTo(Response.Status.NO_CONTENT.getStatusCode());

    Mockito.verify(service).updateGreeting(Mockito.anyString());
    Mockito.verifyNoMoreInteractions(service);
  }
}
