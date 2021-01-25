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
class SecretStrategyTest {

  @Test
  void testShouldResolveSecretFile(Vertx vertx, VertxTestContext context) {
    JsonObject config = new JsonObject().put("username", "secret/userfile").put("password", "secret/passfile");
    SecretStrategy.resolveSecret(vertx, config).onComplete(context.succeeding(res -> {
      context.verify(() -> {
        assertEquals("foo", res.getString("username"), "User must match");
        assertEquals("bar", res.getString("password"), "Pass must match");
      });
      context.completeNow();
    }));
  }

  @Test
  void testShouldFailBecauseUsernamePathIsMissing(Vertx vertx, VertxTestContext context) {
    JsonObject config = new JsonObject().put("username", null).put("password", "secret/passfile");
    SecretStrategy.resolveSecret(vertx, config).onComplete(context.failing(res -> context.completeNow()));
  }

  @Test
  void testShouldFailBecausePasswordPathIsMissing(Vertx vertx, VertxTestContext context) {
    JsonObject config = new JsonObject().put("username", "secret/userfile").put("password", null);
    SecretStrategy.resolveSecret(vertx, config).onComplete(context.failing(res -> context.completeNow()));
  }
  
  @Test
  void testShouldFailBecauseCriptoIsWrongType(Vertx vertx, VertxTestContext context) {
    JsonObject config = new JsonObject().put("username", "secret/userfile").put("password", "secret/passfile").put("crypto", new JsonObject());
    SecretStrategy.resolveSecret(vertx, config).onComplete(context.failing(res -> context.completeNow()));
  }
  
  @Test
  void testShouldResolveSecretFileWithCrypto(Vertx vertx, VertxTestContext context) {
    JsonObject crypto = new JsonObject().put("key", "foobar");
    JsonObject config = new JsonObject().put("username", "secret/encryptedUserfile")
        .put("password", "secret/encryptedPassfile").put("crypto", crypto);
    SecretStrategy.resolveSecret(vertx, config).onComplete(context.succeeding(res -> {
      context.verify(() -> {
        assertEquals("foo", res.getString("username"), "User must match");
        assertEquals("bar", res.getString("password"), "Pass must match");
      });
      context.completeNow();
    }));
  }
  
  @Test
  void testShouldResolveSecretFileWithCryptoFile(Vertx vertx, VertxTestContext context) {
    JsonObject crypto = new JsonObject().put("file", "secret/key");
    JsonObject config = new JsonObject().put("username", "secret/encryptedUserfile")
        .put("password", "secret/encryptedPassfile").put("crypto", crypto);
    SecretStrategy.resolveSecret(vertx, config).onComplete(context.succeeding(res -> {
      context.verify(() -> {
        assertEquals("foo", res.getString("username"), "User must match");
        assertEquals("bar", res.getString("password"), "Pass must match");
      });
      context.completeNow();
    }));
  }
}
