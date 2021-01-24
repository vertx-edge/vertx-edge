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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author Luiz Schmidt
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Timer {

  private static final long HOURS = 3_600_000_000_000L;
  private static final long MINUTES = 60_000_000_000L;
  private static final long SECONDS = 1_000_000_000L;
  private static final long MILISECONDS = 1_000_000L;

  private long start;
  private long end;

  public static Timer start() {
    Timer timer = new Timer();
    timer.start = System.nanoTime();
    return timer;
  }

  public Timer end() {
    this.end = System.nanoTime();
    return this;
  }

  public Timer restart() {
    this.start = System.nanoTime();
    this.end = 0;
    return this;
  }

  public long getTime() {
    if(end == 0) {
      return System.nanoTime() - start;
    }else {
      return end - start;
    }
  }

  public long getTimeMillis() {
    return getTime() / MILISECONDS;
  }

  public static String toTimeFromMillis(long timeInMilli) {
    return toTime(timeInMilli * MILISECONDS);
  }
  
  private String result() {
    return Timer.toTime(this.getTime());
  }

  public static String toTime(long result) {
    StringBuilder message = new StringBuilder();

    if (result >= HOURS) {
      message.append(result / HOURS).append(" hours ");
      result = result % HOURS;
    }

    if (result >= MINUTES) {
      message.append(result / MINUTES).append(" minutes ");
      result = result % MINUTES;
    }

    if (result >= SECONDS) {
      message.append(result / SECONDS).append(" seconds ");
      result = result % SECONDS;
    }

    if (result >= MILISECONDS) {
      message.append(result / MILISECONDS).append(" ms ");
      result = result % MILISECONDS;
    }

    if (result > 0L) {
      message.append(result).append(" ns");
    }
    return message.toString();
  }

  @Override
  public String toString() {
    return result();
  }
}
