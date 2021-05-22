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

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

/**
 * 
 * @author Luiz Schmidt
 *
 */
public interface ConfigurationStrategy {

  String BASE_PACKAGE_EDGE = "com.vertx.edge";
  
  Future<JsonObject> load();

}
