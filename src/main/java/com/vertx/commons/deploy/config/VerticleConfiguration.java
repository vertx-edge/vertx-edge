/*
 * Copyright (c) 2020-2021 Contributors
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
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
