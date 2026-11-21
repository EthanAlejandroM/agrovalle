package com.agrovalle.exception;

public class RolCompradorInvalidoException extends RuntimeException {

  public RolCompradorInvalidoException() {
    super("Solo el comprador puede usar el carrito");
  }
}