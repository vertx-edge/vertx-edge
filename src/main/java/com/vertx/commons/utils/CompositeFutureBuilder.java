package com.vertx.commons.utils;

import java.util.ArrayList;
import java.util.List;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;

/**
 * @author Luiz Schmidt
 */
public class CompositeFutureBuilder {

  private List<Future<?>> futures;

  public CompositeFutureBuilder() {
    futures = new ArrayList<>();
  }

  public static CompositeFutureBuilder create() {
    return new CompositeFutureBuilder();
  }

  public <T> CompositeFutureBuilder add(Future<T> future) {
    futures.add(future);
    return this;
  }

  public Future<Void> all() {
    Promise<Void> promise = Promise.promise();
    CompositeFuture.all(new ArrayList<>(futures))
      .onSuccess(v -> promise.complete())
      .onFailure(promise::fail);
    return promise.future();
  }

  public Future<Void> any() {
    Promise<Void> promise = Promise.promise();
    CompositeFuture.any(new ArrayList<>(futures))
      .onSuccess(v -> promise.complete())
      .onFailure(promise::fail);
    return promise.future();
  }

  public Future<Void> join() {
    Promise<Void> promise = Promise.promise();
    CompositeFuture.join(new ArrayList<>(futures))
      .onSuccess(v -> promise.complete())
      .onFailure(promise::fail);
    return promise.future();
  }
}