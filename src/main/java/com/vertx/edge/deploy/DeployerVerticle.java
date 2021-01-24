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
package com.vertx.edge.deploy;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.stream.Collectors;

import com.vertx.edge.deploy.config.VerticleConfiguration;
import com.vertx.edge.deploy.service.ServiceDiscoveryVerticle;
import com.vertx.edge.utils.CompositeFutureBuilder;
import com.vertx.edge.utils.Timer;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.extern.log4j.Log4j2;

/**
 * @author Luiz Schmidt
 */
@Log4j2
public final class DeployerVerticle extends AbstractVerticle {

  private static final String VERTICLE_WEB_CLIENT = "com.vertx.edge.web.client.verticle.WebClientVerticle";
  private static final String VERTICLE_WEB_SERVER = "com.vertx.edge.web.server.verticle.WebServerVerticle";
  private Deployer deployer;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    Timer timer = Timer.start();
    String threadName = Thread.currentThread().getName();
    Thread.currentThread().setName("deploying");

    StartInfo.print();

    this.deployer = new Deployer(vertx);
    VerticleConfiguration.create(vertx).load().onFailure(startPromise::fail).onSuccess(config -> {
      String registryPackages = config.getString("registryPackages");
      RegisterCodec.registerAll(vertx, registryPackages);

      startDepoy(timer, config, registryPackages).onSuccess(p -> {
        log.info("All Verticles are deployed successful");
        log.info("Elapsed time to deploy: {}", timer);
        log.info("Application started!");
        Thread.currentThread().setName(threadName);
      }).onFailure(startPromise::fail);
    });
  }

  private Future<CompositeFuture> startDepoy(Timer timer, JsonObject config, String registryPackages) {
    Promise<CompositeFuture> promise = Promise.promise();

    JsonObject services = config.getJsonObject("services");
    JsonArray phases = config.getJsonArray("phases");
    JsonObject webServer = config.getJsonObject("web-server");
    JsonObject webClients = config.getJsonObject("web-client");

    CompositeFutureBuilder.create().add(this.deployServices(services, registryPackages))
        .add(this.deployWebClient(webClients)).all()
        .compose(v -> CompositeFutureBuilder.create().add(this.deployWebServer(webServer, registryPackages))
            .add(this.deployPhases(phases)).all().onSuccess(cr -> promise.complete()).onFailure(promise::fail));

    return promise.future();
  }

  private Future<Void> deployServices(JsonObject config, String registryPackages) {
    if (config == null) {
      log.info("The configuration \"services\" was not found, no one service will be discovered.");
      return Future.succeededFuture();
    }

    JsonObject options = new JsonObject().put("config", config).put("base-package", registryPackages);
    return this.deployer.deploy(ServiceDiscoveryVerticle.class.getName(), options);
  }

  private Future<Void> deployWebServer(JsonObject config, String registryPackages) {
    if (config == null) {
      log.info("The configuration \"web-server\" was not found, no one @Controller will inject.");
      return Future.succeededFuture();
    }

    JsonObject options = config.getJsonObject("deployOptions", new JsonObject());
    config.remove("deployOptions");

    try {
      return this.deployer.deploy(Class.forName(VERTICLE_WEB_SERVER).getName(),
          options.put("config", config.put("base-package", registryPackages)));
    } catch (ClassNotFoundException e) {
      return Future.failedFuture("In the configuration file the WebServer field was found, but the package is missing. "
          + "Import the library -> groupId: com.vertx.commons | artifactId: web-server");
    }
  }

  private Future<Void> deployWebClient(JsonObject config) {
    if (config == null) {
      log.info("The configuration \"web-client\" was not found, no one WebClient will be discovered.");
      return Future.succeededFuture();
    }

    JsonObject options = config.getJsonObject("deployOptions", new JsonObject());
    config.remove("deployOptions");

    try {
      return this.deployer.deploy(Class.forName(VERTICLE_WEB_CLIENT).getName(), options.put("config", config));
    } catch (ClassNotFoundException e) {
      return Future.failedFuture("In the configuration file the WebClient field was found, but the package is missing. "
          + "Import the library -> groupId: com.vertx.commons | artifactId: web-client");
    }
  }

  private Future<Void> deployPhases(JsonArray config) {
    if (config == null || config.isEmpty()) {
      log.info("The configuration \"phases\" was not found, no one personalized verticle will be up.");
      return Future.succeededFuture();
    }

    Promise<Void> promise = Promise.promise();
    LinkedList<Phase> phases = config.stream().map(JsonObject.class::cast).map(Phase::new)
        .collect(Collectors.toCollection(LinkedList::new));

    Iterator<Phase> it = phases.iterator();
    this.deployPhase(it, promise);
    return promise.future();
  }

  private void deployPhase(Iterator<Phase> it, Handler<AsyncResult<Void>> handler) {
    if (it.hasNext()) {
      it.next().deploy(vertx).onSuccess(v -> deployPhase(it, handler))
          .onFailure(cause -> handler.handle(Future.failedFuture(cause)));
    } else {
      handler.handle(Future.succeededFuture());
    }
  }
}
