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
package com.vertx.edge.deploy.service;

import java.util.Locale;

import com.vertx.edge.utils.CompositeFutureBuilder;
import com.vertx.edge.verticle.BaseVerticle;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import lombok.extern.log4j.Log4j2;

/**
 * @author Luiz Schmidt
 */
@Log4j2
public class ServiceDiscoveryVerticle extends BaseVerticle {

  private ServiceProviderFactory serviceFactory;

  @Override
  protected void up(Promise<Void> promise) {
    this.serviceFactory = new ServiceProviderFactory(config().getString("base-package"));
    this.registerServices().onComplete(promise);
  }

  private Future<Void> registerServices() {
    CompositeFutureBuilder composite = CompositeFutureBuilder.create();
    for (String key : config().fieldNames()) {
      composite
          .add(registerService(vertx, config().getJsonObject(key.toLowerCase(Locale.ENGLISH), new JsonObject()), key)
              .future());
    }

    return composite.all();
  }

  private Promise<Void> registerService(Vertx vertx, JsonObject serviceConfig, String key) {
    Promise<Void> promise = Promise.promise();

    serviceFactory.newInstance(vertx, key, serviceConfig)
      .onSuccess(record -> publish(key, record).onComplete(promise))
      .onFailure(cause -> promise.fail("Error on creating instance of service '" + key + "' -> reason: " + cause));

    return promise;
  }

  private Future<Void> publish(String key, Record record) {
    Promise<Record> promiseRecord = Promise.promise();
    discovery.publish(record, promiseRecord);

    Promise<Void> promise = Promise.promise();
    promiseRecord.future().onSuccess(published -> {
      Thread.currentThread().setName("service-registration");
      log.info("'" + key + "' published");
      promise.complete();
    }).onFailure(cause -> {
      log.error("'" + key + "' failed to publish", cause);
      promise.fail("Error on publishing instance of service '" + key + "' -> reason: " + cause);
    });
    return promise.future();
  }
}
