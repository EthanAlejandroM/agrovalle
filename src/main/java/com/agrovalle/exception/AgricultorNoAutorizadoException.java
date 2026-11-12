package com.agrovalle.exception;

/**
 * El agricultor indicado en el cuerpo de la solicitud no coincide con el
 * usuario autenticado (token).
 */
public class AgricultorNoAutorizadoException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public AgricultorNoAutorizadoException(String mensaje) {
    super(mensaje);
  }
}
