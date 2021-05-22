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
package com.vertx.edge.deploy;

import java.util.Objects;
import java.util.Set;

import org.reflections.Reflections;

import com.vertx.edge.annotations.EventBusCodec;
import com.vertx.edge.deploy.config.ConfigurationStrategy;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.MessageCodec;
import lombok.extern.log4j.Log4j2;

/**
 * @author Luiz Schmidt
 */
@Log4j2
public final class RegisterCodec {

  private RegisterCodec() {
    // Nothing to do
  }

  public static void registerAll(Vertx vertx, String registryPackages) {
    Objects.requireNonNull(registryPackages, "Missing configuration of 'RegistryPackages'");

    Reflections reflections = new Reflections(ConfigurationStrategy.BASE_PACKAGE_EDGE, registryPackages);
    Set<Class<?>> annotations = reflections.getTypesAnnotatedWith(EventBusCodec.class);

    for (Class<?> clazz : annotations) {
      if (MessageCodec.class.isAssignableFrom(clazz)) {
        try {
          vertx.eventBus().registerCodec((MessageCodec<?, ?>) clazz.getDeclaredConstructor().newInstance());
        } catch (ReflectiveOperationException e) {
          log.warn("The codec {} cannot be registered, please verify: ", e);
        }
      } else {
        log.warn("The codec {} not implements interface MessageCodec, ignoring this Codec.", clazz.getName());
      }

    }
  }

}
