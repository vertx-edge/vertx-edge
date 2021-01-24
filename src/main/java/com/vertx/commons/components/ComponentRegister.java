package com.vertx.commons.components;

/**
 * @author Luiz Schmidt
 */
public enum ComponentRegister {

  TCP_SERVER(TCPServer.class, "startTcpServer"),
  TCP_CLIENT(TCPClient.class, "startTcpClient"),
  LIVENESS(LivenessCheckable.class, "startLivenessCheck"), 
  READINESS(ReadinessCheckable.class, "startReadinessCheck");

  private String name;
  private String method;

  ComponentRegister(Class<?> name, String method) {
    this.name = name.getName();
    this.method = method;
  }

  public String getName() {
    return name;
  }

  public String getMethod() {
    return method;
  }
}
