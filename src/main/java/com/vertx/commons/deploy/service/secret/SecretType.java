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
package com.vertx.commons.deploy.service.secret;

import com.vertx.commons.deploy.service.secret.strategy.BasicAuthStrategy;
import com.vertx.commons.deploy.service.secret.strategy.SecretStrategy;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * @author Luiz Schmidt
 */
public enum SecretType {
  BASIC_AUTH {
    @Override
    public Future<JsonObject> getUserAndPassword(Vertx vertx, JsonObject config) {
      return BasicAuthStrategy.resolveSecret(config);
    }
  },
  SECRET_FILE {
    @Override
    public Future<JsonObject> getUserAndPassword(Vertx vertx, JsonObject config) {
      return SecretStrategy.resolveSecret(vertx, config);
    }
  },
  NO_AUTH {
    @Override
    public Future<JsonObject> getUserAndPassword(Vertx vertx, JsonObject config) {
      return Future.succeededFuture(new JsonObject());
    }
  };

  public abstract Future<JsonObject> getUserAndPassword(Vertx vertx, JsonObject config);
}
