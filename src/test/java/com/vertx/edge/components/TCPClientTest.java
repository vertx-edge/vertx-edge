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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.ServerSocket;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetSocket;
import io.vertx.core.streams.Pump;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

/**
 * @author Luiz Schmidt
 *
 */
@ExtendWith(VertxExtension.class)
class TCPClientTest {

  @Test
  void testStartTCPServer(Vertx vertx, VertxTestContext context) throws IOException {
    Checkpoint checkpoint = context.checkpoint();
    Checkpoint startTcp = context.checkpoint();
    Checkpoint finish = context.checkpoint();

    TCPClientImpl tcpClient = new TCPClientImpl();
    ServerSocket socket = new ServerSocket(0);
    int port = socket.getLocalPort();
    socket.close();

    vertx.createNetServer().connectHandler(sock -> {
      Pump.pump(sock, sock).start();
      sock.handler(res -> {
        context.verify(() -> {
          assertEquals("bye", res.toString(), "bye must be a string received");
        });
        finish.flag();
      });
    }).listen(port).onComplete(context.succeeding(v -> checkpoint.flag()));

    JsonObject config = new JsonObject().put("socket", new JsonObject().put("client", new JsonObject().put("hostname", "localhost").put("port", port)));
    tcpClient.startTcpClient(vertx, config).onComplete(context.succeeding(res -> startTcp.flag()));
  }

  private class TCPClientImpl implements TCPClient {
    @Override
    public void handlerClient(NetSocket socket) {
      socket.write("bye");
    }

    @Override
    public void socketError() {

    }
  }
}
