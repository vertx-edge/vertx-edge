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
    JsonObject services = config().getJsonObject("services");
    this.serviceFactory = new ServiceProviderFactory(config().getString("base-package"));
    this.registerServices(services).onComplete(promise);
  }

  private Future<Void> registerServices(JsonObject services) {
    CompositeFutureBuilder composite = CompositeFutureBuilder.create();
    for (String key : services.fieldNames()) {
      String serviceName = key.toLowerCase(Locale.ENGLISH);
      JsonObject serviceConfig = services.getJsonObject(key, new JsonObject());
      composite.add(registerService(serviceName, serviceConfig).future());
    }

    return composite.all();
  }

  private Promise<Void> registerService(String key, JsonObject serviceConfig) {
    Promise<Void> promise = Promise.promise();
    serviceFactory.newInstance(vertx, key, serviceConfig).compose(record -> publish(key, record)).onComplete(promise);
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
