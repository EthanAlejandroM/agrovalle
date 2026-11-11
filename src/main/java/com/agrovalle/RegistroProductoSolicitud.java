package com.agrovalle;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class RegistroProductoSolicitud {

  @NotNull(message = "La categoría es obligatoria.")
  private Integer categoriaId;

  @NotNull(message = "El agricultor es obligatorio.")
  private Integer agricultorId;

  @NotBlank(message = "El nombre del producto es obligatorio.")
  private String nombre;

  @NotBlank(message = "El tipo de producto es obligatorio.")
  private String tipoProducto;

  @NotNull(message = "La unidad de medida es obligatoria.")
  private UnidadMedida unidadMedida;

  @NotNull(message = "El precio es obligatorio.")
  @DecimalMin(value = "0.01", message = "El precio debe ser mayor que cero.")
  private BigDecimal precio;

  @NotBlank(message = "La imagen es obligatoria.")
  private String imagen;

  @NotNull(message = "La fecha de cosecha es obligatoria.")
  private LocalDate fechaCosecha;

  @NotNull(message = "La cantidad producida es obligatoria.")
  @Positive(message = "La cantidad producida debe ser mayor que cero.")
  private BigDecimal cantidadProducida;

  @NotNull(message = "La cantidad disponible es obligatoria.")
  @Positive(message = "La cantidad disponible debe ser mayor que cero.")
  private BigDecimal cantidadDisponible;

  @NotBlank(message = "La ubicación del lote es obligatoria.")
  private String ubicacionLote;

  public Integer getCategoriaId() {
    return categoriaId;
  }

  public void setCategoriaId(Integer categoriaId) {
    this.categoriaId = categoriaId;
  }

  public Integer getAgricultorId() {
    return agricultorId;
  }

  public void setAgricultorId(Integer agricultorId) {
    this.agricultorId = agricultorId;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getTipoProducto() {
    return tipoProducto;
  }

  public void setTipoProducto(String tipoProducto) {
    this.tipoProducto = tipoProducto;
  }

  public UnidadMedida getUnidadMedida() {
    return unidadMedida;
  }

  public void setUnidadMedida(UnidadMedida unidadMedida) {
    this.unidadMedida = unidadMedida;
  }

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