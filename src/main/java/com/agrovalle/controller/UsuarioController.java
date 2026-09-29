package com.agrovalle.controller;

import com.agrovalle.RegistroUsuarioSolicitud;
import com.agrovalle.Usuario;
import com.agrovalle.service.UsuarioService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UsuarioController {

  private final UsuarioService usuarioService;

  public UsuarioController(UsuarioService usuarioService) {
    this.usuarioService = usuarioService;
  }

  @PostMapping("/api/v1/auth/register")
  public ResponseEntity<Usuario> registrarUsuario(
      @Valid @RequestBody RegistroUsuarioSolicitud solicitud) {

    Usuario usuario = usuarioService.registrar(solicitud);
    return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
  }
}