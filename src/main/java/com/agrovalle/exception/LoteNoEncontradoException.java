package com.agrovalle.exception;

/** El lote indicado no existe o no pertenece al producto. */
public class LoteNoEncontradoException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public LoteNoEncontradoException(String mensaje) {
    super(mensaje);
  }
}
