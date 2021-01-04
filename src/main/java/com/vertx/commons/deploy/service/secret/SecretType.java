package com.vertx.commons.deploy.service.secret;

import com.vertx.commons.deploy.service.secret.strategy.BasicAuthStrategy;
import com.vertx.commons.deploy.service.secret.strategy.SecretStrategy;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * @author Luiz Schmidt
 */
public enum SecretType {
  BASIC_AUTH {
    @Override
    public Future<JsonObject> getUserAndPassword(Vertx vertx, JsonObject config) {
      return BasicAuthStrategy.resolveSecret(config);
    }
  },
  SECRET_FILE {
    @Override
    public Future<JsonObject> getUserAndPassword(Vertx vertx, JsonObject config) {
      return SecretStrategy.resolveSecret(vertx, config);
    }
  },
  NO_AUTH {
    @Override
    public Future<JsonObject> getUserAndPassword(Vertx vertx, JsonObject config) {
      return Future.succeededFuture(new JsonObject());
    }
  };

  public abstract Future<JsonObject> getUserAndPassword(Vertx vertx, JsonObject config);
}
