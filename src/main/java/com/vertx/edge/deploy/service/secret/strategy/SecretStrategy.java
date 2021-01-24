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
package com.vertx.edge.deploy.service.secret.strategy;

import java.security.GeneralSecurityException;

import com.vertx.edge.utils.Crypto;
import com.vertx.edge.utils.FileUtils;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import lombok.extern.log4j.Log4j2;

/**
 * @author Luiz Schmidt
 */
@Log4j2
public final class SecretStrategy {

  private static final int AES_LENGHT = 16;
  private static final String USER = "user";
  private static final String PASSWORD = "password";

  private SecretStrategy() {
    // Nothing to do
  }

  public static Future<JsonObject> resolveSecret(Vertx vertx, JsonObject config) {
    JsonObject secretFile = config.getJsonObject("secret-file");

    String userPath = secretFile.getString(USER);
    String passPath = secretFile.getString(PASSWORD);
    JsonObject cryptoConfig = config.getJsonObject("crypto");

    Promise<JsonObject> promise = Promise.promise();
    if (userPath == null || userPath.isEmpty()) {
      promise.fail("PATH username cannot be null");
    } else if (passPath == null || passPath.isEmpty()) {
      promise.fail("PATH password cannot be null");
    } else {
      Future<String> loadUser = FileUtils.loadFileToString(vertx, userPath);
      Future<String> loadPass = FileUtils.loadFileToString(vertx, passPath);

      CompositeFuture.all(loadUser, loadPass).onSuccess(result -> {
        if (cryptoConfig != null) {
          getKey(vertx, cryptoConfig)
              .onSuccess(key -> decrypt(key, loadUser.result(), loadPass.result()).onComplete(promise));
        } else {
          promise.complete(new JsonObject().put("username", loadUser.result()).put(PASSWORD, loadPass.result()));
        }
      }).onFailure(promise::fail);
    }
    return promise.future();
  }

  private static Future<JsonObject> decrypt(String key, String userEncrypt, String passEncrypt) {
    Promise<JsonObject> promise = Promise.promise();
    try {
      Crypto crypto = new Crypto(key, AES_LENGHT, "AES");
      String user = crypto.decrypt(userEncrypt);
      String pass = crypto.decrypt(passEncrypt);
      promise.complete(new JsonObject().put("username", user).put(PASSWORD, pass));
    } catch (GeneralSecurityException e) {
      log.error("Cannot decrypt username or password, reason:", e);
      promise.fail("Cannot decrypt username or password, reason: " + e.getMessage());
    }
    return promise.future();
  }

  /**
   * Get the value of key to open Crypto AES
   * 
   * @param vertx
   * @param config
   * @return
   */
  private static Future<String> getKey(Vertx vertx, JsonObject config) {
    Promise<String> promise = Promise.promise();
    if (config.containsKey("file")) {
      FileUtils.loadFileToString(vertx, config.getString("file")).onComplete(promise);
    } else if (config.containsKey("key")) {
      promise.complete(config.getString("key"));
    } else {
      promise.fail("Please enter mode of crypto key (eg. file, key)");
    }
    return promise.future();
  }
}
