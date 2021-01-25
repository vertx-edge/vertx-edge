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

import java.util.Objects;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author Luiz Schmidt
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BasicAuthStrategy {

  public static Future<JsonObject> resolveSecret(JsonObject basicAuth) {
    Objects.requireNonNull(basicAuth, "'basic-auth' must be a json object.");

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
