package com.agrovalle.dto.response;

import java.math.BigDecimal;
import java.util.List;

/** Resultado de realizar un pedido: la orden y un pedido por cada agricultor. */
public record PedidoRespuesta(
    Integer idOrden,
    BigDecimal total,
    List<PedidoAgricultorResumen> pedidos) {

  public record PedidoAgricultorResumen(
      Integer idPedido,
      Integer idAgricultor,
      String nombreAgricultor,
      String estado,
      BigDecimal subtotal) {

  }
}