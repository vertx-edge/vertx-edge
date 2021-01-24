/*
 * Copyright (c) 2020-2021 Contributors
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vertx.commons.components;

/**
 * @author Luiz Schmidt
 */
public enum ComponentRegister {

  TCP_SERVER(TCPServer.class, "startTcpServer"), 
  TCP_CLIENT(TCPClient.class, "startTcpClient"),
  LIVENESS(LivenessCheckable.class, "startLivenessCheck"), 
  READINESS(ReadinessCheckable.class, "startReadinessCheck");

  private String name;
  private String method;

  ComponentRegister(Class<?> name, String method) {
    this.name = name.getName();
    this.method = method;
  }

  public String getName() {
    return name;
  }

  public String getMethod() {
    return method;
  }
}
