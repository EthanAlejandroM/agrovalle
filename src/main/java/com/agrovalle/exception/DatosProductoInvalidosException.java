package com.agrovalle.exception;

/** Precio o imagen inválidos al actualizar un producto. */
public class DatosProductoInvalidosException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public DatosProductoInvalidosException(String mensaje) {
    super(mensaje);
  }
}
