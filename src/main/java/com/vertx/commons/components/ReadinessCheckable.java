package com.vertx.commons.components;

import com.vertx.commons.codec.StatusHealthCodec;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.healthchecks.Status;

/**
 * @author Luiz Schmidt
 */
public interface ReadinessCheckable {

  default Future<Void> startReadinessCheck(Vertx vertx, JsonObject vertxConfig) {
    DeliveryOptions opts = new DeliveryOptions().setCodecName(StatusHealthCodec.class.getName());
    String address = "healthcheck.readiness.".concat(this.getClass().getName());

    vertx.eventBus().<String>consumer(address, res -> checkReadiness(read -> res.reply(read, opts)));

    Long timeWait = vertxConfig.getJsonObject("readiness", new JsonObject()).getLong("timeWait");
    JsonObject myClass = new JsonObject().put("className", this.getClass().getName()).put("timeWait", timeWait);

    vertx.eventBus().publish("healthcheck.readiness.register", myClass);
    return Future.succeededFuture();
  }

  void checkReadiness(Handler<Status> handler);
}