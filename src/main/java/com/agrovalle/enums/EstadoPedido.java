package com.agrovalle.enums;

/**
 * Estado de un pedido_agricultor.
 * PENDIENTE: creado, stock reservado, a la espera del pago (HU-07).
 * PAGADO: pago aprobado, el descuento de stock pasa a ser definitivo.
 * CANCELADO: pago rechazado, cancelación o plazo vencido; el stock se devolvió.
 */
public enum EstadoPedido {
  PENDIENTE,
  PAGADO,
  CANCELADO
}
