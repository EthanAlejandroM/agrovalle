package com.agrovalle;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;

/**
 * Datos editables de un producto: solo precio e imagen. Ambos son opcionales,
 * pero al menos uno debe venir (lo valida el servicio).
 */
public class ActualizarProductoSolicitud {

  @DecimalMin(value = "0.01", message = "El precio debe ser mayor que cero.")
  private BigDecimal precio;

  @Pattern(regexp = ".*\\S.*", message = "La imagen no puede estar vacía.")
  private String imagen;

  public BigDecimal getPrecio() {
    return precio;
  }

  public void setPrecio(BigDecimal precio) {
    this.precio = precio;
  }

  public String getImagen() {
    return imagen;
  }

  public void setImagen(String imagen) {
    this.imagen = imagen;
  }
}
