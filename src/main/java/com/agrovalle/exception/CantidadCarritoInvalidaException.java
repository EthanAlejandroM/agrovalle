package com.agrovalle.exception;

public class CantidadCarritoInvalidaException extends RuntimeException {

  public CantidadCarritoInvalidaException(String mensaje) {
    super(mensaje);
  }
}
