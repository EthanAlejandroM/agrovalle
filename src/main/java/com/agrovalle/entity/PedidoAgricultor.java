package com.agrovalle.entity;

import com.agrovalle.enums.EstadoPedido;
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
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Parte de una orden que corresponde a un agricultor. Es lo que ese agricultor
 * ve en "Mis pedidos".
 *
 * No tiene @Version porque la tabla no tiene columna de versión: los cambios de
 * estado se hacen con un UPDATE condicionado al estado esperado.
 */
@Entity
@Table(name = "pedido_agricultor")
public class PedidoAgricultor {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_pedido_agricultor")
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "id_orden", nullable = false)
  private OrdenCompra orden;

  // Relación con Usuario (agricultor dueño de los productos)
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "id_usuario_agricultor", nullable = false)
  private Usuario agricultor;

  @Column(name = "fecha_creacion", nullable = false)
  private LocalDateTime fechaCreacion;

  @Enumerated(EnumType.STRING)
  @Column(name = "estado_pedido", nullable = false)
  private EstadoPedido estadoPedido;

  @Column(name = "subtotal_pedido", nullable = false, precision = 10, scale = 2)
  private BigDecimal subtotalPedido;

  public PedidoAgricultor() {
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

  public Usuario getAgricultor() {
    return agricultor;
  }

  public void setAgricultor(Usuario agricultor) {
    this.agricultor = agricultor;
  }

  public LocalDateTime getFechaCreacion() {
    return fechaCreacion;
  }

  public void setFechaCreacion(LocalDateTime fechaCreacion) {
    this.fechaCreacion = fechaCreacion;
  }

  public EstadoPedido getEstadoPedido() {
    return estadoPedido;
  }

  public void setEstadoPedido(EstadoPedido estadoPedido) {
    this.estadoPedido = estadoPedido;
  }

  public BigDecimal getSubtotalPedido() {
    return subtotalPedido;
  }

  public void setSubtotalPedido(BigDecimal subtotalPedido) {
    this.subtotalPedido = subtotalPedido;
  }
}

