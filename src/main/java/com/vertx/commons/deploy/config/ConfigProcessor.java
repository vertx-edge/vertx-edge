package com.vertx.commons.deploy.config;

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

  private static final Pattern pattern = Pattern
      .compile("(\\\"(!?)(env|intEnv|bolEnv|rawEnv):([a-zA-Z-_0-9]+)(\\?([\\w\\W]([^\\\"]){0,}))?\\\")");

  @Override
  public JsonObject apply(JsonObject config) {
    String text = config.encode();
    Matcher m = pattern.matcher(text);

    boolean error = false;
    while (m.find()) {
      String fullExpr = m.group(1).replaceFirst("\\?", "\\\\?");
      boolean optional = "!".equalsIgnoreCase(m.group(2));
      String type = m.group(3);
      String nameVariable = m.group(4);
      String defaultValue = m.group(6);

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
