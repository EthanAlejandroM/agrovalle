package com.agrovalle.exception;

/** Un producto del carrito ya no tiene lotes con existencias. */
public class ProductoNoDisponibleException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public ProductoNoDisponibleException(String mensaje) {
    super(mensaje);
  }
}
