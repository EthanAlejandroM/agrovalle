package com.agrovalle;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegistroUsuarioSolicitud {

  @NotBlank(message = "El nombre es obligatorio.")
  private String nombre;

  @JsonAlias("ubicacion_valle")
  @NotBlank(message = "La ubicación en el Valle es obligatoria.")
  private String ubicacionValle;

  @NotNull(message = "El tipo de documento es obligatorio (CC, CE o PASAPORTE).")
  private TipoDocumento tipoDocumento;

  @NotBlank(message = "El documento es obligatorio.")
  @Pattern(regexp = "^[A-Za-z0-9]+$", message = "El documento solo puede contener letras y números, sin espacios ni guiones.")
  private String documento;

  @NotNull(message = "El rol es obligatorio (AGRICULTOR o COMPRADOR).")
  private TipoUsuario rol;

  @NotBlank(message = "El teléfono es obligatorio.")
  private String telefono;

  @NotBlank(message = "El correo es obligatorio.")
  @Email(message = "El correo debe tener un formato válido.")
  private String correo;

  @NotBlank(message = "La contraseña es obligatoria.")
  @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres.")
  private String contrasena;

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

  public String getTelefono() {
    return telefono;
  }

  public void setTelefono(String telefono) {
    this.telefono = telefono;
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