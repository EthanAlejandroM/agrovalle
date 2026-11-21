package com.agrovalle.exception;

/** El comprador no tiene ítems en su carrito. */
public class CarritoVacioException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public CarritoVacioException(String mensaje) {
    super(mensaje);
  }
}
