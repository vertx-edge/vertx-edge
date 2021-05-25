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
package com.vertx.edge.components;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
import io.vertx.core.parsetools.RecordParser;
import io.vertx.core.streams.Pump;

/**
 * @author Luiz Schmidt
 */
public interface TCPServer {

  Logger log = LoggerFactory.getLogger(TCPServer.class);

  void handlerMessage(String message);

  default Future<NetServer> startTcpServer(Vertx vertx, JsonObject vertxConfig) {
    JsonObject config = vertxConfig.getJsonObject("socket", new JsonObject()).getJsonObject("server");

    if (config == null)
      return Future.failedFuture("Must be informed a \"socket.server\" on configuration");

    Integer port = config.getInteger("port", null);

    if (port == null || port <= 0)
      return Future.failedFuture("The port must be informed.");

    Promise<NetServer> promise = Promise.promise();

    NetServerOptions options = new NetServerOptions();
    JsonObject netOptions = config.getJsonObject("options");
    if (netOptions != null)
      options = new NetServerOptions(netOptions);

    this.createNetServer(vertx, promise, port, this.chooseParser(config), options);

    log.debug("Enabling TCPServer...");
    return promise.future();
  }

  default void createNetServer(Vertx vertx, Promise<NetServer> promise, Integer port, RecordParser parser,
      NetServerOptions options) {
    vertx.createNetServer(options).connectHandler(sock -> {
      Pump.pump(sock, sock).start();
      sock.handler(parser::handle);
    }).listen(port).onSuccess(res -> {
      log.info("TCPServer opened for: "+ port);
      promise.complete();
    }).onFailure(cause -> {
      log.error("Error opening TCPServer on port " + port + ", reason: ", cause);
      promise.fail(cause);
    });

  }

  default RecordParser chooseParser(JsonObject config) {
    String encoding = config.getString("encoding");
    if (encoding == null || encoding.isEmpty())
      throw new IllegalArgumentException("The 'encoding' need to be informed.");

    if (config.containsKey("delimited")) {
      String delimiter = config.getJsonObject("delimited").getString("character");

      if (delimiter == null || delimiter.isEmpty())
        throw new IllegalArgumentException("The 'character' need to be informed.");

      return RecordParser.newDelimited(delimiter, res -> handlerMessage(res.toString(encoding)));
    } else if (config.containsKey("fixed")) {
      Integer length = config.getJsonObject("fixed").getInteger("length");

      if (length == null || length <= 0)
        throw new IllegalArgumentException("The length can not be null and greater than 0.");

      return RecordParser.newFixed(length, res -> handlerMessage(res.toString(encoding)));
    } else {
      throw new IllegalArgumentException("Must be informed whether 'delimited' or 'fixed' in json properties.");
    }
  }
}
