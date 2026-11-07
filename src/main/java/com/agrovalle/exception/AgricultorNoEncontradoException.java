package com.agrovalle.exception;

public class AgricultorNoEncontradoException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public AgricultorNoEncontradoException(String mensaje) {
    super(mensaje);
  }
}
