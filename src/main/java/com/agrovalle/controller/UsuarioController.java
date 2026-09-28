package com.agrovalle.controller;

import com.agrovalle.RegistroUsuarioSolicitud;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UsuarioController {
  
  @PostMapping("/api/v1/auth/register")
  public ResponseEntity<Void> registrarUsuario(
      @Valid @RequestBody RegistroUsuarioSolicitud solicitud) {

    return ResponseEntity.ok().build();
  }
}