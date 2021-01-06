package com.vertx.commons.deploy;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.stream.Collectors;

import com.vertx.commons.deploy.config.VerticleConfiguration;
import com.vertx.commons.deploy.service.ServiceDiscoveryVerticle;
import com.vertx.commons.utils.Timer;
import com.vertx.commons.verticle.RestServerVerticle;

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

  private static final String VERTICLE_HTTP_CLIENT = "com.vertx.commons.http.client.verticle.WebClientVerticle";
  private Deployer deployer;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    String threadName = Thread.currentThread().getName();
    Thread.currentThread().setName("deploying");
    Timer timer = Timer.start();
    StartInfo.print();

    VerticleConfiguration.create(vertx).load().onFailure(startPromise::fail).onSuccess(config -> {
      String registryPackages = config.getString("registryPackages");
      RegisterCodec.registerAll(vertx, registryPackages);

      this.deployer = new Deployer(vertx);

      JsonObject services = config.getJsonObject("services");
      JsonArray phases = config.getJsonArray("phases");
      JsonObject webServer = config.getJsonObject("web-server");
      JsonObject webClients = config.getJsonObject("web-client");

      this.deployServices(services, registryPackages).onSuccess(v -> {
        CompositeFuture.all(deployRestService(webServer), deployRestClient(webClients)).onSuccess(c -> {
          this.deployPhases(phases).onSuccess(p -> {
            log.info("All Verticles are deployed successful");
            log.info("Elapsed time to deploy: {}", timer);
            log.info("Application started!");
            Thread.currentThread().setName(threadName);
          }).onFailure(startPromise::fail);
        }).onFailure(startPromise::fail);
      }).onFailure(startPromise::fail);

    });
  }

  private Future<Void> deployServices(JsonObject config, String registryPackages) {
    if (config == null || config.isEmpty()) {
      log.info("The configuration \"services\" was not found, no one service will be discovered.");
      return Future.succeededFuture();
    }

    JsonObject options = new JsonObject().put("config", config).put("base-package", registryPackages);
    return this.deployer.deploy(ServiceDiscoveryVerticle.class.getName(), options);
  }

  private Future<Void> deployRestService(JsonObject config) {
    if (config == null || config.isEmpty()) {
      log.info("The configuration \"web-server\" was not found, no one @Controller will inject.");
      return Future.succeededFuture();
    }

    return this.deployer.deploy(config.getString("core", RestServerVerticle.class.getName()), config);
  }

  private Future<Void> deployRestClient(JsonObject config) {
    if (config == null || config.isEmpty()) {
      log.info("The configuration \"web-client\" was not found, no one WebClient will be discovered.");
      return Future.succeededFuture();
    }
    
    JsonObject options = config.getJsonObject("options", new JsonObject());
    JsonObject clients = config.getJsonObject("clients");

    try {
      return this.deployer.deploy(Class.forName(VERTICLE_HTTP_CLIENT).getName(),
          options.put("config", clients));
    } catch (ClassNotFoundException e) {
      return Future.failedFuture("In the configuration file the WebClient field was found, but the package is missing. "
          + "Import the library -> groupId: com.vertx.commons | artifactId: http-client");
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
      it.next().deploy(vertx).onSuccess(v -> deployPhase(it, handler)).onFailure(cause -> handler.handle(Future.failedFuture(cause)));
    } else {
      handler.handle(Future.succeededFuture());
    }
  }
}
