package com.agrovalle.exception;

/**
 * Se lanza cuando se intenta registrar un usuario con un correo que ya existe.
 * La capa de servicio no conoce HTTP: el mapeo a 409 se hace en
 * {@link GlobalExceptionHandler}.
 */
public class CorreoDuplicadoException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public CorreoDuplicadoException(String mensaje) {
    super(mensaje);
  }
}