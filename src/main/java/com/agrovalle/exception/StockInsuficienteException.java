package com.agrovalle.exception;

import java.math.BigDecimal;


public class StockInsuficienteException extends RuntimeException {

  private final BigDecimal disponible;

  public StockInsuficienteException(BigDecimal disponible) {
    super("Stock insuficiente. Disponible: " + disponible);
    this.disponible = disponible;
  }

  // HU-19 (pedido): mensaje con el detalle de varios productos.
  public StockInsuficienteException(String mensaje) {
    super(mensaje);
    this.disponible = null;
  }

  public BigDecimal getDisponible() {
    return disponible;
  }
}
