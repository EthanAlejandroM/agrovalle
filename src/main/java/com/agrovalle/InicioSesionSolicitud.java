package com.agrovalle;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class InicioSesionSolicitud {

  @NotBlank(message = "El correo electrónico es obligatorio")
  @Email(message = "El formato del correo electrónico no es válido")
  private String correo;

  @NotBlank(message = "La contraseña es obligatoria")
  private String contrasena;

  public InicioSesionSolicitud() {
  }

  public String getCorreo() {
    return correo;
  }

  public void setCorreo(String correo) {
    this.correo = correo;
  }

  public String getContrasena() {
    return contrasena;
  }

  public void setContrasena(String contrasena) {
    this.contrasena = contrasena;
  }
}
