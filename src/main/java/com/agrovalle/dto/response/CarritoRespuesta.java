package com.agrovalle.dto.response;

import java.math.BigDecimal;
import java.util.List;

/**
 * Resumen del carrito. {@code subtotal} es la suma de los totales de las líneas
 * (sin IVA), {@code iva} la suma de los IVA de cada línea y {@code total} =
 * subtotal + iva, el valor que pagaría el comprador.
 */
public record CarritoRespuesta(
    List<ItemCarrito> items,
    BigDecimal subtotal,
    BigDecimal iva,
    BigDecimal total) {

  public record ItemCarrito(
      Integer idProducto,
      String nombre,
      String imagen,
      String unidad,
      BigDecimal precioUnitario,
      BigDecimal cantidad,
      BigDecimal subtotal,
      BigDecimal iva,
      BigDecimal cantidadDisponible,
      boolean stockSuficiente) {

  }
}