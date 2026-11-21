package com.agrovalle.exception;

public class ItemCarritoNoEncontradoException extends RuntimeException {

  public ItemCarritoNoEncontradoException() {
    super("El producto no está en tu carrito");
  }
}
