package com.agrovalle.exception;

public class CarritoConcurrenteException extends RuntimeException {

  public CarritoConcurrenteException() {
    super("El carrito fue modificado por otra operación. Consulte el carrito e intente de nuevo.");
  }
}
