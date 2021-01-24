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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.jar.Manifest;

import lombok.extern.log4j.Log4j2;

/**
 * @author Luiz Schmidt
 */
@Log4j2
public final class ManifestUtil {

  private static final Map<String, String> manifest = new HashMap<>();
  private static boolean loaded;

  static {
    try {
      manifest.putAll(readManifest(loadManifest()));
      loaded = true;
    } catch (final IOException | URISyntaxException ex) {
      log.warn("Manifest cannot be read: ", ex);
      loaded = false;
    }
  }

  private ManifestUtil() {
    // Nothing to do
  }

  /**
   * Read one value from file MANIFEST.MF
   * 
   * @param the key of manifest
   * @return the value
   */
  public static String read(String string) {
    if (manifest.containsKey(string))
      return manifest.get(string);
    else
      return "";
  }

  /**
   * Load MANIFEST.MF
   * 
   * @return
   * @throws URISyntaxException
   * @throws IOException
   */
  private static Collection<InputStream> loadManifest() throws URISyntaxException, IOException {
    final Enumeration<URL> resources = Thread.currentThread().getContextClassLoader()
        .getResources("META-INF/MANIFEST.MF");
    final Collection<URI> uris = new LinkedList<>();
    while (resources.hasMoreElements()) {
      uris.add(resources.nextElement().toURI());
    }
    final Collection<InputStream> streams = new ArrayList<>(uris.size());
    for (final URI uri : uris) {
      streams.add(uri.toURL().openStream());
    }
    return streams;
  }

  private static Map<String, String> readManifest(final Collection<InputStream> collection) throws IOException {
    for (final InputStream stream : collection) {
      final Manifest man = new Manifest(stream);

      for (final Entry<Object, Object> attr : man.getMainAttributes().entrySet()) {
        manifest.put(attr.getKey().toString(), attr.getValue().toString());
      }
    }
    return manifest;
  }

  public static boolean isLoaded() {
    return loaded;
  }
}
