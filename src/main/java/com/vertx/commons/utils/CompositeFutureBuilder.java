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