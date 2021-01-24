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

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import lombok.extern.log4j.Log4j2;

/**
 * @author Luiz Schmidt
 */
@Log4j2
public class Crypto {
  private SecretKeySpec secretKey;
  private Cipher cipher;

  public Crypto(String secret, int length, String algorithm) throws NoSuchAlgorithmException, NoSuchPaddingException {
    byte[] key = new byte[length];
    key = fixSecret(secret, length);
    this.secretKey = new SecretKeySpec(key, algorithm);
    this.cipher = Cipher.getInstance(algorithm);
  }

  private static byte[] fixSecret(String s, int length) {
    if (s.length() < length) {
      int missingLength = length - s.length();
      StringBuilder fixed = new StringBuilder(s);
      for (int i = 0; i < missingLength; i++) {
        fixed.append(' ');
      }
      s = fixed.toString();
    }
    return s.substring(0, length).getBytes(StandardCharsets.UTF_8);
  }

  public String decrypt(String encrypted) {
    try {
      this.cipher.init(Cipher.DECRYPT_MODE, this.secretKey);
      byte[] cryptedBytes = this.cipher.doFinal(Base64.getDecoder().decode(encrypted));
      return new String(cryptedBytes, StandardCharsets.UTF_8);
    } catch (GeneralSecurityException e) {
      log.error("Cannot decrypt value: ", e);
      return null;
    }
  }

  public String encrypt(String string) {
    try {
      this.cipher.init(Cipher.ENCRYPT_MODE, this.secretKey);
      byte[] cryptedBytes = this.cipher.doFinal(string.getBytes(StandardCharsets.UTF_8));
      return Base64.getEncoder().encodeToString(cryptedBytes);
    } catch (GeneralSecurityException e) {
      log.error("Cannot encrypt value: ", e);
      return null;
    }
  }
}
