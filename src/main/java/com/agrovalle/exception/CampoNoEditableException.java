package com.agrovalle.exception;

/** Se intentó modificar un campo que el estado actual del lote no permite editar. */
public class CampoNoEditableException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public CampoNoEditableException(String mensaje) {
    super(mensaje);
  }
}
