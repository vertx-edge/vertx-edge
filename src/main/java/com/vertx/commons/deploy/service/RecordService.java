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