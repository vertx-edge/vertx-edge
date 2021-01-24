/*
 * Vert.x Edge, open source.
 * Copyright (C) 2020-2021 Vert.x Edge
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vertx.edge.components;

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
