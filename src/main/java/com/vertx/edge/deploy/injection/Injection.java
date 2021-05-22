package com.vertx.edge.deploy.injection;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.vertx.edge.deploy.injection.annotation.Inject;
import com.vertx.edge.utils.CompositeFutureBuilder;
import com.vertx.edge.verticle.ServiceInjectionVerticle;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;

public final class Injection {

  private ServiceDiscovery discovery;
  private Vertx vertx;
  private Object instance;

  private Injection(ServiceDiscovery discovery, Vertx vertx, Object instance) {
    this.discovery = discovery;
    this.vertx = vertx;
    this.instance = instance;
  }

  public static Injection create(Vertx vertx, Object instance) {
    return new Injection(ServiceDiscovery.create(vertx), vertx, instance);
  }

  public Future<Void> inject() {
    Field[] fields = instance.getClass().getDeclaredFields();
    List<Field> listFields = Arrays.stream(fields).filter(f -> f.getAnnotation(Inject.class) != null)
        .collect(Collectors.toList());

    if (listFields.isEmpty()) {
      return Future.succeededFuture();
    }

    CompositeFutureBuilder composite = CompositeFutureBuilder.create();
    listFields.forEach(field -> {
      String name = ServiceInjectionVerticle.SERVICE_FACTORY + field.getType().getName();

      Promise<Void> promise = Promise.promise();
      discovery.getRecord(new JsonObject().put("name", name)).compose(record -> this.getReference(field, record))
          .onComplete(promise);
      composite.add(promise);
    });
    return composite.all();
  }

  private Future<Void> getReference(Field field, Record record) {
    Promise<Void> promise = Promise.promise();
    if (record != null) {
      Injectable value = discovery.getReference(record).get();
      value.start(vertx, record.getMetadata()).onSuccess(res -> {
        try {
          field.setAccessible(true);
          field.set(instance, value);
          promise.complete();
        } catch (IllegalAccessException e) {
          promise.fail(e);
        }
      }).onFailure(promise::fail);
    } else {
      promise.fail("When injection dependency on class: " + this.instance.getClass().getName()
          + ", the instance not found for field: " + field.getName() + " (" + field.getType().getName() + ") ");
    }
    return promise.future();
  }
}
