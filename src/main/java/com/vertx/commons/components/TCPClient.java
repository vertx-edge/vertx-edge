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
package com.vertx.commons.components;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;

/**
 * @author Luiz Schmidt
 */

public interface TCPClient {

  long DEFAULT_RECONNECT_INTERVAL = 5000L;
  Logger log = LoggerFactory.getLogger(TCPClient.class);

  void handlerClient(NetSocket socket);

  void socketError();

  default Future<Void> startTcpClient(Vertx vertx, JsonObject vertxConfig) {
    JsonObject config = vertxConfig.getJsonObject("socket", new JsonObject()).getJsonObject("client");

    if (config == null) {
      return Future.failedFuture("Must be informed a \"socket.client\" on configuration");
    }

    final Integer port = config.getInteger("port", null);
    if (port == null || port <= 0) {
      return Future.failedFuture("The 'port' must be informed.");
    }

    final String hostname = config.getString("hostname");
    if (hostname == null || hostname.isEmpty()) {
      return Future.failedFuture("The 'hostname' must be informed.");
    }

    JsonObject netOptions = config.getJsonObject("options");
    NetClientOptions options = new NetClientOptions();
    if (netOptions != null)
      options = new NetClientOptions(netOptions);

    Long reconnectInterval = config.getLong("reconnectInterval", DEFAULT_RECONNECT_INTERVAL);

    Promise<Void> promise = Promise.promise();
    this.createTCPClient(vertx, promise, config, options, hostname, port, reconnectInterval);

    log.trace("Enabling TCPClient...");
    return promise.future();
  }

  default void createTCPClient(Vertx vertx, Promise<Void> promise, JsonObject config, NetClientOptions options,
      String hostname, Integer port, Long reconnectInterval) {
    boolean optional = config.getBoolean("optional", Boolean.FALSE);

    if (optional)
      promise.complete();

    this.connectTCPClient(vertx, options, hostname, port, reconnectInterval).onSuccess(v -> promise.tryComplete())
        .onFailure(cause -> {
          if (optional) {
            reconnectAttempts(vertx, options, hostname, port, reconnectInterval);
          } else {
            promise.fail(cause);
          }
        });
  }

  default void reconnectAttempts(Vertx vertx, NetClientOptions options, String hostname, Integer port,
      Long reconnectInterval) {
    vertx.periodicStream(reconnectInterval).handler(
        idTimer -> this.connectTCPClient(vertx, options, hostname, port, reconnectInterval).onSuccess(connect -> {
          log.debug("Success connect TCP Client in {} times " + idTimer);
          vertx.cancelTimer(idTimer);
        }).onFailure(cause -> log.debug("Failed TCP Client {} times " + idTimer)));
  }

  default Future<Void> connectTCPClient(Vertx vertx, NetClientOptions options, String hostname, Integer port,
      Long reconnectInterval) {
    Promise<Void> promise = Promise.promise();

    vertx.createNetClient(options).connect(port, hostname).onSuccess(socket -> {
      this.handlerClient(socket);
      log.info("TCPClient opened for: " + hostname + ":" + port);

      socket.closeHandler(close -> {
        this.socketError();
        this.reconnectAttempts(vertx, options, hostname, port, reconnectInterval);
      });

      promise.complete();
    }).onFailure(cause -> {
      log.error("Error connecting to TCPClient with server " + hostname + ":" + port + ", reason:", cause);
      promise.fail(cause);
    });

    return promise.future();
  }
}
