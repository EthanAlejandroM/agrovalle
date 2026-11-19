package com.agrovalle.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record CarritoRespuesta(List<ItemCarrito> items, BigDecimal subtotal) {

  public record ItemCarrito(
      Integer idProducto,
      String nombre,
      String unidad,
      BigDecimal precioUnitario,
      BigDecimal cantidad,
      BigDecimal subtotal,
      BigDecimal cantidadDisponible,
      boolean stockSuficiente) {

  }
}