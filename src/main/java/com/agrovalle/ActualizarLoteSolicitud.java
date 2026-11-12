package com.agrovalle;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Campos editables de un lote. Cuáles se aceptan depende del estado del lote
 * (lo valida el servicio):
 * <ul>
 * <li>DISPONIBLE: solo cantidadDisponible (0 permitido).</li>
 * <li>AGOTADO: cantidadProducida, cantidadDisponible (&gt; 0) y fechaCosecha.</li>
 * </ul>
 */
public class ActualizarLoteSolicitud {

  private LocalDate fechaCosecha;

  @Positive(message = "La cantidad producida debe ser mayor que cero.")
  private BigDecimal cantidadProducida;

  @PositiveOrZero(message = "La cantidad disponible no puede ser negativa.")
  private BigDecimal cantidadDisponible;

  public LocalDate getFechaCosecha() {
    return fechaCosecha;
  }

  public void setFechaCosecha(LocalDate fechaCosecha) {
    this.fechaCosecha = fechaCosecha;
  }

  public BigDecimal getCantidadProducida() {
    return cantidadProducida;
  }

  public void setCantidadProducida(BigDecimal cantidadProducida) {
    this.cantidadProducida = cantidadProducida;
  }

  public BigDecimal getCantidadDisponible() {
    return cantidadDisponible;
  }

  public void setCantidadDisponible(BigDecimal cantidadDisponible) {
    this.cantidadDisponible = cantidadDisponible;
  }
}
