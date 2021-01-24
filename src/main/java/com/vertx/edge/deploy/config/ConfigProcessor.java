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
package com.vertx.edge.deploy.config;

import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.vertx.core.json.JsonObject;
import lombok.extern.log4j.Log4j2;

/**
 * @author Luiz Schmidt
 */
@Log4j2
public final class ConfigProcessor implements UnaryOperator<JsonObject> {

  private static final int FULL_GROUP = 1;
  private static final int OPTIONAL_GROUP = 2;
  private static final int TYPE_GROUP = 3;
  private static final int NAME_GROUP = 4;
  private static final int DEFAULT_GROUP = 6;

  private static final Pattern OPTIONAL = Pattern.compile("\\?");
  private static final Pattern pattern = Pattern
      .compile("(\\\"(!?)(env|intEnv|bolEnv|rawEnv):([a-zA-Z-_0-9]+)(\\?([\\w\\W]([^\\\"]){0,}))?\\\")");

  @Override
  public JsonObject apply(JsonObject config) {
    String text = config.encode();
    Matcher m = pattern.matcher(text);

    boolean error = false;
    while (m.find()) {
      String fullExpr = OPTIONAL.matcher(m.group(FULL_GROUP)).replaceFirst("\\\\?");
      boolean optional = "!".equalsIgnoreCase(m.group(OPTIONAL_GROUP));
      String type = m.group(TYPE_GROUP);
      String nameVariable = m.group(NAME_GROUP);
      String defaultValue = m.group(DEFAULT_GROUP);

      try {
        String value = getValue(nameVariable, defaultValue, optional);
        text = text.replaceAll(fullExpr, transformToType(value, type));
      } catch (IllegalArgumentException e) {
        log.error(e.getMessage(), e);
        error = true;
      }
    }

    if (error) {
      throw new IllegalArgumentException(
          "One or more Environment Variable is required and not informed, please review the logs.");
    }

    return new JsonObject(text);
  }

  private static String transformToType(String value, String type) {
    String transformedValue;
    switch (type) {
      case "env":
        transformedValue = value != null ? ('"' + value + '"') : "\"\"";
        break;
      case "rawEnv":
        transformedValue = value != null ? value : "";
        break;
      case "intEnv":
        transformedValue = value != null ? String.valueOf(Long.parseLong(value)) : "0";
        break;
      case "bolEnv":
        transformedValue = value != null ? value : "false";
        break;
      default:
        throw new IllegalArgumentException("Unexpected value: " + type);
    }
    return transformedValue;
  }

  private static String getValue(String nameVariable, String defaultValue, boolean optional) {
    String value = System.getProperty(nameVariable);
    if (value == null) {
      value = System.getenv(nameVariable);
    }

    if (defaultValue != null && value == null) {
      value = defaultValue;
    }

    if (value == null && !optional) {
      throw new IllegalArgumentException("A property \"" + nameVariable + "\" is not present on Environment Variable.");
    }
    return value;
  }

}
