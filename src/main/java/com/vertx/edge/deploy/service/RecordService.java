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

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;

/**
 * @author Luiz Schmidt
 */
public interface RecordService {

  Future<Record> newRecord(Vertx vertx, JsonObject config);

  static String buildErrorMessage(String name, Throwable cause) {
    return "Error on retrieve " + name + " from ServiceDiscovery, be sure to configure '" + name
        + "' service on strategy json. reason -> " + cause.getMessage();
  }

}
