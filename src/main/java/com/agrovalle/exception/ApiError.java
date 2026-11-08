package com.agrovalle.exception;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;

/**
 * Cuerpo JSON estándar para todas las respuestas de error de la API.
 */
public final class ApiError {

  private final int status;
  private final String codigo;
  private final String detalle;
  private final List<ErrorCampo> errores;
  private final LocalDateTime timestamp;

  private ApiError(int status, String codigo, String detalle, List<ErrorCampo> errores) {
    this.status = status;
    this.codigo = codigo;
    this.detalle = detalle;
    this.errores = errores;
    this.timestamp = LocalDateTime.now();
  }

  public static ApiError of(HttpStatus status, String codigo, String detalle) {
    return new ApiError(status.value(), codigo, detalle, List.of());
  }

  public static ApiError of(
      HttpStatus status, String codigo, String detalle, List<ErrorCampo> errores) {
    return new ApiError(status.value(), codigo, detalle, errores);
  }

  public int getStatus() {
    return status;
  }

  public String getCodigo() {
    return codigo;
  }

  public String getDetalle() {
    return detalle;
  }

  public List<ErrorCampo> getErrores() {
    return errores;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  /**
   * Error asociado a un campo específico de la solicitud.
   */
  public static final class ErrorCampo {

    private final String campo;
    private final String mensaje;

    public ErrorCampo(String campo, String mensaje) {
      this.campo = campo;
      this.mensaje = mensaje;
    }

    public String getCampo() {
      return campo;
    }

    public String getMensaje() {
      return mensaje;
    }
  }
}
