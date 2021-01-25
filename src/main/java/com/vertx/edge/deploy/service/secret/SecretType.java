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

import com.vertx.edge.deploy.service.secret.strategy.BasicAuthStrategy;
import com.vertx.edge.deploy.service.secret.strategy.SecretStrategy;

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
      return BasicAuthStrategy.resolveSecret(config.getJsonObject("basic-auth"));
    }
  },
  SECRET_FILE {
    @Override
    public Future<JsonObject> getUserAndPassword(Vertx vertx, JsonObject config) {
      return SecretStrategy.resolveSecret(vertx, config.getJsonObject("secret-file"));
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
