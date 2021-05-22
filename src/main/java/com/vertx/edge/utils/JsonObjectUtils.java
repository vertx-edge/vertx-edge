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

import java.util.Map;
import java.util.Objects;

import io.vertx.core.json.JsonObject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author Luiz Schmidt
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JsonObjectUtils {

  private static final int MAX_SPLITED = 2;

  public static Map<String, Object> flatMap(JsonObject json) {
    return flat(json).getMap();
  }

  public static JsonObject flat(JsonObject json) {
    return transformFlat(json, null);
  }

  private static JsonObject transformFlat(JsonObject json, String father) {
    Objects.requireNonNull(json, "To transform flatJson JSON cannot be null");
    
    JsonObject flatNode = new JsonObject();
    json.getMap().forEach((key, value) -> {
      if (father != null) {
        key = father.concat(".").concat(key);
      }

      if (value instanceof JsonObject) {
        flatNode.getMap().putAll(transformFlat((JsonObject) value, key).getMap());
      } else {
        flatNode.put(key, value);
      }
    });
    return flatNode;
  }

  static JsonObject expandFlatJson(JsonObject flat) {
    JsonObject expanded = new JsonObject();
    flat.getMap().forEach((key, value) -> {
      String[] split = key.split("\\.", MAX_SPLITED);
      if (split.length > 1) {
        expanded.put(split[0], expandFlatJson(expanded.getJsonObject(split[0], new JsonObject()).put(split[1], value)));
      } else {
        expanded.put(key, value);
      }
    });

    return expanded;
  }
}
