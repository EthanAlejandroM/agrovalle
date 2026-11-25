package com.agrovalle.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.time.OffsetTime;

public record OrdenCompra (
    Integer idOrden,
    OffsetTime fechaOrden,
    List<DetalleCompraRespuesta> detalles,
    BigDecimal subtotal,
    BigDecimal iva,
    BigDecimal total) {
    
}
