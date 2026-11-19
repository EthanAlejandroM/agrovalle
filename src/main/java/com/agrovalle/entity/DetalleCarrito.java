package com.agrovalle.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Version;

import java.math.BigDecimal;

@Entity
@Table(name = "detalle_carrito")
public class DetalleCarrito {


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "\"Id_detalle_carrito\"")
  private Integer id;

  // Relación con Carrito
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "\"Id_carrito\"", nullable = false)
  private Carrito carrito;

  // Relación con Producto
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "\"Id_producto\"", nullable = false)
  private Producto producto;

  // Cantidad del producto que desea comprar el usuario
  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal cantidad;

  // Control de concurrencia mediante versionado optimista
  @Version
  private Long version;

  // Getters y setters
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Carrito getCarrito() {
    return carrito;
  }

  public void setCarrito(Carrito carrito) {
    this.carrito = carrito;
  }

  public Producto getProducto() {
    return producto;
  }

  public void setProducto(Producto producto) {
    this.producto = producto;
  }

  public BigDecimal getCantidad() {
    return cantidad;
  }

  public void setCantidad(BigDecimal cantidad) {
    this.cantidad = cantidad;
  }


  public Long getVersion() {
    return version;
  }

}