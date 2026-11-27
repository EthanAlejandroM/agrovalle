package com.agrovalle.exception;

/** La orden indicada no existe. */
public class PedidoNoEncontradoException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public PedidoNoEncontradoException(String mensaje) {
    super(mensaje);
  }
}
