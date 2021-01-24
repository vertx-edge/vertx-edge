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
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * @author Luiz Schmidt
 *
 */
class ManifestUtilTest {

  @Test
  void testShouldInitilizeManifestWithSuccess() {
    assertTrue(ManifestUtil.isLoaded(), "Manifest must be loaded");
    assertTrue(ManifestUtil.read("valueNotExists").isEmpty(), "value not exists must return empty string");
    assertEquals("vertx-junit5", ManifestUtil.read("Maven-Artifact-Id"), "value exists must return value");
  }
}
