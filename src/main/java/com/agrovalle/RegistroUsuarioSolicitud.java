package com.agrovalle;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class RegistroUsuarioSolicitud {

  @NotBlank
  private String nombre;

  @NotBlank
  private String ubicacionValle;

  @NotNull
  private TipoDocumento tipoDocumento;

  @NotBlank
  @Pattern(regexp = "^[A-Za-z0-9]+$")
  private String documento;

  @NotNull
  private TipoUsuario rol;

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

  
}