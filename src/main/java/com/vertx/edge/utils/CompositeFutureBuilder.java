/*
 * Vert.x Edge, open source.
 * Copyright (C) 2020-2021 Vert.x Edge
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vertx.edge.utils;

import java.util.ArrayList;
import java.util.List;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import lombok.NoArgsConstructor;

/**
 * @author Luiz Schmidt
 */
@NoArgsConstructor
public class CompositeFutureBuilder {

  private List<Future<?>> futures = new ArrayList<>();

  public static CompositeFutureBuilder create() {
    return new CompositeFutureBuilder();
  }

  public <T> CompositeFutureBuilder add(Promise<T> promise) {
    this.add(promise.future());
    return this;
  }
  
  public <T> CompositeFutureBuilder add(Future<T> future) {
    futures.add(future);
    return this;
  }
  
  public <T> CompositeFutureBuilder addHandler(Promise<T> promise) {
    this.addHandler(promise.future());
    return this;
  }
  
  public <T> CompositeFutureBuilder addHandler(Future<T> future) {
    Promise<Void> promise = Promise.promise();
    future.onComplete(v -> promise.complete());
    futures.add(promise.future());
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
