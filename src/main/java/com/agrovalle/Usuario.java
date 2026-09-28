package com.agrovalle;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "usuario")
public class Usuario {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  private String nombre;

  @NotBlank
  private String ubicacionValle;

  @NotNull 
  @Enumerated(EnumType.STRING)
  private TipoDocumento tipoDocumento;

  @NotBlank
  private String documento;

  @NotNull 
  @Enumerated(EnumType.STRING)
  private TipoUsuario rol;

  @NotNull
  private LocalDate fechaRegistro;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getUbicacionValle() {
    return ubicacionValle;
  }

  public void setUbicacionValle(String ubicacionValle) {
    this.ubicacionValle = ubicacionValle;
  }

  public TipoDocumento getTipoDocumento() {
    return tipoDocumento;
  }

  public void setTipoDocumento(TipoDocumento tipoDocumento) {
    this.tipoDocumento = tipoDocumento;
  }

  public String getDocumento() {
    return documento;
  }

  public void setDocumento(String documento) {
    this.documento = documento;
  }

  public TipoUsuario getRol() {
    return rol;
  }

  public void setRol(TipoUsuario rol) {
    this.rol = rol;
  }

  public LocalDate getFechaRegistro() {
    return fechaRegistro;
  }

  public void setFechaRegistro(LocalDate fechaRegistro) {
    this.fechaRegistro = fechaRegistro;
  }
}