package com.agrovalle.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.OneToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Version;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carrito")
public class Carrito {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "\"Id_carrito\"")
  private Integer id;


  //Relacíon con Usuario (comprador)
  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "\"Id_usuario_comprador\"", nullable = false, unique = true)
  private Usuario comprador;

  //Fecha de creación del carrito
  @Column(name = "fecha_creacion", nullable = false)
  private OffsetDateTime fechaCreacion;

  //Fecha de la ultima actualiazación del carrito
  @Column(name = "fecha_actualizacion", nullable = false)
  private OffsetDateTime fechaActualizacion;

  // Relación con DetalleCarrito (productos agregados al carrito)
  @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<DetalleCarrito> items = new ArrayList<>();

  //Control de concurrencia mediante versionado optimista
  @Version
  private Long version;

  // Getters y setters
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Usuario getComprador() {
    return comprador;
  }

  public void setComprador(Usuario comprador) {
    this.comprador = comprador;
  }

  public OffsetDateTime getFechaCreacion() {
    return fechaCreacion;
  }

  public void setFechaCreacion(OffsetDateTime f) {
    this.fechaCreacion = f;
  }

  public OffsetDateTime getFechaActualizacion() {
    return fechaActualizacion;
  }

  public void setFechaActualizacion(OffsetDateTime f) {
    this.fechaActualizacion = f;
  }

  public List<DetalleCarrito> getItems() {
    return items;
  }

  public void setItems(List<DetalleCarrito> items) {
    this.items = items;
  }

  public Long getVersion() {
    return version;
  }

}