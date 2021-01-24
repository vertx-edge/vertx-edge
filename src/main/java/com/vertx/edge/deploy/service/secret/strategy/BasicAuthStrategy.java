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
package com.vertx.edge.deploy.service.secret.strategy;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

/**
 * @author Luiz Schmidt
 */
public final class BasicAuthStrategy {

  private BasicAuthStrategy() {
    // Nothing to do
  }

  public static Future<JsonObject> resolveSecret(JsonObject config) {
    JsonObject basicAuth = config.getJsonObject("basic-auth");

    String username = basicAuth.getString("username");
    String password = basicAuth.getString("password");

    if (username == null || username.isEmpty()) {
      return Future.failedFuture("username cannot be null");
    } else if (password == null || password.isEmpty()) {
      return Future.failedFuture("password cannot be null");
    } else {
      return Future.succeededFuture(new JsonObject().put("username", username).put("password", password));
    }
  }
}
