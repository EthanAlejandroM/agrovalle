package com.agrovalle.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.OffsetTime;

/**
 * De qué lote salió cuánta cantidad de cada detalle de compra. Permite mostrar
 * al agricultor los lotes asignados y devolver la reserva con exactitud.
 */
@Entity
@Table(name = "detalle_lote")
public class DetalleLote {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "\"Id_detalle_lote\"")
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "\"Id_detalle_compra\"", nullable = false)
  private DetalleCompra detalleCompra;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "\"Id_lote\"", nullable = false)
  private LoteProduccion lote;

  @Column(name = "cantidad_asignada", nullable = false, precision = 10, scale = 2)
  private BigDecimal cantidadAsignada;

  // La columna es "time with time zone": solo guarda la hora.
  @Column(name = "fecha_asignacion", nullable = false, columnDefinition = "time with time zone")
  private OffsetTime fechaAsignacion;

  public DetalleLote() {
  }

  // Getters y setters
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public DetalleCompra getDetalleCompra() {
    return detalleCompra;
  }

  public void setDetalleCompra(DetalleCompra detalleCompra) {
    this.detalleCompra = detalleCompra;
  }

  public LoteProduccion getLote() {
    return lote;
  }

  public void setLote(LoteProduccion lote) {
    this.lote = lote;
  }

  public BigDecimal getCantidadAsignada() {
    return cantidadAsignada;
  }

  public void setCantidadAsignada(BigDecimal cantidadAsignada) {
    this.cantidadAsignada = cantidadAsignada;
  }

  public OffsetTime getFechaAsignacion() {
    return fechaAsignacion;
  }

  public void setFechaAsignacion(OffsetTime fechaAsignacion) {
    this.fechaAsignacion = fechaAsignacion;
  }
}
