package com.vertx.commons.utils;

import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

/**
 * @author Luiz Schmidt
 */
public final class FileUtils {

  private FileUtils(){
    //Nothing to do
  }
  
  /**
   * Load first line from file
   * 
   * @param vertx
   * @param path
   * @return
   */
  public static Future<String> loadFileToString(Vertx vertx, String path) {
    if (path == null || path.isEmpty())
      return Future.failedFuture(new FileNotFoundException("File path is required to be informed."));

    Promise<String> promise = Promise.promise();
    vertx.fileSystem().exists(path).onSuccess(exists -> {
      if (exists.booleanValue()) {
        vertx.fileSystem().readFile(path)
          .onSuccess(file -> promise.complete(file.toString(StandardCharsets.UTF_8)))
          .onFailure(cause -> new FileNotFoundException("Error on reading file, cause: " + cause));
      } else {
        promise.fail(new FileNotFoundException("File " + path + " not exists in directory."));
      }
    }).onFailure(promise::fail);
    return promise.future();
  }
}
