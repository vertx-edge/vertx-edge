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
package com.vertx.edge.deploy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.vertx.edge.utils.ManifestUtil;

import lombok.extern.log4j.Log4j2;

/**
 * @author Luiz Schmidt
 */
@Log4j2
public final class StartInfo {

  private static final String DIR_BANNER = "banner.txt";

  private StartInfo() {
    // Nothing to do
  }

  public static void print() {
    String threadName = Thread.currentThread().getName();
    Thread.currentThread().setName("initializing");
    String banner = getBannerFile();
    if (banner != null) {
      System.out.println(banner);
    }

    if (ManifestUtil.isLoaded()) {
      String projectName = ManifestUtil.read("project-name");
      String projectVersion = ManifestUtil.read("project-version");
      if (!projectName.isEmpty() && !projectVersion.isEmpty())
        log.info("Starting application [{} - v{}]", projectName, projectVersion);
      else if (!projectName.isEmpty())
        log.info("Starting application [{}]", projectName);
      else
        log.info("Starting application...");
      log.info("Created by " + ManifestUtil.read("Created-By") + " on " + ManifestUtil.read("project-datetime"));
      log.info("Vert.x Core " + ManifestUtil.read("vertx-version"));
      log.info("Vert.x Commons " + ManifestUtil.read("vertx-commons-version"));
    }
    Thread.currentThread().setName(threadName);
  }

  private static String getBannerFile() {
    try {
      return Files.readString(Paths.get(DIR_BANNER));
    } catch (IOException e) {
      return null;
    }
  }
}
