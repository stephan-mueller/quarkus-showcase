/*
 * 02.07.2019 AP6T6
 * Copyright (c) 2019 HUK-COBURG. All Rights Reserved.
 * Copyright (C) 2019 open knowledge GmbH
 */
package de.openknowledge.projects.greet;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.core.Response;

/**
 * Test class for the resource {@link GreetResource}.
 */
@ExtendWith(MockitoExtension.class)
public class GreetResourceTest {

  @InjectMocks
  private GreetResource resource;

  @Mock
  private GreetingApplicationService service;

  @BeforeEach
  public void setUp() {
    Mockito.lenient().doCallRealMethod().when(service).getMessage(Mockito.anyString());
  }

  @Test
  public void greet() {
    Mockito.doReturn("Hola").when(service).getGreeting();

    Response response = resource.greet("Stephan");
    assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    assertThat(response.getEntity()).isEqualTo(new GreetDTO("Hola Stephan!"));

    Mockito.verify(service).getGreeting();
    Mockito.verify(service).getMessage(Mockito.anyString());
    Mockito.verifyNoMoreInteractions(service);
  }

  @Test
  public void greetTheWorld() {
    Mockito.doReturn("Hello").when(service).getGreeting();

    Response response = resource.greetTheWorld();
    assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    assertThat(response.getEntity()).isEqualTo(new GreetDTO("Hello World!"));

    Mockito.verify(service).getGreeting();
    Mockito.verify(service).getMessage(Mockito.anyString());
    Mockito.verifyNoMoreInteractions(service);
  }

  @Test
  void getGreeting() {
    Mockito.doReturn("Hola").when(service).getGreeting();

    Response response = resource.getGreeting();
    assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    assertThat(response.getEntity()).isEqualTo(new GreetingDTO("Hola"));

    Mockito.verify(service).getGreeting();
    Mockito.verifyNoMoreInteractions(service);
  }

  @Test
  void updateGreeting() {
    Mockito.doNothing().when(service).updateGreeting(Mockito.anyString());

    GreetingDTO newGreeting = new GreetingDTO("Hole");

    Response response = resource.updateGreeting(newGreeting);

    assertThat(response.getStatus()).isEqualTo(Response.Status.NO_CONTENT.getStatusCode());

    Mockito.verify(service).updateGreeting(Mockito.anyString());
    Mockito.verifyNoMoreInteractions(service);
  }
}
