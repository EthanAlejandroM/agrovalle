package com.agrovalle;

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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "producto")
public class Producto {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "\"Id_producto\"")
  private Integer idProducto;

  // Relación con CategoriaProducto
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "\"Id_categoria\"", nullable = false)
  private CategoriaProducto categoria;

  // Relación con Usuario (agricultor)
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "\"Id_usuario_agricultor\"", nullable = false)
  private Usuario agricultor;

  @NotBlank
  private String nombre;

  @NotBlank
  private String tipoProducto;

  @NotNull
  @Enumerated(EnumType.STRING)
  private UnidadMedida unidadMedida;

  @NotNull
  @Positive
  private BigDecimal precio;

  @NotBlank
  private String imagen;

  // Relación con LoteProduccion
  @OneToMany(mappedBy = "producto", fetch = FetchType.LAZY)
  private List<LoteProduccion> lotesProduccion = new ArrayList<>();

  public Producto() {
  }

  // Getters y setters
  public Integer getIdProducto() {
    return idProducto;
  }

  public void setIdProducto(Integer idProducto) {
    this.idProducto = idProducto;
  }

  public CategoriaProducto getCategoria() {
    return categoria;
  }

  public void setCategoria(CategoriaProducto categoria) {
    this.categoria = categoria;
  }

  public Usuario getAgricultor() {
    return agricultor;
  }

  public void setAgricultor(Usuario agricultor) {
    this.agricultor = agricultor;
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

  public List<LoteProduccion> getLotesProduccion() {
    return lotesProduccion;
  }

  public void setLotesProduccion(List<LoteProduccion> lotesProduccion) {
    this.lotesProduccion = lotesProduccion;
  }
}