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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.reflections.Reflections;

import com.vertx.edge.deploy.DeployerVerticle;
import com.vertx.edge.deploy.injection.Injectable;
import com.vertx.edge.deploy.injection.annotation.Singleton;
import com.vertx.edge.deploy.injection.type.InjectionType;
import com.vertx.edge.utils.CompositeFutureBuilder;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;

/**
 * @author Luiz Schmidt
 */
public class ServiceInjectionVerticle extends BaseVerticle {

  public static final String SERVICE_FACTORY = "service-factory-";

  @Override
  protected void up(Promise<Void> promise) {
    if(!config().containsKey(DeployerVerticle.BASE_PACKAGE)) {
      promise.fail("The deploy strategy must contains the cofig: "+ DeployerVerticle.BASE_PACKAGE);
    }
    
    Reflections reflections = new Reflections(config().getString(DeployerVerticle.BASE_PACKAGE));
    Set<Class<?>> list = new HashSet<>();
    list.addAll(reflections.getTypesAnnotatedWith(Singleton.class));

    CompositeFutureBuilder composite = CompositeFutureBuilder.create();

    list.stream().filter(service -> (service.getSuperclass().equals(Injectable.class)))
        .map(this::publish).forEach(composite::add);

    composite.all().onComplete(promise);
  }

  private Future<Record> publish(Class<?> service) {
    String interfaceName = Arrays.stream(service.getInterfaces()).findFirst().orElse(service).getName();
    String name = SERVICE_FACTORY + interfaceName;

    Record record = InjectionType.createRecord(name, config().getJsonObject(interfaceName, new JsonObject()));
    return this.discovery.publish(record);
  }
}
