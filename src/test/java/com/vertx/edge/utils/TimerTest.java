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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

/**
 * @author Luiz Schmidt
 *
 */
@ExtendWith(VertxExtension.class)
class TimerTest {

  @Test
  void testShouldStartTimerAndGetTimeInNanoseconds() {
    Timer timer = Timer.start();
    assertTrue(timer.getTime() > 0, "Assert nanoseconds are bigger than zero");
  }
  
  @Test
  void testShouldStartTheTimerAndGetTimeTwiceWithoutEnd() {
    Timer timer = Timer.start();
    long firstTime = timer.getTime();
    long secondTime = timer.getTime();
    
    assertTrue(firstTime > 0, "Assert nanoseconds are bigger than zero");
    assertTrue(secondTime > firstTime, "Assert true if the second time is bigger than first time");
  }

  @Test
  void testShouldStartTheTimerAndGetTimeTwiceWithEnd() {
    Timer timer = Timer.start();
    timer.end();
    long firstTime = timer.getTime();
    long secondTime = timer.getTime();
    
    assertTrue(firstTime > 0, "Assert nanoseconds are bigger than zero");
    assertEquals(firstTime, secondTime, "Assert first and second is the same value");
  }
  
  @Test
  void testShouldStartAndRestartTimer(Vertx vertx, VertxTestContext context) {
    Timer timer = Timer.start();
    vertx.setTimer(1000, then -> {
      timer.end();
      long firstTime = timer.getTimeMillis();
      context.verify(() -> assertTrue(firstTime > 1000, "is bigger than first timer"));
    
      timer.restart();
      vertx.setTimer(1000, thenTwo -> {
        timer.end();
        long secondTime = timer.getTimeMillis();
        context.verify(() -> {
          assertTrue(secondTime > 1000 && secondTime < 2000, "is smaller than first and second timer;");
          assertNotNull(timer.toString(), "Print timer must not be null");
        });
        context.completeNow();
      });
    });
  }
  
  @Test
  void testShouldPrintManyWays() {
    String timer_100ns = Timer.toTime(100);
    assertTrue(timer_100ns.contains("100 ns"), "Must contain NS value");
    assertFalse(timer_100ns.contains("ms"), "Must NOT contain NS value");
    
    String timer_2ms = Timer.toTimeFromMillis(2);
    assertFalse(timer_2ms.contains("ns"), "Must NOT contain NS value because is rounded.");
    assertTrue(timer_2ms.contains("2 ms"), "Must contain NS value");

    String timer_1_2s = Timer.toTimeFromMillis(1200);
    assertTrue(timer_1_2s.contains("200 ms"), "Must contain 200 ms value.");
    assertTrue(timer_1_2s.contains("1 seconds"), "Must contain 1 seconds value");

    String timer_1m = Timer.toTimeFromMillis(60 * 1000);
    assertTrue(timer_1m.contains("1 minutes"), "Must contain 1 minutes value");
    
    String timer_1h = Timer.toTimeFromMillis(60 * 60 * 1000);
    assertTrue(timer_1h.contains("1 hour"), "Must contain 1 hour value");
  }
}
