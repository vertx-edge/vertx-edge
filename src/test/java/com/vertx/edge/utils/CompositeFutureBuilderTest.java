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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

/**
 * @author Luiz Schmidt
 *
 */
@ExtendWith(VertxExtension.class)
class CompositeFutureBuilderTest {

  @Test
  void testShouldCompositeFutureAll(Vertx vertx, VertxTestContext context) {
    Promise<Void> promise1 = Promise.promise();
    Promise<Void> promise2 = Promise.promise();
    Promise<Void> promise3 = Promise.promise();
    
    CompositeFutureBuilder.create().add(promise1).add(promise2.future()).add(promise3).all().onComplete(context.succeeding(v -> {
      assertTrue(promise1.future().succeeded(), "promise1 must be successed");
      assertTrue(promise2.future().succeeded(), "promise2 must be successed");
      assertTrue(promise3.future().succeeded(), "promise3 must be successed");
      context.completeNow();
    }));
    
    promise1.complete();
    promise2.complete();
    promise3.complete();
  }

  @Test
  void testShouldCompositeFutureAllFailBecauseOneOfFail(Vertx vertx, VertxTestContext context) {
    Promise<Void> promise1 = Promise.promise();
    Promise<Void> promise2 = Promise.promise();
    Promise<Void> promise3 = Promise.promise();
    
    CompositeFutureBuilder.create().add(promise1).add(promise2.future()).add(promise3).all().onComplete(context.failing(v -> context.completeNow()));
    
    promise1.complete();
    promise2.complete();
    promise3.fail("error");
  }
  
  @Test
  void testShouldCompositeFutureAny(Vertx vertx, VertxTestContext context) {
    Promise<Void> promise1 = Promise.promise();
    Promise<Void> promise2 = Promise.promise();
    Promise<Void> promise3 = Promise.promise();
    
    CompositeFutureBuilder.create().add(promise1).add(promise2.future()).add(promise3).any().onComplete(context.succeeding(v -> {
      assertTrue(promise1.future().succeeded(), "promise1 must be successed");
      assertFalse(promise2.future().isComplete(), "promise2 must not completed yet");
      assertFalse(promise3.future().isComplete(), "promise3 must not completed yet");
      context.completeNow();
    }));
    
    promise1.complete();
  }
  
  @Test
  void testShouldCompositeAnyOnlyFailIfAllFuturesFailed(Vertx vertx, VertxTestContext context) {
    Promise<Void> promise1 = Promise.promise();
    Promise<Void> promise2 = Promise.promise();
    Promise<Void> promise3 = Promise.promise();
    
    CompositeFutureBuilder.create().add(promise1).add(promise2.future()).add(promise3).any().onComplete(context.failing(v -> context.completeNow()));
    
    promise1.fail("error");
    promise2.fail("error");
    promise3.fail("error");
  }
  
  @Test
  void testShouldCompositeFutureJoinWaitAllFutureCompleted(Vertx vertx, VertxTestContext context) {
    Promise<Void> promise1 = Promise.promise();
    Promise<Void> promise2 = Promise.promise();
    Promise<Void> promise3 = Promise.promise();
    
    CompositeFutureBuilder.create().add(promise1).add(promise2.future()).add(promise3).join().onComplete(context.failing(v -> {
      assertTrue(promise1.future().succeeded(), "promise1 success");
      assertTrue(promise2.future().succeeded(), "promise2 success");
      assertTrue(promise3.future().failed(), "promise3 have failed");
      context.completeNow();
    }));
    
    promise1.complete();
    promise2.complete();
    promise3.fail("error");
  }
  
  @Test
  void testShouldCompositeFutureJoinSuccessWhenAllFutureComplete(Vertx vertx, VertxTestContext context) {
    Promise<Void> promise1 = Promise.promise();
    Promise<Void> promise2 = Promise.promise();
    Promise<Void> promise3 = Promise.promise();
    
    CompositeFutureBuilder.create().add(promise1).add(promise2.future()).add(promise3).join().onComplete(context.succeeding(v -> {
      assertTrue(promise1.future().succeeded(), "promise1 success");
      assertTrue(promise2.future().succeeded(), "promise2 success");
      assertTrue(promise3.future().succeeded(), "promise3 success");
      context.completeNow();
    }));
    
    promise1.complete();
    promise2.complete();
    promise3.complete();
  }
}
