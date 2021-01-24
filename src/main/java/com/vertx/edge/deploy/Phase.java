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
package com.vertx.edge.deploy;

import java.util.HashMap;
import java.util.Map;

import com.vertx.edge.utils.CompositeFutureBuilder;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * @author Luiz Schmidt
 */
public class Phase {

  private Map<String, JsonObject> list;

  public Phase(JsonObject json) {
    this.list = new HashMap<>();
    json.fieldNames().forEach(field -> this.list.put(field, json.getJsonObject(field)));
  }

  public Future<Void> deploy(Vertx vertx) {
    CompositeFutureBuilder builder = CompositeFutureBuilder.create();
    Deployer deployer = new Deployer(vertx);

    list.forEach((name, opts) -> {
      if (opts != null) {
        if (opts.getBoolean("enabled", Boolean.TRUE).booleanValue())
          builder.add(deployer.deploy(name, opts));
      } else {
        builder.add(deployer.deploy(name));
      }
    });

    return builder.all();
  }
}
