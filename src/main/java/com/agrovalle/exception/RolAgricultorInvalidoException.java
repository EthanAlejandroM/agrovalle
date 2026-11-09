package com.agrovalle.exception;

public class RolAgricultorInvalidoException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public RolAgricultorInvalidoException(String mensaje) {
    super(mensaje);
  }
}