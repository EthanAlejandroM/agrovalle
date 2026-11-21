package com.agrovalle.exception;

/** El total del pedido supera el máximo que admite numeric(10,2). */
public class TotalPedidoInvalidoException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public TotalPedidoInvalidoException(String mensaje) {
    super(mensaje);
  }
}
