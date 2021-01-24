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
package com.vertx.edge.codec;

import com.vertx.edge.annotations.EventBusCodec;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.ext.healthchecks.Status;

/**
 * @author Luiz Schmidt
 */
@EventBusCodec
public class StatusHealthCodec implements MessageCodec<Status, Status> {

  @Override
  public void encodeToWire(Buffer buffer, Status s) {
    // Nothing to do
  }

  @Override
  public Status decodeFromWire(int pos, Buffer buffer) {
    return null;
  }

  @Override
  public Status transform(Status s) {
    return s;
  }

  @Override
  public String name() {
    return StatusHealthCodec.class.getName();
  }

  @Override
  public byte systemCodecID() {
    return -1;
  }

}
