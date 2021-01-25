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

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.ServiceDiscovery;

/**
 * @author Luiz Schmidt
 */
public abstract class BaseVerticle extends AbstractComponentVerticle {

  protected ServiceDiscovery discovery;

  protected void up(Promise<Void> promise) {
    promise.complete();
  }

  protected void up() {}

  @Override
  public final Future<Void> startBaseVerticle() {
    discovery = ServiceDiscovery.create(vertx);
    Promise<Void> promise = Promise.promise();
    vertx.eventBus().<JsonObject>consumer("configuration.store", this::updateLocalConfig);
    
    up();
    up(promise);
    return promise.future();
  }
  
  /**
   * Handler to on config change.
   * 
   * @param phaseConfig
   */
  protected void onConfigChange(JsonObject phaseConfig) {}
  
  /**
   * Every time config as change this method is notified.
   * 
   * @param config as json
   */
  protected void updateLocalConfig(Message<JsonObject> message) {
    JsonObject json = message.body();
    JsonArray phases = json.getJsonObject("strategy").getJsonArray("phases");

    for (int i = 0; i < phases.size(); i++) {
      JsonObject phase = phases.getJsonObject(i);
      if (phase.containsKey(this.getClass().getName())) {
        JsonObject deploy = phase.getJsonObject(this.getClass().getName());

        if (deploy != null && deploy.containsKey("config")) {
          JsonObject config = deploy.getJsonObject("config", new JsonObject());
          this.config().mergeIn(config);

          this.onConfigChange(phase);
        }
        break;
      }
    }
  }
}
