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
package com.vertx.edge.deploy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.vertx.core.Context;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.servicediscovery.ServiceDiscovery;

/**
 * @author Luiz Schmidt
 *
 */
@ExtendWith(VertxExtension.class)
class ServiceDiscoveryVerticleTest {

  private ServiceDiscoveryVerticle verticle = new ServiceDiscoveryVerticle();
  private ServiceDiscovery discovery;

  @BeforeEach
  private void beforeEach(Vertx vertx, VertxTestContext context) throws IllegalAccessException {
    discovery = ServiceDiscovery.create(vertx);
    FieldUtils.writeField(verticle, "vertx", vertx, true);
    FieldUtils.writeField(verticle, "discovery", discovery, true);
    context.completeNow();
  }

  @Test
  void testShouldServiceDiscoveryUp(Vertx vertx, VertxTestContext context) throws IllegalAccessException {
    Promise<Void> promise = Promise.promise();

    Context vertContext = vertx.getOrCreateContext();
    vertContext.config().put("base-package", "com.vertx.edge").put("services",
        new JsonObject().put("myservice", new JsonObject()));
    FieldUtils.writeField(verticle, "context", vertContext, true);

    verticle.up(promise);
    promise.future().onComplete(context.succeeding(result -> {
      discovery.getRecord(new JsonObject().put("name", "myservice")).onComplete(context.succeeding(res -> {
        context.verify(() -> {
          assertNotNull(res, "record must be not null");
          assertEquals("myservice", res.getName(), "same name");
        });
        context.completeNow();
      }));
    }));
  }
}
