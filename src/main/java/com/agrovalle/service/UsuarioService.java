package com.agrovalle.service;

import com.agrovalle.RegistroUsuarioSolicitud;
import com.agrovalle.Usuario;
import com.agrovalle.UsuarioRepository;
import com.agrovalle.exception.DocumentoDuplicadoException;
import com.agrovalle.exception.CorreoDuplicadoException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class UsuarioService {

  private final UsuarioRepository usuarioRepository;
  private final PasswordEncoder passwordEncoder;

  public UsuarioService(
      UsuarioRepository usuarioRepository,
      PasswordEncoder passwordEncoder) {

    this.usuarioRepository = usuarioRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public Usuario registrar(RegistroUsuarioSolicitud solicitud) {

    if (usuarioRepository.existsByDocumento(solicitud.getDocumento())) {
      throw new DocumentoDuplicadoException(
          "Ya existe un usuario registrado con ese documento.");
    }

    if (usuarioRepository.existsByCorreo(solicitud.getCorreo())) {
      throw new CorreoDuplicadoException(
          "Ya existe un usuario registrado con ese correo.");
    }

    Usuario usuario = construirUsuario(solicitud);

    return usuarioRepository.save(usuario);
  }

  private Usuario construirUsuario(RegistroUsuarioSolicitud solicitud) {

    Usuario usuario = new Usuario();

    usuario.setNombre(solicitud.getNombre());
    usuario.setUbicacionValle(solicitud.getUbicacionValle());
    usuario.setTipoDocumento(solicitud.getTipoDocumento());
    usuario.setDocumento(solicitud.getDocumento());
    usuario.setRol(solicitud.getRol());
    usuario.setFechaRegistro(LocalDate.now());
    usuario.setTelefono(solicitud.getTelefono());
    usuario.setCorreo(solicitud.getCorreo());
    // Convertimos la contraseña original en un hash antes de guardarla.
    usuario.setContrasenaHash(
        passwordEncoder.encode(solicitud.getContrasena()));
    return usuario;
  }
}