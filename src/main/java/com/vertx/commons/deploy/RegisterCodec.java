/*
 * Copyright (c) 2020-2021 Contributors
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vertx.commons.deploy;

import java.util.Objects;
import java.util.Set;

import org.reflections.Reflections;

import com.vertx.commons.annotations.EventBusCodec;
import com.vertx.commons.deploy.config.VerticleConfiguration;

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

    Reflections reflections = new Reflections(VerticleConfiguration.BASE_PACKAGE_COMMONS, registryPackages);
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
