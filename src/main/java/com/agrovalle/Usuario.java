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

  @NotBlank 
  @Enumerated(EnumType.STRING)
  private TipoDocumento tipoDocumento;

  @NotBlank
  private String documento;

  @NotNull
  @Enumerated(EnumType.STRING)
  private TipoUsuario rol;

  @NotNull
  private LocalDate fechaRegistro;
}