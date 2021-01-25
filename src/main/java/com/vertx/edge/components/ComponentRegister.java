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

import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * @author Luiz Schmidt
 */
@Accessors(fluent = true)
public enum ComponentRegister {

  TCP_SERVER(TCPServer.class, "startTcpServer"), 
  TCP_CLIENT(TCPClient.class, "startTcpClient");

  @Getter
  private Class<?> clazz;
  @Getter
  private String method;

  ComponentRegister(Class<?> name, String method) {
    this.clazz = name;
    this.method = method;
  }

  /**
   * @param type
   * @return
   */
  public static ComponentRegister getByClass(Class<?> clazz) {
    for (ComponentRegister cr : ComponentRegister.values()) {
      if(cr.clazz().equals(clazz)) {
        return cr;
      }
    }
    return null;
  }
}
