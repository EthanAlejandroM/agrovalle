package com.agrovalle;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "lote_produccion")
public class LoteProduccion {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "\"Id_lote\"")
  private Integer idLote;

  @NotBlank
  @Column(name = "codigo_lote")
  private String codigoLote;

  // Relación con Producto obligada
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "\"Id_producto\"", nullable = false)
  private Producto producto;

  @NotNull
  private LocalDate fechaCosecha;

  @NotNull
  @PositiveOrZero
  private BigDecimal cantidadProducida;

  @NotNull
  @PositiveOrZero
  private BigDecimal cantidadDisponible;

  @NotBlank
  private String ubicacionLote;

  public LoteProduccion() {
  }

  // Getters y setters
  public Integer getIdLote() {
    return idLote;
  }

  public void setIdLote(Integer idLote) {
    this.idLote = idLote;
  }

  public String getCodigoLote() {
    return codigoLote;
  }

  public void setCodigoLote(String codigoLote) {
    this.codigoLote = codigoLote;
  }

  public Producto getProducto() {
    return producto;
  }

  public void setProducto(Producto producto) {
    this.producto = producto;
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