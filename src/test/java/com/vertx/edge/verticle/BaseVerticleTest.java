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
package com.vertx.edge.verticle;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

/**
 * @author Luiz Schmidt
 *
 */
@ExtendWith(VertxExtension.class)
class BaseVerticleTest {

  @Test
  void testUp(Vertx vertx, VertxTestContext context) {
    Checkpoint up = context.checkpoint();
    Checkpoint upPromise = context.checkpoint();
    BaseVerticle base = new BaseVerticle() {
      @Override
      protected void up() {
        up.flag();
      }

      @Override
      protected void up(Promise<Void> promise) {
        upPromise.flag();
        promise.complete();
      }
    };
    vertx.deployVerticle(base).onComplete(context.succeeding(res -> {
      assertNotNull(res, "deploymentId must be not null.");
      context.completeNow();
    }));
  }

  @Test
  void testFailedUP(Vertx vertx, VertxTestContext context) {
    BaseVerticle base = new BaseVerticle() {
      @Override
      protected void up(Promise<Void> promise) {
        promise.fail("error");
      }
    };
    vertx.deployVerticle(base).onComplete(context.failing(res -> context.completeNow()));
  }
}
