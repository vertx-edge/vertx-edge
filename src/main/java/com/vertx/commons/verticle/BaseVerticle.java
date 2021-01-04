package com.vertx.commons.verticle;

import java.util.Objects;

import io.vertx.core.Promise;
import io.vertx.servicediscovery.ServiceDiscovery;

/**
 * @author Luiz Schmidt
 */
public abstract class BaseVerticle extends AbstractComponentVerticle {

  protected ServiceDiscovery discovery;

  protected abstract Promise<Void> up();

  @Override
  public final void start(Promise<Void> startPromise) throws Exception {
    discovery = ServiceDiscovery.create(vertx);

    super.initialize().future().onComplete(result -> {
      if (result.succeeded()) {
        Promise<Void> promise = up();
        Objects.requireNonNull(promise, "Verticle is not correctly completing the initialization step, "
            + "please check your implementation of the up() method.");

        promise.future().onComplete(startPromise);
      } else {
        startPromise.fail(result.cause().toString());
      }
    });
  }
}
