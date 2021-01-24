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
package com.vertx.commons.verticle;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.vertx.commons.components.ComponentRegister;
import com.vertx.commons.utils.CompositeFutureBuilder;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * @author Luiz Schmidt
 */
public abstract class AbstractComponentVerticle extends AbstractVerticle {

  private static final long TIMEOUT_VERTICLE_UP = 30000L;

  public Promise<Void> initialize() {
    Promise<Void> startPromise = Promise.promise();

    vertx.eventBus().<JsonObject>localConsumer("configuration.store",
        message -> this.updateLocalConfig(message.body()));

    this.invokeAllComponents().onComplete(startPromise);
    return startPromise;
  }

  /**
   * Every time config as change this method is notified.
   * 
   * @param config as json
   */
  protected void updateLocalConfig(JsonObject json) {
    JsonArray phases = json.getJsonObject("strategy").getJsonArray("phases");

    for (int i = 0; i < phases.size(); i++) {
      JsonObject phase = phases.getJsonObject(i);
      if (phase.containsKey(this.getClass().getName())) {
        JsonObject deploy = phase.getJsonObject(this.getClass().getName());

        if (deploy != null && deploy.containsKey("config")) {
          JsonObject config = deploy.getJsonObject("config", new JsonObject());
          this.config().mergeIn(config);

          this.onConfigChange(phase);
        }
        break;
      }
    }
  }

  /**
   * Handler to on config change.
   * 
   * @param phase
   */
  protected void onConfigChange(JsonObject phase) {
    // Nothing to do
  }

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
      for (AnnotatedType type : clazz.getAnnotatedInterfaces()) {
        for (ComponentRegister model : ComponentRegister.values())
          if (type.getType().getTypeName().equals(model.getName()))
            annotations.add(model);
      }
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
      composite.add(invokeComponent(invokeModel.getName(), invokeModel.getMethod()));

    Promise<Void> promise = Promise.promise();
    composite.all().onComplete(promise);

    vertx.setTimer(config().getLong("timeout", TIMEOUT_VERTICLE_UP),
        res -> promise.tryFail("Timeout on executing models, be sure to complete() all futures/promise verticles"));

    return promise.future();
  }

  @SuppressWarnings("unchecked")
  private Future<Void> invokeComponent(String name, String methodName) {
    try {
      Method method = Class.forName(name).getMethod(methodName, Vertx.class, JsonObject.class);
      Object obj = method.invoke(this, vertx, config());

      return (Future<Void>) obj;
    } catch (ReflectiveOperationException e) {
      return Future.failedFuture(e);
    }
  }
}
