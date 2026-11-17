package com.agrovalle.exception;

public class FechaCosechaInvalidaException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public FechaCosechaInvalidaException(String mensaje) {
    super(mensaje);
  }
}
