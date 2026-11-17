
package com.agrovalle;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "estado_trazabilidad")
public class EstadoTrazabilidad {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "\"Id_estado_trazabilidad\"")
  private Integer idEstadoTrazabilidad;

  // Relación con LoteProduccion
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "\"Id_lote\"", nullable = false)
  private LoteProduccion lote;

  @NotNull
  private OffsetDateTime fechaHora;

  @NotNull
  @Enumerated(EnumType.STRING)
  private EstadoLote estadoLote;

  private String descripcion;

  public EstadoTrazabilidad() {
  }

  // Getters y setters

  public Integer getIdEstadoTrazabilidad() {
    return idEstadoTrazabilidad;
  }

  public void setIdEstadoTrazabilidad(Integer idEstadoTrazabilidad) {
    this.idEstadoTrazabilidad = idEstadoTrazabilidad;
  }

  public LoteProduccion getLote() {
    return lote;
  }

  public void setLote(LoteProduccion lote) {
    this.lote = lote;
  }

  public OffsetDateTime getFechaHora() {
    return fechaHora;
  }

  public void setFechaHora(OffsetDateTime fechaHora) {
    this.fechaHora = fechaHora;
  }

  public EstadoLote getEstadoLote() {
    return estadoLote;
  }

  public void setEstadoLote(EstadoLote estadoLote) {
    this.estadoLote = estadoLote;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }
}
