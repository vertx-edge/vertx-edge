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

import static org.junit.jupiter.api.Assertions.assertEquals;

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
class BasicAuthStrategyTest {

  @Test
  void testShouldResolveBasicAuth(Vertx vertx, VertxTestContext context) {
    JsonObject config = new JsonObject().put("username", "user").put("password", "pass");
    BasicAuthStrategy.resolveSecret(config).onComplete(context.succeeding(res -> {
      context.verify(() -> {
        assertEquals("user", res.getString("username"), "User must match");
        assertEquals("pass", res.getString("password"), "Pass must match");
      });
      context.completeNow();
    }));
  }

  @Test
  void testShouldFailBecauseMissingPassword(Vertx vertx, VertxTestContext context) {
    JsonObject config = new JsonObject().put("username", "user");
    BasicAuthStrategy.resolveSecret(config).onComplete(context.failing(res -> context.completeNow()));
  }
  
  @Test
  void testShouldFailBecauseMissingUsername(Vertx vertx, VertxTestContext context) {
    JsonObject config = new JsonObject().put("password", "pass");
    BasicAuthStrategy.resolveSecret(config).onComplete(context.failing(res -> context.completeNow()));
  }
}
