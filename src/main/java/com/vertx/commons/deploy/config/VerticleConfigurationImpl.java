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

import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.io.FilenameUtils;

import io.vertx.config.ConfigChange;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import lombok.extern.log4j.Log4j2;

/**
 * 
 * @author Luiz Schmidt
 *
 */
@Log4j2
public class VerticleConfigurationImpl implements VerticleConfiguration {

  private static final String EVENT_CONFIG_CHANGE = "configuration.store";

  private static final String MESSAGE_FILE_NOT_FOUND = "Missing deploy strategy. You can set by environment variable "
      + ENV_CONFIG_PARAM + " or create file: \"src/main/resources/" + CONFIG_NAME + "\"";

  private Vertx vertx;
  private String fileName;
  
  private AtomicLong version = new AtomicLong();

  public VerticleConfigurationImpl(Vertx vertx) {
    this.vertx = vertx;
    this.fileName = loadFileName();
  }

  @Override
  public Future<JsonObject> load() {
    Promise<JsonObject> promise = Promise.promise();
    vertx.fileSystem().exists(this.fileName)
        .onFailure(cause -> promise.fail(MESSAGE_FILE_NOT_FOUND))
        .onSuccess(exists -> {
          ConfigStoreOptions store = new ConfigStoreOptions().setType("file")
              .setFormat(FilenameUtils.getExtension(CONFIG_NAME))
              .setConfig(new JsonObject().put("path", fileName));

          ConfigurationLoader.create(vertx, store)
            .setProcessor(new ConfigProcessor()).onChange(this::onChange)
            .load().onSuccess(config -> {
              version.getAndIncrement();
              promise.complete(config);
            }).onFailure(promise::fail);
        });
    return promise.future();
  }

  private void onChange(ConfigChange config) {
    String threadName = Thread.currentThread().getName();
    Thread.currentThread().setName("configuration");
    log.trace("[Reload configuration] before -> {}", config.getPreviousConfiguration().encodePrettily());
    log.trace("[Reload configuration] after -> {}", config.getNewConfiguration().encodePrettily());
    log.info("Configuration change... v" + version.incrementAndGet());
    vertx.eventBus().publish(EVENT_CONFIG_CHANGE, config.getNewConfiguration());
    Thread.currentThread().setName(threadName);
  }

  /**
   * Read environment searching for
   * {@value VerticleConfiguration#ENV_CONFIG_PARAM} variable
   * 
   * @return file name or default {@value VerticleConfiguration#CONFIG_NAME}
   */
  private static String loadFileName() {
    String deployConfig = System.getProperty(ENV_CONFIG_PARAM, null);
    if (deployConfig == null || deployConfig.isEmpty()) {
      deployConfig = System.getenv(ENV_CONFIG_PARAM);

      if (deployConfig == null || deployConfig.isEmpty()) {
        deployConfig = CONFIG_NAME;
      }
    }
    return deployConfig;
  }
}
