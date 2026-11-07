
package com.agrovalle;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class RegistroLoteSolicitud {

  @NotNull(message = "La fecha de cosecha es obligatoria.")
  private LocalDate fechaCosecha;

  @NotNull(message = "La cantidad producida es obligatoria.")
  @PositiveOrZero(message = "La cantidad producida no puede ser negativa.")
  private BigDecimal cantidadProducida;

  @NotNull(message = "La cantidad disponible es obligatoria.")
  @PositiveOrZero(message = "La cantidad disponible no puede ser negativa.")
  private BigDecimal cantidadDisponible;

  @NotBlank(message = "La ubicación del lote es obligatoria.")
  private String ubicacionLote;

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

  public String getUbicacionLote() {
    return ubicacionLote;
  }

  public void setUbicacionLote(String ubicacionLote) {
    this.ubicacionLote = ubicacionLote;
  }
}
