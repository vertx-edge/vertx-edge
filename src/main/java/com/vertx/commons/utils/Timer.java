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
package com.vertx.commons.utils;

/**
 * @author Luiz Schmidt
 */
public final class Timer {

  private static final long HOURS = 3_600_000_000_000L;
  private static final long MINUTES = 60_000_000_000L;
  private static final long SECONDS = 1_000_000_000L;
  private static final long MILISECONDS = 1_000_000L;

  private long start;
  private long end;

  private Timer() {
    super();
  }

  public static Timer start() {
    Timer timer = new Timer();
    timer.start = System.nanoTime();
    return timer;
  }

  public Timer end() {
    return end(false);
  }

  public Timer end(boolean force) {
    if (force || this.end == 0)
      this.end = System.nanoTime();
    return this;
  }

  public Timer restart() {
    this.start = System.nanoTime();
    this.end = 0;
    return this;
  }

  public long getTime(boolean force) {
    end(force);
    return end - start;
  }

  public long getTimeNano() {
    end();
    return end - start;
  }

  public long getTimeMillis() {
    return getTimeNano() / MILISECONDS;
  }

  public static String toTimeFromMillis(long timeInMilli) {
    return toTime(timeInMilli * MILISECONDS);
  }

  public static String toTime(long timeInNanoSeconds) {
    long result = timeInNanoSeconds;
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

  private String result() {
    return Timer.toTime(getTime(true));
  }

  @Override
  public String toString() {
    return result();
  }
}
