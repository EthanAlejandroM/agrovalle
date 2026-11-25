package com.agrovalle.dto.response;

import java.math.BigDecimal;

public record DetalleCompraRespuesta (
    Integer idProducto,
    String nombre,
    String unidad,
    BigDecimal precioUnitario,
    BigDecimal cantidad,
    BigDecimal total,
    BigDecimal iva) {
    
}
