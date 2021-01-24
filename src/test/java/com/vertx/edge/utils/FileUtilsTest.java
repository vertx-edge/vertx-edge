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
package com.vertx.edge.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

/**
 * @author Luiz Schmidt
 *
 */
@ExtendWith(VertxExtension.class)
class FileUtilsTest {

  @Test
  void testShouldReadAFile(Vertx vertx, VertxTestContext context) {
    FileUtils.loadFileToString(vertx, "file.txt").onComplete(context.succeeding(file -> {
      context.verify(() -> {
        assertEquals("Hello Foo!\nBar?", file, "content of file must be equals");
      });
      context.completeNow();
    }));
  }
  
  @Test
  void testShouldFailBecauseFileNotExists(Vertx vertx, VertxTestContext context) {
    FileUtils.loadFileToString(vertx, "fileNotExists.txt").onComplete(context.failing(file -> context.completeNow()));
  }
  
  @Test
  void testShouldFailBecausePathIsNull(Vertx vertx, VertxTestContext context) {
    FileUtils.loadFileToString(vertx, null).onComplete(context.failing(file -> context.completeNow()));
  }
  
  @Test
  void testShouldFailBecausePathIsEmpty(Vertx vertx, VertxTestContext context) {
    FileUtils.loadFileToString(vertx, "").onComplete(context.failing(file -> context.completeNow()));
  }
}
