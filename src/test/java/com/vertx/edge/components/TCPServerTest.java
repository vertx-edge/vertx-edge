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
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

/**
 * @author Luiz Schmidt
 *
 */
@ExtendWith(VertxExtension.class)
class TCPServerTest {

  @Test
  void testStartTCPServer(Vertx vertx, VertxTestContext context) throws IOException {
    TCPServerImpl tcpServer = new TCPServerImpl(vertx);
    ServerSocket socket = new ServerSocket(0);
    int port = socket.getLocalPort();
    socket.close();
    
    vertx.eventBus().<String>consumer("RECEIVE_MESSAGE", message -> {
      context.verify(() -> assertEquals("hello", message.body(), "message equals"));
      context.completeNow();
    });
    
    JsonObject delimiter = new JsonObject().put("character", "\n");
    JsonObject server = new JsonObject().put("port", port).put("encoding", "UTF-8").put("delimited", delimiter );
    JsonObject config = new JsonObject().put("socket", new JsonObject().put("server", server));
    tcpServer.startTcpServer(vertx, config).onComplete(context.succeeding(res -> {
      vertx.createNetClient().connect(port, "localhost").onComplete(context.succeeding(connected -> {
        connected.write("hello\n");
      }));
    }));
  }
  
  private class TCPServerImpl implements TCPServer {
    private Vertx vertx;

    public TCPServerImpl(Vertx vertx) {
      this.vertx = vertx;
    }
    
    @Override
    public void handlerMessage(String message) {
      vertx.eventBus().send("RECEIVE_MESSAGE", message);
    }
  }
}
