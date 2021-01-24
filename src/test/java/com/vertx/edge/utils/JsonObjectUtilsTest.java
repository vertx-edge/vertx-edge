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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * @author Luiz Schmidt
 *
 */
class JsonObjectUtilsTest {

  @Test
  void testShouldFlatAndExpandJson() {
    JsonObject json = new JsonObject().put("foo", new JsonObject()
        .put("bar", "foobar")
        .put("baz", new JsonObject()
            .put("foo", "foobazfoo")
            .put("firstList", new JsonArray()
                .add("item")
                .add(new JsonArray().add("one").add("two").add("three"))
                .add(new JsonObject().put("foo", new JsonObject().put("bar", "foobar"))))));

    JsonObject flatJson = JsonObjectUtils.flat(json);
    assertFalse(flatJson.containsKey("foo"), "not contain root foo");
    assertEquals("foobar", flatJson.getString("foo.bar"), "match foobar");
    assertEquals("foobazfoo", flatJson.getString("foo.baz.foo"), "match foobazfoo");
    
    JsonObject expandedJson = JsonObjectUtils.expandFlatJson(flatJson);
    assertTrue(expandedJson.containsKey("foo"), "contain root foo");
    assertEquals("foobar", expandedJson.getJsonObject("foo").getString("bar"), "match foobar");
    assertEquals("foobazfoo", expandedJson.getJsonObject("foo").getJsonObject("baz").getString("foo"),
        "match foobazfoo");

    assertEquals(json, expandedJson, "the same again");
  }
  
  @Test
  void testShouldExpandSimpleFlatJson() {
    JsonObject json = new JsonObject().put("foo.bar", "foobar");
    JsonObject expanded = JsonObjectUtils.expandFlatJson(json);
    assertTrue(expanded.containsKey("foo"), "contain root foo");
    assertEquals("foobar", expanded.getJsonObject("foo").getString("bar"), "match foobar");
    assertFalse(JsonObjectUtils.flatMap(expanded).isEmpty(), "when transform into map must be not empty too");
  }
}
