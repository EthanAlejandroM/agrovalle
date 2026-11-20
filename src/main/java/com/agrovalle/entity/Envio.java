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
import java.time.LocalDate;

/**
 * Envío de un pedido_agricultor. Al crear el pedido se guarda con valores
 * provisionales: costo_envio = 0 y fecha_envio = fecha del pedido, porque ambas
 * columnas son NOT NULL. Los valores reales se completan cuando el agricultor
 * despacha el pedido (HU posterior). Mientras el pedido siga en PENDIENTE, esos
 * dos campos no tienen significado.
 */
@Entity
@Table(name = "envio")
public class Envio {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "\"Id_envio\"")
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "\"Id_pedido_agricultor\"", nullable = false)
  private PedidoAgricultor pedido;

  @Column(name = "direccion_entrega", nullable = false)
  private String direccionEntrega;

  @Column(name = "costo_envio", nullable = false, precision = 10, scale = 2)
  private BigDecimal costoEnvio;

  @Column(name = "numero_guia")
  private String numeroGuia;

  @Column(name = "fecha_envio", nullable = false)
  private LocalDate fechaEnvio;

  public Envio() {
  }

  // Getters y setters
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public PedidoAgricultor getPedido() {
    return pedido;
  }

  public void setPedido(PedidoAgricultor pedido) {
    this.pedido = pedido;
  }

  public String getDireccionEntrega() {
    return direccionEntrega;
  }

  public void setDireccionEntrega(String direccionEntrega) {
    this.direccionEntrega = direccionEntrega;
  }

  public BigDecimal getCostoEnvio() {
    return costoEnvio;
  }

  public void setCostoEnvio(BigDecimal costoEnvio) {
    this.costoEnvio = costoEnvio;
  }

  public String getNumeroGuia() {
    return numeroGuia;
  }

  public void setNumeroGuia(String numeroGuia) {
    this.numeroGuia = numeroGuia;
  }

  public LocalDate getFechaEnvio() {
    return fechaEnvio;
  }

  public void setFechaEnvio(LocalDate fechaEnvio) {
    this.fechaEnvio = fechaEnvio;
  }
}
