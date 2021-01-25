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
package com.vertx.edge.deploy.service.secret;

import java.util.Objects;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author Luiz Schmidt
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Secret {

  public static final String PASSWORD_LITERAL = "password";
  public static final String USERNAME_LITERAL = "username";
  private static final String SECRET_NOT_FOUND = "Please enter the database authentication type. "
      + "(eg. secret-file, basic-auth, no-auth)";

  /**
   * Return an JsonObject with username and password
   * 
   * @param vertx
   * @param config
   * @return
   */
  public static Future<JsonObject> getUsernameAndPassword(Vertx vertx, JsonObject config) {
    Objects.requireNonNull(config, "Secret json config is null.");
    SecretType type = chooseType(config);

    if (type == null) {
      return Future.failedFuture(SECRET_NOT_FOUND);
    }

    JsonObject configuration = config.copy();
    clear(config);
    Promise<JsonObject> promise = Promise.promise();
    type.getUserAndPassword(vertx, configuration).onSuccess(secret -> promise.complete(buildJsonAuthFile(secret)))
        .onFailure(promise::fail);
    return promise.future();
  }

  private static SecretType chooseType(JsonObject config) {
    SecretType type = null;
    if (config.containsKey("secret-file")) {
      type = SecretType.SECRET_FILE;
    } else if (config.containsKey("basic-auth")) {
      type = SecretType.BASIC_AUTH;
    } else if (config.containsKey("no-auth")) {
      type = SecretType.NO_AUTH;
    }
    return type;
  }

  private static JsonObject buildJsonAuthFile(JsonObject secret) {
    return new JsonObject().put(USERNAME_LITERAL, secret.getString(USERNAME_LITERAL)).put(PASSWORD_LITERAL,
        secret.getString(PASSWORD_LITERAL));
  }

  private static void clear(JsonObject config) {
    config.remove("user");
    config.remove("pass");
    config.remove(USERNAME_LITERAL);
    config.remove(PASSWORD_LITERAL);
  }
}
