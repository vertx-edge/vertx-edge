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
public interface LivenessCheckable {

  void checkLiveness(Handler<Status> handler);

  default Future<Void> startLivenessCheck(Vertx vertx, JsonObject vertxConfig) {
    DeliveryOptions setCodecName = new DeliveryOptions().setCodecName(StatusHealthCodec.class.getName());

    String address = "healthcheck.liveness.".concat(this.getClass().getName());
    vertx.eventBus().<String>consumer(address, res -> this.checkLiveness(status -> res.reply(status, setCodecName)));

    Long timeWait = vertxConfig.getJsonObject("liveness", new JsonObject()).getLong("timeWait");
    JsonObject myClass = new JsonObject().put("className", this.getClass().getName()).put("timeWait", timeWait);

    vertx.eventBus().publish("healthcheck.liveness.register", myClass);
    return Future.succeededFuture();
  }
}
