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

@Entity 
@Table(name = "usuario")

public class Usuario {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank 
    private String nombre;

    @NotBlank 
    private String ubicacion_valle;

    @NotBlank 
    private String documento;

    @NotBlank 
    @Enumerated (EnumType.STRING)
    private TipoUsuario rol;

    @NotBlank 
    private LocalDate fecha_registro;

}
