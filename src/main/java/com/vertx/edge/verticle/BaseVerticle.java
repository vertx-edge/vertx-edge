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

import io.vertx.core.Promise;
import io.vertx.servicediscovery.ServiceDiscovery;

/**
 * @author Luiz Schmidt
 */
public abstract class BaseVerticle extends AbstractComponentVerticle {

  protected ServiceDiscovery discovery;

  protected void up(Promise<Void> promise) {
    promise.complete();
  }

  protected void up() {
    // Nothing to do
  }

  @Override
  public final void start(Promise<Void> startPromise) throws Exception {
    discovery = ServiceDiscovery.create(vertx);

    super.initialize().future().onComplete(result -> {
      if (result.succeeded()) {
        Promise<Void> promise = Promise.promise();
        up(promise);
        up();

        promise.future().onComplete(startPromise);
      } else {
        startPromise.fail(result.cause().toString());
      }
    });
  }
}
