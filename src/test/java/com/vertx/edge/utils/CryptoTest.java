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
import static org.junit.jupiter.api.Assertions.assertNull;

import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.vertx.junit5.VertxExtension;

/**
 * @author Luiz Schmidt
 *
 */
@ExtendWith(VertxExtension.class)
class CryptoTest {

  @Test
  void testCryptAndDecryptValueWithSuccess() throws NoSuchAlgorithmException, NoSuchPaddingException {
    String value = "Hello Foo!Bar?";
    String key = "foobar";

    Crypto crypto = new Crypto(key, 16, "AES");
    String encrypted = crypto.encrypt(value);
    assertEquals("OiJN4k0p8bu/YFcnWU0bzg==", encrypted, "value must be encrypt in base64");

    String decrypted = crypto.decrypt(encrypted);
    assertEquals(value, decrypted, "must be the same of initial value");
  }
  
  @Test
  void testCryptAndDecryptValueWithFailure() throws NoSuchAlgorithmException, NoSuchPaddingException {
    String value = "Hello Foo!Bar?";
    String key = "foobar";

    Crypto crypto = new Crypto(key, 16, "AES");
    String encrypted = crypto.encrypt(value);
    assertEquals("OiJN4k0p8bu/YFcnWU0bzg==", encrypted, "value must be encrypt in base64");

    crypto = new Crypto("barfoo", 16, "AES");
    String decrypted = crypto.decrypt(encrypted);
    assertNull(decrypted, "cannot decrypt because key is invalid.");
  }

  @Test
  void testCryptInvalid() throws NoSuchAlgorithmException, NoSuchPaddingException {
    String value = "Hello Foo!Bar?";

    Crypto crypto = new Crypto("", 5, "AES");
    String encrypted = crypto.encrypt(value);
    assertNull(encrypted, "error on encrypt value");
  }
}
