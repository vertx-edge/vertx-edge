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

import java.util.function.UnaryOperator;

import io.vertx.config.ConfigChange;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * @author Luiz Schmidt
 */
public final class ConfigurationLoader {

  private static final int DEFAULT_SCAN_PERIOD = 1000;
  private ConfigRetriever configuration;

  /**
   * Do not add ENV store.
   * 
   * @param vertx
   * @param stores
   */
  private ConfigurationLoader(Vertx vertx, ConfigStoreOptions... stores) {
    this(vertx, DEFAULT_SCAN_PERIOD, stores);
  }

  /**
   * Constructor default of Configuration Retreiver
   * 
   * @param vertx
   * @param storeType
   * @param storeConfig
   * @param scanPeriod
   */
  private ConfigurationLoader(Vertx vertx, int scanPeriod, ConfigStoreOptions... stores) {
    ConfigRetrieverOptions options = new ConfigRetrieverOptions().setScanPeriod(scanPeriod);

    for (ConfigStoreOptions store : stores)
      options.addStore(store);

    configuration = ConfigRetriever.create(vertx, options);
  }

  /**
   * Create a instance of ConfigurationLoader
   * @param vertx
   * @param store
   * @return
   */
  public static ConfigurationLoader create(Vertx vertx, ConfigStoreOptions store) {
    return new ConfigurationLoader(vertx, store);
  }
  
  /**
   * Set function to prepare config JsonObject
   * @param function
   * @return
   */
  public ConfigurationLoader setProcessor(UnaryOperator<JsonObject> function) {
    configuration.setConfigurationProcessor(function);
    return this;
  }

  /**
   * Handler for any changes on configuration
   * @param handler
   * @return
   */
  public ConfigurationLoader onChange(Handler<ConfigChange> handler) {
    configuration.listen(handler);
    return this;
  }

  /**
   * Get the main configuration 'strategy'
   * @return
   */
  public Future<JsonObject> load() {
    Promise<JsonObject> promise = Promise.promise();

    configuration.getConfig().onFailure(cause -> promise.fail("fail on get configuration: " + cause))
        .onSuccess(config -> {
          JsonObject strategy = config.getJsonObject("strategy");

          if (strategy != null) {
            promise.complete(strategy);
          } else {
            promise.fail("Not found 'strategy' on the configuration file");
          }
        });
    return promise.future();
  }
}
