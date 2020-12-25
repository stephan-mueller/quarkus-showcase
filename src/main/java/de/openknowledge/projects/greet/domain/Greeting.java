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

import org.apache.commons.lang3.Validate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
@Table(name = "GREETING")
public class Greeting extends PanacheEntity {

  @Column(name = "SALUTATION", nullable = false, unique = true)
  private String salutation;

  @Column(name = "RESPONSE", nullable = false)
  private String response;

  protected Greeting() {
    super();
  }

  public Greeting(final String salutation, final String response) {
    this();
    this.salutation = Validate.notNull(salutation, "salutation must not be null");
    this.setResponse(response);
  }

  public String getSalutation() {
    return this.salutation;
  }

  public String getResponse() {
    return this.response;
  }

  public void setResponse(final String response) {
    this.response = Validate.notNull(response, "response must not be null");
  }
}