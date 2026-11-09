package com.agrovalle.exception;

import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Centraliza el manejo de errores de la API y los devuelve como
 * {@link ApiError}.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final String MENSAJE_DUPLICADO_DOCUMENTO = "Ya existe un usuario registrado con ese documento.";

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> manejarValidacion(MethodArgumentNotValidException ex) {
    List<ApiError.ErrorCampo> errores = ex.getBindingResult().getFieldErrors().stream()
        .map(error -> new ApiError.ErrorCampo(error.getField(), error.getDefaultMessage()))
        .toList();

    ApiError error = ApiError.of(
        HttpStatus.BAD_REQUEST,
        "VALIDACION_FALLIDA",
        "La solicitud contiene datos inválidos.",
        errores);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiError> manejarCuerpoIlegible(HttpMessageNotReadableException ex) {
    ApiError error = ApiError.of(
        HttpStatus.BAD_REQUEST,
        "SOLICITUD_ILEGIBLE",
        "El cuerpo de la solicitud falta o no es un JSON válido "
        + "(revise también los valores de tipoDocumento y rol).");

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  @ExceptionHandler(DocumentoDuplicadoException.class)
  public ResponseEntity<ApiError> manejarDocumentoDuplicado(DocumentoDuplicadoException ex) {
    ApiError error = ApiError.of(
        HttpStatus.CONFLICT, "DOCUMENTO_DUPLICADO", ex.getMessage());

    return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
  }

  /**
   * Red de seguridad ante una condición de carrera: dos registros simultáneos con
   * el mismo
   * documento pasan la verificación del servicio y el UNIQUE de PostgreSQL
   * rechaza el segundo.
   */
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ApiError> manejarViolacionIntegridad(
      DataIntegrityViolationException ex) {

    ApiError error = ApiError.of(
        HttpStatus.CONFLICT,
        "VIOLACION_INTEGRIDAD",
        "No fue posible completar el registro debido a una restricción de integridad de datos.");

    return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
  }

  @ExceptionHandler(CorreoDuplicadoException.class)
  public ResponseEntity<ApiError> manejarCorreoDuplicado(CorreoDuplicadoException ex) {
    ApiError error = ApiError.of(
        HttpStatus.CONFLICT, "CORREO_DUPLICADO", ex.getMessage());

    return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ApiError> manejarCredencialesInvalidas(BadCredentialsException ex) {
    ApiError error = ApiError.of(
        HttpStatus.UNAUTHORIZED, "CREDENCIALES_INVALIDAS", ex.getMessage());

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
  }

  @ExceptionHandler(CategoriaNoEncontradaException.class)
  public ResponseEntity<ApiError> manejarCategoriaNoEncontrada(
      CategoriaNoEncontradaException ex) {
    ApiError error = ApiError.of(
        HttpStatus.NOT_FOUND,
        "CATEGORIA_NO_ENCONTRADA",
        ex.getMessage());

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }

  @ExceptionHandler(AgricultorNoEncontradoException.class)
  public ResponseEntity<ApiError> manejarAgricultorNoEncontrado(
      AgricultorNoEncontradoException ex) {
    ApiError error = ApiError.of(
        HttpStatus.NOT_FOUND,
        "AGRICULTOR_NO_ENCONTRADO",
        ex.getMessage());

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }

  @ExceptionHandler(RolAgricultorInvalidoException.class)
  public ResponseEntity<ApiError> manejarRolAgricultorInvalido(
      RolAgricultorInvalidoException ex) {
    ApiError error = ApiError.of(
        HttpStatus.FORBIDDEN,
        "ROL_AGRICULTOR_INVALIDO",
        ex.getMessage());

    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
  }

  @ExceptionHandler(CantidadLoteInvalidaException.class)
  public ResponseEntity<ApiError> manejarCantidadLoteInvalida(
      CantidadLoteInvalidaException ex) {
    ApiError error = ApiError.of(
        HttpStatus.BAD_REQUEST,
        "CANTIDAD_LOTE_INVALIDA",
        ex.getMessage());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }
}
