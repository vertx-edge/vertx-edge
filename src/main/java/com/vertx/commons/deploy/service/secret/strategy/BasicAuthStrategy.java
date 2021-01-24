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
package com.vertx.commons.deploy.service.secret.strategy;

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
