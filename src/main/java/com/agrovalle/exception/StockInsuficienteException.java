package com.agrovalle.exception;

import java.math.BigDecimal;


public class StockInsuficienteException extends RuntimeException {

  private final BigDecimal disponible;

  public StockInsuficienteException(BigDecimal disponible) {
    super("Stock insuficiente. Disponible: " + disponible);
    this.disponible = disponible;
  }

  public BigDecimal getDisponible() {
    return disponible;
  }
}
