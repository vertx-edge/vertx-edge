package com.vertx.commons.verticle;

import io.vertx.core.Promise;
import io.vertx.servicediscovery.ServiceDiscovery;

/**
 * @author Luiz Schmidt
 */
public abstract class BaseVerticle extends AbstractComponentVerticle {

  protected ServiceDiscovery discovery;

  protected void up(Promise<Void> promise) {
    promise.complete();
  }

  protected void up() {
    // Nothing to do
  }

  @Override
  public final void start(Promise<Void> startPromise) throws Exception {
    discovery = ServiceDiscovery.create(vertx);

    super.initialize().future().onComplete(result -> {
      if (result.succeeded()) {
        Promise<Void> promise = Promise.promise();
        up(promise);
        up();

        promise.future().onComplete(startPromise);
      } else {
        startPromise.fail(result.cause().toString());
      }
    });
  }
}
