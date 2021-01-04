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
          MessageCodec<?, ?> codec = (MessageCodec<?, ?>) Class.forName(clazz.getName()).getDeclaredConstructor()
              .newInstance();
          vertx.eventBus().registerCodec(codec);
        } catch (ReflectiveOperationException e) {
          log.warn("The codec {} cannot be registered, please verify: ", e);
        }
      } else {
        log.warn("The codec {} not implements interface MessageCodec, ignoring this Codec.", clazz.getName());
      }

    }
  }

}
