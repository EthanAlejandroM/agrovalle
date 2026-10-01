package com.agrovalle.exception;

/**
 * Se lanza cuando se intenta registrar un usuario con un documento que ya existe.
 * La capa de servicio no conoce HTTP: el mapeo a 409 se hace en {@link GlobalExceptionHandler}.
 */
public class DocumentoDuplicadoException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public DocumentoDuplicadoException(String mensaje) {
    super(mensaje);
  }
}
