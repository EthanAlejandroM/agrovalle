package com.agrovalle.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ModificarCantidadSolicitud(
    @NotNull(message = "La cantidad es obligatoria.")
    @DecimalMin(value = "0.00", inclusive = false,
        message = "La cantidad debe ser mayor que cero.")
    @Digits(integer = 8, fraction = 2,
        message = "La cantidad admite máximo 8 enteros y 2 decimales.")
    BigDecimal cantidad) {

}