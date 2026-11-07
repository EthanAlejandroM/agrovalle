package com.agrovalle.controller;

import com.agrovalle.AuthResponseSolicitud;
import com.agrovalle.InicioSesionSolicitud;
import com.agrovalle.RegistroUsuarioSolicitud;
import com.agrovalle.Usuario;
import com.agrovalle.service.AuthService;
import com.agrovalle.service.UsuarioService;
import java.util.Map;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class UsuarioController {

  private final UsuarioService usuarioService;
  private final AuthService authService;

  public UsuarioController(
      UsuarioService usuarioService,
      AuthService authService) {

    this.usuarioService = usuarioService;
    this.authService = authService;
  }

  // Registrar usuario
  @PostMapping("/register")
  public ResponseEntity<Map<String, Object>> registrarUsuario(
      @Valid @RequestBody RegistroUsuarioSolicitud solicitud) {

    Usuario usuario = usuarioService.registrar(solicitud);

    Map<String, Object> respuesta = Map.of(
        "id", usuario.getId(),
        "rol", usuario.getRol().name());

    return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
  }

  // Iniciar sesión
  @PostMapping("/login")
  public ResponseEntity<AuthResponseSolicitud> iniciarSesion(
      @Valid @RequestBody InicioSesionSolicitud solicitud) {

    AuthResponseSolicitud respuesta = authService.iniciarSesion(solicitud);

    return ResponseEntity.ok(respuesta);
  }
}
