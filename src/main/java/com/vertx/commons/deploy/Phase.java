package com.vertx.commons.deploy;

import java.util.HashMap;
import java.util.Map;

import com.vertx.commons.utils.CompositeFutureBuilder;

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
