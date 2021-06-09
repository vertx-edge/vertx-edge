package com.vertx.edge.deploy.injection.type;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.reflections.Reflections;

import com.vertx.edge.deploy.injection.Injectable;
import com.vertx.edge.verticle.ServiceInjectionVerticle;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceReference;
import io.vertx.servicediscovery.types.AbstractServiceReference;

public class InjectionTypeImpl implements InjectionType {

  private Map<String, Injectable> services = new HashMap<>();
  
  @Override
  public String name() {
    return InjectionType.TYPE;
  }

  @Override
  public ServiceReference get(Vertx vertx, ServiceDiscovery discovery, Record record, JsonObject config) {
    Objects.requireNonNull(vertx);
    Objects.requireNonNull(discovery);
    Objects.requireNonNull(config);
    return new InjectionServiceReference(vertx, discovery, record);
  }

  private class InjectionServiceReference extends AbstractServiceReference<Injectable>{

    public InjectionServiceReference(Vertx vertx, ServiceDiscovery discovery, Record record) {
      super(vertx, discovery, record);
    }

    @Override
    protected Injectable retrieve() {
      String name = record().getName().replace(ServiceInjectionVerticle.SERVICE_FACTORY, "");
      if(!services.containsKey(name)) {
        services.put(name, newInstance(name));
      }
      return services.get(name);
    }

    private synchronized Injectable newInstance(String name) {
      try {
        return instanceOf(name);
      }catch(SecurityException | ReflectiveOperationException e) {
        throw new IllegalArgumentException(e);
      }catch (IllegalArgumentException e) {
        throw e;
      }
    }

    private Injectable instanceOf(String name) throws ReflectiveOperationException {
      Class<?> clazz = Class.forName(name);
      if(clazz.isInterface()) {
        Set<?> types = getReflections(clazz).getSubTypesOf(clazz);
        if(types.size() > 1) {
          throw new IllegalArgumentException("More than one implementaion of interface: "+clazz.isInterface());
        }else if(types.isEmpty()) {
          throw new IllegalArgumentException("No one class implements interface: "+clazz.isInterface());
        }else {
          return newInstance(((Class<?>) types.iterator().next()).getName());
        }
      }else {
        return (Injectable) clazz.getConstructor().newInstance();  
      }
    }

    private Reflections getReflections(Class<?> clazz) {
      return new Reflections(clazz.getPackageName());
    }
    
  }
}
