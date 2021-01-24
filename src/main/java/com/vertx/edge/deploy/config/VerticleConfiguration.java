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
package com.vertx.edge.deploy.config;

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
