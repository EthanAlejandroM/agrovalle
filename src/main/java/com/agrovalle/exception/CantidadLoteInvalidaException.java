package com.agrovalle.exception;

public class CantidadLoteInvalidaException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public CantidadLoteInvalidaException(String mensaje) {
    super(mensaje);
  }
}