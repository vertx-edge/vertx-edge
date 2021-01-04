package com.vertx.commons.deploy.service;

import java.util.HashSet;
import java.util.Set;

import org.reflections.Reflections;

import com.vertx.commons.annotations.ServiceProvider;
import com.vertx.commons.deploy.config.VerticleConfiguration;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import lombok.extern.log4j.Log4j2;

/**
 * @author Luiz Schmidt
 */
@Log4j2
public class ServiceProviderFactory {

  
  private Set<Class<?>> list = new HashSet<>();

  public ServiceProviderFactory(String basePackage) {
    Reflections reflections = new Reflections(VerticleConfiguration.BASE_PACKAGE_COMMONS, basePackage);
    Set<Class<?>> annotations = reflections.getTypesAnnotatedWith(ServiceProvider.class);

    for (Class<?> clazz : annotations)
      list.add(clazz);
  }

  public Future<Record> newInstance(Vertx vertx, String key, JsonObject config) {
    for (Class<?> clazz : list) {
      if (key.equalsIgnoreCase(clazz.getAnnotation(ServiceProvider.class).name())) {
        try {
          return ((RecordService) clazz.getDeclaredConstructor().newInstance()).newRecord(vertx, config);
        } catch (ReflectiveOperationException e) {
          log.error("Error on instance {} reason -> ", clazz, e);
          return Future.failedFuture("Error on instance: " + clazz + " reason -> " + e.getMessage());
        }
      }
    }
    return Future.failedFuture("No one of " + key
        + " was registered in the ServiceProviderFactory. Please annotate with @ServiceProvider the class");
  }
}
