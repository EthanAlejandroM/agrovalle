package com.agrovalle.exception;

public class ProductoSinStockException extends RuntimeException {

  public ProductoSinStockException() {
    super("El producto no tiene stock disponible");
  }
}
