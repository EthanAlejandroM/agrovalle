package com.agrovalle.exception;

/** La solicitud de actualización no trae ningún campo para modificar. */
public class SolicitudSinCambiosException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public SolicitudSinCambiosException(String mensaje) {
    super(mensaje);
  }
}
