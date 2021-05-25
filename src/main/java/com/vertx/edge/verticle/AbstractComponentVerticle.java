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
package com.vertx.edge.verticle;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.vertx.edge.components.ComponentRegister;
import com.vertx.edge.deploy.injection.Injection;
import com.vertx.edge.utils.CompositeFutureBuilder;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.ServiceDiscovery;

/**
 * @author Luiz Schmidt
 */
public abstract class AbstractComponentVerticle extends AbstractVerticle {

  private static final long TIMEOUT_VERTICLE_UP = 30000L;
  protected ServiceDiscovery discovery;

  @Override
  public final void start(Promise<Void> startPromise) {
    discovery = ServiceDiscovery.create(vertx);
    Injection.create(vertx, this).inject().compose(v -> this.invokeAllComponents())
        .compose(v -> this.startBaseVerticle()).onComplete(startPromise);
  }

  protected abstract Future<Void> startBaseVerticle();

  /**
   * Get all the components of a class and superClasses
   * 
   * @return
   */
  private List<ComponentRegister> getAllTheComponentsToInitilize() {
    List<ComponentRegister> annotations = new ArrayList<>();

    boolean first = true;
    Class<?> clazz = this.getClass();
    do {
      if (!first)
        clazz = clazz.getSuperclass();
      first = false;
      annotations.addAll(setAnnotations(clazz));
    } while (clazz.getSuperclass() != BaseVerticle.class && clazz.getSuperclass() != Object.class);
    return annotations;
  }

  /**
   * Getting all the annotations ComponentRegister from the class
   * 
   * @param clazz
   * @return
   */
  private static List<ComponentRegister> setAnnotations(Class<?> clazz) {
    List<ComponentRegister> annotations = new ArrayList<>();
    if (clazz.getAnnotatedInterfaces() != null) {
      Arrays.stream(clazz.getAnnotatedInterfaces()).forEach(type -> {
        ComponentRegister component = ComponentRegister.getByClass((Class<?>) type.getType());
        if (component != null) {
          annotations.add(component);
        }
      });
    }
    return annotations;
  }

  /**
   * Invoke all components
   * 
   * @param components
   * @return
   */
  private Future<Void> invokeAllComponents() {
    List<ComponentRegister> components = this.getAllTheComponentsToInitilize();
    if (components.isEmpty())
      return Future.succeededFuture();

    CompositeFutureBuilder composite = CompositeFutureBuilder.create();
    for (ComponentRegister invokeModel : components)
      composite.add(invokeComponent(invokeModel.clazz(), invokeModel.method()));

    Promise<Void> promise = Promise.promise();
    composite.all().onComplete(promise);

    vertx.setTimer(config().getLong("timeout", TIMEOUT_VERTICLE_UP),
        res -> promise.tryFail("Timeout on executing models, be sure to complete() all futures/promise verticles"));

    return promise.future();
  }

  @SuppressWarnings("unchecked")
  private Future<Void> invokeComponent(Class<?> name, String methodName) {
    try {
      Method method = name.getMethod(methodName, Vertx.class, JsonObject.class);
      Object obj = method.invoke(this, vertx, config());

      return (Future<Void>) obj;
    } catch (ReflectiveOperationException e) {
      return Future.failedFuture(e);
    }
  }
}
