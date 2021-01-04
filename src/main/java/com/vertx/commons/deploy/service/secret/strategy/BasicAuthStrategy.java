package com.vertx.commons.deploy.service.secret.strategy;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

/**
 * @author Luiz Schmidt
 */
public final class BasicAuthStrategy {

  private BasicAuthStrategy() {
    //Nothing to do
  }
  
  public static Future<JsonObject> resolveSecret(JsonObject config) {
    JsonObject basicAuth = config.getJsonObject("basic-auth");

    String username = basicAuth.getString("username");
    String password = basicAuth.getString("password");

    if (username == null || username.isEmpty()) {
      return Future.failedFuture("username cannot be null");
    } else if (password == null || password.isEmpty()) {
      return Future.failedFuture("password cannot be null");
    } else {
      return Future.succeededFuture(new JsonObject().put("username", username).put("password", password));
    }
  }
}
