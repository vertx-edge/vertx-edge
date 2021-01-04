package com.vertx.commons.utils;

import java.util.ArrayList;
import java.util.List;

import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.impl.future.CompositeFutureImpl;

/**
 * @author Luiz Schmidt
 */
public class CompositeFutureBuilder {

  private List<Future<?>> futures;

  public CompositeFutureBuilder() {
    futures = new ArrayList<>();
  }

  public static CompositeFutureBuilder list() {
    return new CompositeFutureBuilder();
  }

  public <T> CompositeFutureBuilder add(Future<T> future) {
    futures.add(future);
    return this;
  }

  public CompositeFuture all(Handler<AsyncResult<CompositeFuture>> handler) {
    return CompositeFutureImpl.all(futures.toArray(new Future[futures.size()])).onComplete(handler);
  }

  public CompositeFuture join(Handler<AsyncResult<CompositeFuture>> handler) {
    return CompositeFutureImpl.join(futures.toArray(new Future[futures.size()])).onComplete(handler);
  }

  public CompositeFuture any(Handler<AsyncResult<CompositeFuture>> handler) {
    return CompositeFutureImpl.any(futures.toArray(new Future[futures.size()])).onComplete(handler);
  }

  public Handler<AsyncResult<Void>> addHandler() {
    Promise<Void> promise = Promise.promise();
    this.add(promise.future());
    return promise;
  }

  public Future<Void> all() {
    Promise<Void> promise = Promise.promise();
    CompositeFutureImpl.all(futures.toArray(new Future[futures.size()])).onSuccess(v -> promise.complete())
        .onFailure(promise::fail);
    return promise.future();
  }
}