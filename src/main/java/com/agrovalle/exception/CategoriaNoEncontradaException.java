package com.agrovalle.exception;

public class CategoriaNoEncontradaException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public CategoriaNoEncontradaException(String mensaje) {
    super(mensaje);
  }
}