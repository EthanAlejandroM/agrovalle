
package com.agrovalle.service;

import com.agrovalle.entity.Usuario;
import com.agrovalle.repository.UsuarioRepository;
import com.agrovalle.dto.request.InicioSesionSolicitud;
import com.agrovalle.dto.response.AuthResponseSolicitud;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  private final UsuarioRepository usuarioRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  public AuthService(
      UsuarioRepository usuarioRepository,
      PasswordEncoder passwordEncoder,
      JwtService jwtService) {

    this.usuarioRepository = usuarioRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
  }

  public AuthResponseSolicitud iniciarSesion(
      InicioSesionSolicitud solicitud) {

    // Buscar usuario por correo
    Usuario usuario = usuarioRepository
        .findByCorreo(solicitud.getCorreo())
        .orElseThrow(() -> new BadCredentialsException(
            "Correo o contraseña incorrectos."));

    // Verificar contraseña mediante BCrypt
    boolean contrasenaValida = passwordEncoder.matches(
        solicitud.getContrasena(),
        usuario.getContrasenaHash());

    if (!contrasenaValida) {
      throw new BadCredentialsException(
          "Correo o contraseña incorrectos.");
    }

    // Generar JWT con los datos del usuario
    String token = jwtService.generateToken(usuario);

    // Retornar respuesta de autenticación
    return new AuthResponseSolicitud(
        token,
        jwtService.getExpirationMs());
  }
}
