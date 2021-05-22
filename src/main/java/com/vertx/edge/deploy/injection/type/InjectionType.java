package com.vertx.edge.deploy.injection.type;

import java.util.Objects;

import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.spi.ServiceType;

public interface InjectionType extends ServiceType {

  String TYPE = "injection";

  static Record createRecord(String name, JsonObject config) {
    Objects.requireNonNull(name);
    Objects.requireNonNull(config);
    return new Record().setName(name).setType(TYPE).setMetadata(config);
  }

}
