package com.vertx.edge.utils;

import java.util.function.Function;

import io.vertx.core.Future;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VoidFuture<T> implements Function<T, Future<Void>>{

  @Override
  public Future<Void> apply(T t) {
    return Future.succeededFuture();
  }
  
  public static <U> VoidFuture<U> future(){
    return new VoidFuture<U>();
  }

}
