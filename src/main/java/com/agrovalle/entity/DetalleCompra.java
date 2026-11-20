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

@Entity
@Table(name = "detalle_compra")
public class DetalleCompra {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "\"Id_detalle_compra\"")
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "\"Id_orden\"", nullable = false)
  private OrdenCompra orden;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "\"Id_producto\"", nullable = false)
  private Producto producto;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal cantidad;

  // Precio del producto copiado al confirmar el pedido: no cambia aunque el
  // agricultor modifique después el precio del producto.
  @Column(name = "precio_unitario", precision = 10, scale = 2)
  private BigDecimal precioUnitario;

  // cantidad x precioUnitario, escala 2
  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal total;

  public DetalleCompra() {
  }

  // Getters y setters
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public OrdenCompra getOrden() {
    return orden;
  }

  public void setOrden(OrdenCompra orden) {
    this.orden = orden;
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

  public BigDecimal getPrecioUnitario() {
    return precioUnitario;
  }

  public void setPrecioUnitario(BigDecimal precioUnitario) {
    this.precioUnitario = precioUnitario;
  }

  public BigDecimal getTotal() {
    return total;
  }

  public void setTotal(BigDecimal total) {
    this.total = total;
  }
}

