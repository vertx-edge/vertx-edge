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

import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author Luiz Schmidt
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FileUtils {

  /**
   * Load lines of small files
   * 
   * @param vertx
   * @param path
   * @return
   */
  public static Future<String> loadFileToString(Vertx vertx, String path) {
    if (path == null || path.isEmpty())
      return Future.failedFuture(new FileNotFoundException("File path is required to be informed."));

    Promise<String> promise = Promise.promise();
    vertx.fileSystem().exists(path).onSuccess(exists -> {
      if (exists.booleanValue()) {
        vertx.fileSystem().readFile(path).onSuccess(file -> promise.complete(file.toString(StandardCharsets.UTF_8)))
            .onFailure(promise::fail);
      } else {
        promise.fail(new FileNotFoundException("File " + path + " not exists in directory."));
      }
    }).onFailure(promise::fail);
    return promise.future();
  }
}
