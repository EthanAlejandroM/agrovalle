package com.agrovalle.exception;

/** El monto pagado no coincide con el total de la orden. */
public class MontoPagoInvalidoException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public MontoPagoInvalidoException(String mensaje) {
    super(mensaje);
  }
}
