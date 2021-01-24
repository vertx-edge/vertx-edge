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
package com.vertx.edge.components;

import com.vertx.edge.codec.StatusHealthCodec;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.healthchecks.Status;

/**
 * @author Luiz Schmidt
 */
public interface ReadinessCheckable {

  default Future<Void> startReadinessCheck(Vertx vertx, JsonObject vertxConfig) {
    DeliveryOptions opts = new DeliveryOptions().setCodecName(StatusHealthCodec.class.getName());
    String address = "healthcheck.readiness.".concat(this.getClass().getName());

    vertx.eventBus().<String>consumer(address, res -> checkReadiness(read -> res.reply(read, opts)));

    Long timeWait = vertxConfig.getJsonObject("readiness", new JsonObject()).getLong("timeWait");
    JsonObject myClass = new JsonObject().put("className", this.getClass().getName()).put("timeWait", timeWait);

    vertx.eventBus().publish("healthcheck.readiness.register", myClass);
    return Future.succeededFuture();
  }

  void checkReadiness(Handler<Status> handler);
}
