package com.vertx.commons.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import io.vertx.core.json.JsonObject;

/**
 * @author Luiz Schmidt
 */
public final class JsonObjectUtils {

  private static final int MAX_SPLITED = 2;

  private JsonObjectUtils() {
    // Nothing to do
  }

  public static JsonObject transformIntoFlatJsonObject(JsonObject json) {
    return new JsonObject(transformIntoFlatMap(json));
  }

  public static Map<String, Object> transformIntoFlatMap(JsonObject json) {
    return transformIntoFlatMap(json, null);
  }

  private static Map<String, Object> transformIntoFlatMap(JsonObject json, String father) {
    Map<String, Object> map = new HashMap<>();
    for (Entry<String, Object> entry : json) {
      String key = entry.getKey();
      if (father != null)
        key = father.concat(".").concat(key);

      if (entry.getValue() instanceof JsonObject) {
        map.putAll(transformIntoFlatMap((JsonObject) entry.getValue(), key));
      } else {
        map.put(key, entry.getValue());
      }
    }
    return map;
  }

  public static JsonObject expandFlatJson(JsonObject flat) {
    JsonObject expanded = new JsonObject();
    for (Entry<String, Object> field : flat) {
      String[] split = field.getKey().split("\\.", MAX_SPLITED);
      if (split.length > 1) {
        expanded.put(split[0],
            expandFlatJson(expanded.getJsonObject(split[0], new JsonObject()).put(split[1], field.getValue())));
      } else {
        expanded.put(field.getKey(), field.getValue());
      }
    }

    return expanded;
  }

  public static String getRequiredString(JsonObject transaction, String field) {
    Object value = transaction.getValue(field);
    Objects.requireNonNull(value, "The field '" + field + "' must be informed for aggregation.");

    String text;
    if (value instanceof Double) {
      text = String.valueOf(transaction.getDouble(field));
    } else if (value instanceof Long) {
      text = String.valueOf(transaction.getLong(field));
    } else if (value instanceof Integer) {
      text = String.valueOf(transaction.getInteger(field));
    } else if (value instanceof String) {
      text = transaction.getString(field);
    } else {
      throw new IllegalArgumentException("The field '" + field
          + "' must be a String, Double, Integer or Long value. received: " + value.getClass().getSimpleName());
    }
    return text;
  }
}
