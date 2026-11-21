package com.agrovalle.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * Centraliza el manejo de errores de la API y los devuelve como
 * {@link ApiError}.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  
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

  @ExceptionHandler(FechaCosechaInvalidaException.class)
  public ResponseEntity<ApiError> manejarFechaCosechaInvalida(
      FechaCosechaInvalidaException ex) {
    ApiError error = ApiError.of(
        HttpStatus.BAD_REQUEST,
        "FECHA_COSECHA_INVALIDA",
        ex.getMessage());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  @ExceptionHandler(AgricultorNoAutorizadoException.class)
  public ResponseEntity<ApiError> manejarAgricultorNoAutorizado(
      AgricultorNoAutorizadoException ex) {
    ApiError error = ApiError.of(
        HttpStatus.FORBIDDEN,
        "AGRICULTOR_NO_AUTORIZADO",
        ex.getMessage());

    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
  }

  /**
   * Control de concurrencia optimista: otro proceso (p. ej. una compra o una
   * edición manual del inventario) modificó el lote después de que se leyó.
   * El cliente debe volver a consultar el stock y reintentar.
   */
  @ExceptionHandler(OptimisticLockingFailureException.class)
  public ResponseEntity<ApiError> manejarConflictoConcurrencia(
      OptimisticLockingFailureException ex) {
    ApiError error = ApiError.of(
        HttpStatus.CONFLICT,
        "CONFLICTO_CONCURRENCIA",
        "El inventario fue modificado por otra operación. "
            + "Consulte el stock actualizado e intente de nuevo.");

    return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
  }

  @ExceptionHandler(ProductoNoEncontradoException.class)
  public ResponseEntity<ApiError> manejarProductoNoEncontrado(ProductoNoEncontradoException ex) {
    ApiError error = ApiError.of(
        HttpStatus.NOT_FOUND, "PRODUCTO_NO_ENCONTRADO", ex.getMessage());

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }

  @ExceptionHandler(LoteNoEncontradoException.class)
  public ResponseEntity<ApiError> manejarLoteNoEncontrado(LoteNoEncontradoException ex) {
    ApiError error = ApiError.of(
        HttpStatus.NOT_FOUND, "LOTE_NO_ENCONTRADO", ex.getMessage());

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }

  @ExceptionHandler(CampoNoEditableException.class)
  public ResponseEntity<ApiError> manejarCampoNoEditable(CampoNoEditableException ex) {
    ApiError error = ApiError.of(
        HttpStatus.CONFLICT, "CAMPO_NO_EDITABLE", ex.getMessage());

    return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
  }

  @ExceptionHandler(SolicitudSinCambiosException.class)
  public ResponseEntity<ApiError> manejarSolicitudSinCambios(SolicitudSinCambiosException ex) {
    ApiError error = ApiError.of(
        HttpStatus.BAD_REQUEST, "SOLICITUD_SIN_CAMBIOS", ex.getMessage());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  @ExceptionHandler(DatosProductoInvalidosException.class)
  public ResponseEntity<ApiError> manejarDatosProductoInvalidos(DatosProductoInvalidosException ex) {
    ApiError error = ApiError.of(
        HttpStatus.BAD_REQUEST, "DATOS_PRODUCTO_INVALIDOS", ex.getMessage());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }
  @ExceptionHandler(RolCompradorInvalidoException.class)
  public ResponseEntity<ApiError> manejarRolCompradorInvalido(
      RolCompradorInvalidoException ex) {
    ApiError error = ApiError.of(
        HttpStatus.FORBIDDEN, "ROL_COMPRADOR_INVALIDO", ex.getMessage());

    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
  }

  @ExceptionHandler(ItemCarritoNoEncontradoException.class)
  public ResponseEntity<ApiError> manejarItemCarritoNoEncontrado(
      ItemCarritoNoEncontradoException ex) {
    ApiError error = ApiError.of(
        HttpStatus.NOT_FOUND, "ITEM_CARRITO_NO_ENCONTRADO", ex.getMessage());

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }

  @ExceptionHandler(ProductoSinStockException.class)
  public ResponseEntity<ApiError> manejarProductoSinStock(ProductoSinStockException ex) {
    ApiError error = ApiError.of(
        HttpStatus.CONFLICT, "PRODUCTO_SIN_STOCK", ex.getMessage());

    return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
  }

  @ExceptionHandler(StockInsuficienteException.class)
  public ResponseEntity<ApiError> manejarStockInsuficiente(StockInsuficienteException ex) {
    ApiError error = ApiError.of(
        HttpStatus.CONFLICT, "STOCK_INSUFICIENTE", ex.getMessage());

    return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
  }

  @ExceptionHandler(CantidadCarritoInvalidaException.class)
  public ResponseEntity<ApiError> manejarCantidadCarritoInvalida(
      CantidadCarritoInvalidaException ex) {
    ApiError error = ApiError.of(
        HttpStatus.BAD_REQUEST, "CANTIDAD_CARRITO_INVALIDA", ex.getMessage());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  @ExceptionHandler(CarritoVacioException.class)
  public ResponseEntity<ApiError> manejarCarritoVacio(CarritoVacioException ex) {
    ApiError error = ApiError.of(
        HttpStatus.BAD_REQUEST, "CARRITO_VACIO", ex.getMessage());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  @ExceptionHandler(ProductoNoDisponibleException.class)
  public ResponseEntity<ApiError> manejarProductoNoDisponible(ProductoNoDisponibleException ex) {
    ApiError error = ApiError.of(
        HttpStatus.CONFLICT, "PRODUCTO_NO_DISPONIBLE", ex.getMessage());

    return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
  }

  @ExceptionHandler(EstadoPedidoInvalidoException.class)
  public ResponseEntity<ApiError> manejarEstadoPedidoInvalido(EstadoPedidoInvalidoException ex) {
    ApiError error = ApiError.of(
        HttpStatus.CONFLICT, "ESTADO_PEDIDO_INVALIDO", ex.getMessage());

    return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
  }

  @ExceptionHandler(PedidoNoEncontradoException.class)
  public ResponseEntity<ApiError> manejarPedidoNoEncontrado(PedidoNoEncontradoException ex) {
    ApiError error = ApiError.of(
        HttpStatus.NOT_FOUND, "PEDIDO_NO_ENCONTRADO", ex.getMessage());

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }

  @ExceptionHandler(MontoPagoInvalidoException.class)
  public ResponseEntity<ApiError> manejarMontoPagoInvalido(MontoPagoInvalidoException ex) {
    ApiError error = ApiError.of(
        HttpStatus.BAD_REQUEST, "MONTO_PAGO_INVALIDO", ex.getMessage());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  @ExceptionHandler(TotalPedidoInvalidoException.class)
  public ResponseEntity<ApiError> manejarTotalPedidoInvalido(TotalPedidoInvalidoException ex) {
    ApiError error = ApiError.of(
        HttpStatus.BAD_REQUEST, "TOTAL_PEDIDO_INVALIDO", ex.getMessage());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  @ExceptionHandler(CompradorNoEncontradoException.class)
  public ResponseEntity<ApiError> manejarCompradorNoEncontrado(CompradorNoEncontradoException ex) {
    ApiError error = ApiError.of(
        HttpStatus.NOT_FOUND, "COMPRADOR_NO_ENCONTRADO", ex.getMessage());

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }

  /**
   * Bloqueo pesimista fallido (timeout, deadlock detectado por la base de datos):
   * mismo tratamiento que el conflicto optimista. El cliente consulta de nuevo
   * el stock y reintenta.
   */
  @ExceptionHandler(PessimisticLockingFailureException.class)
  public ResponseEntity<ApiError> manejarBloqueoPesimista(
      PessimisticLockingFailureException ex) {
    ApiError error = ApiError.of(
        HttpStatus.CONFLICT,
        "CONFLICTO_CONCURRENCIA",
        "El inventario está siendo modificado por otra operación. "
            + "Consulte el stock actualizado e intente de nuevo.");

    return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
  }
}
