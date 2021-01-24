/*
 * Copyright (c) 2020-2021 Contributors
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vertx.commons.deploy.service;

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