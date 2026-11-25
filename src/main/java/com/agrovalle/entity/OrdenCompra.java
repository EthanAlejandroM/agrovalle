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

@Entity
@Table(name = "orden_compra")
public class OrdenCompra {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "\"Id_orden\"")
  private Integer id;

  // Relación con Usuario (comprador)
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "\"Id_usuario_comprador\"", nullable = false)
  private Usuario comprador;

  // La columna es "time with time zone": solo guarda la hora. El día del pedido
  // se consulta en pedido_agricultor.fecha_creacion.
  @Column(name = "fecha_orden", nullable = false, columnDefinition = "time with time zone")
  private OffsetTime fechaOrden;

  // Suma de los totales de los detalles de detalle_compra
  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal subtotal;

  //suma de los IVAs de los detalles de detalle_compra
  @Column (nullable = false, precision = 10, scale = 2)
  private BigDecimal iva;

  // total final de la compra total= subtotal + iva
  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal total;

  // constructor vacío requerido por JPA
  public OrdenCompra() {
  }

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

  public OffsetTime getFechaOrden() {
    return fechaOrden;
  }

  public void setFechaOrden(OffsetTime fechaOrden) {
    this.fechaOrden = fechaOrden;
  }

  public BigDecimal getSubtotal() {
    return subtotal;
  }

  public void setSubtotal(BigDecimal subtotal) {
    this.subtotal = subtotal;
  }

  public BigDecimal getIva() {
    return iva;
  }

  public void setIva(BigDecimal iva) {
    this.iva = iva;
  }

  public BigDecimal getTotal() {
    return total;
  }

  public void setTotal(BigDecimal total) {
    this.total = total;
  }
}
