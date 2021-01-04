package com.vertx.commons.deploy.config;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * 
 * @author Luiz Schmidt
 *
 */
public interface VerticleConfiguration {

  String ENV_CONFIG_PARAM = "DEPLOY_CONFIG";
  String CONFIG_NAME = "deploy-strategy.yaml";
  String BASE_PACKAGE_COMMONS = "com.vertx.commons";

  static VerticleConfiguration create(Vertx vertx) {
    return new VerticleConfigurationImpl(vertx);
  }

  Future<JsonObject> load();
}
