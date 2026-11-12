package com.agrovalle.exception;

/** El producto indicado no existe. */
public class ProductoNoEncontradoException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public ProductoNoEncontradoException(String mensaje) {
    super(mensaje);
  }
}
