package com.vertx.edge.deploy.injection;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.ServiceDiscovery;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Injectable {

  private static final String JSON_TIMEOUT_KEY = "timeout";
  private static final int DEFAULT_TIMEOUT = 5000;
  private static final String MESSAGE_TIMEOUT = "Timeout in class %s in the up(promise) or up() method, make sure that the promise is complete or failed. The configured timeout is at %d milliseconds, to change use the \"%s\" setting.";

  protected Vertx vertx;
  protected ServiceDiscovery discovery;
  private JsonObject config;

  public final Future<Void> start(Vertx vertx, JsonObject config) {
    this.vertx = vertx;
    this.config = config;
    this.discovery = ServiceDiscovery.create(vertx);

    Promise<Void> promise = Promise.promise();

    int timeout = config().getInteger(JSON_TIMEOUT_KEY, DEFAULT_TIMEOUT);
    Injection.create(vertx, this).inject().onSuccess(v -> {
      this.vertx.setTimer(timeout, timer -> promise
          .tryFail(String.format(MESSAGE_TIMEOUT, this.getClass().getSimpleName(), timeout, JSON_TIMEOUT_KEY)));
      
      try {
        this.up();
        this.up(promise);
      }catch(RuntimeException e) {
        promise.fail(e);
      }
    }).onFailure(promise::fail);

    return promise.future();
  }

  protected void up() {
    //Nothing to do
  }

  protected void up(Promise<Void> promise) {
    promise.complete();
  }

  protected final JsonObject config() {
    return this.config;
  }
}
