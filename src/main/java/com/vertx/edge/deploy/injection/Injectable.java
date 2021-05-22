package com.vertx.edge.deploy.injection;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.ServiceDiscovery;

public abstract class Injectable {

  protected Vertx vertx;
  protected ServiceDiscovery discovery;
  private JsonObject config;

  public final Future<Void> start(Vertx vertx, JsonObject config) {
    this.vertx = vertx;
    this.config = config;
    this.discovery = ServiceDiscovery.create(vertx);
    
    Promise<Void> promise = Promise.promise();
    Injection.create(vertx, this).inject().onSuccess(v -> {
      this.up(); 
      this.up(promise);
    }).onFailure(promise::fail);
    
    return promise.future();
  }

  protected void up() {}

  protected void up(Promise<Void> promise) {
    promise.complete();
  }

  protected final JsonObject config() {
    return this.config;
  }
}
