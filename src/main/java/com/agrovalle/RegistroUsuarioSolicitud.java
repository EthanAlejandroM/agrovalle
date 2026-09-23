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
}