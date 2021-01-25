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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

/**
 * @author Luiz Schmidt
 *
 */
@ExtendWith(VertxExtension.class)
class SecretTest {

  @Test
  void testSecretIsNull(Vertx vertx, VertxTestContext context) {
    JsonObject config = new JsonObject();
    Secret.getUsernameAndPassword(vertx, config).onComplete(context.failing(cause -> context.completeNow()));
  }

  @Test
  void testSecretTypeBasic(Vertx vertx, VertxTestContext context) {
    JsonObject config = new JsonObject().put("basic-auth",
        new JsonObject().put("username", "user").put("password", "pass"));
    Secret.getUsernameAndPassword(vertx, config).onComplete(context.succeeding(res -> {
      context.verify(() -> {
        assertEquals("user", res.getString("username"), "User must match");
        assertEquals("pass", res.getString("password"), "Pass must match");
      });
      context.completeNow();
    }));
  }

  @Test
  void testSecretTypeSecretFile(Vertx vertx, VertxTestContext context) {
    JsonObject config = new JsonObject().put("secret-file",
        new JsonObject().put("username", "secret/userfile").put("password", "secret/passfile"));
    Secret.getUsernameAndPassword(vertx, config).onComplete(context.succeeding(res -> {
      context.verify(() -> {
        assertEquals("foo", res.getString("username"), "User must match");
        assertEquals("bar", res.getString("password"), "Pass must match");
      });
      context.completeNow();
    }));
  }

  @Test
  void testSecretTypeNoAuth(Vertx vertx, VertxTestContext context) {
    JsonObject config = new JsonObject().put("no-auth", null);
    Secret.getUsernameAndPassword(vertx, config).onComplete(context.succeeding(res -> {
      context.verify(() -> {
        assertNull(res.getString("username"), "User must be null");
        assertNull(res.getString("password"), "Pass must be null");
      });
      context.completeNow();
    }));
  }
}
