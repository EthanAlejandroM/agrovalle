package com.agrovalle.exception;

/** La orden no está en el estado que exige la operación (por ejemplo, ya no está PENDIENTE). */
public class EstadoPedidoInvalidoException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public EstadoPedidoInvalidoException(String mensaje) {
    super(mensaje);
  }
}
