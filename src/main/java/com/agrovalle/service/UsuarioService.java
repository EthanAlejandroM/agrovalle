package com.agrovalle.service;

import com.agrovalle.RegistroUsuarioSolicitud;
import com.agrovalle.Usuario;
import com.agrovalle.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;


@Service
public class UsuarioService {

  private final UsuarioRepository usuarioRepository;

  public UsuarioService(UsuarioRepository usuarioRepository) {
    this.usuarioRepository = usuarioRepository;
  }

  public Usuario registrar(
      RegistroUsuarioSolicitud solicitud) {

    if (usuarioRepository.existsByDocumento(
        solicitud.getDocumento())) {

      throw new ResponseStatusException(
          HttpStatus.CONFLICT,
          "Ya existe un usuario registrado con ese documento.");

    }

    Usuario usuario = new Usuario();

    usuario.setNombre(solicitud.getNombre());
    usuario.setUbicacionValle(solicitud.getUbicacionValle());
    usuario.setTipoDocumento(solicitud.getTipoDocumento());
    usuario.setDocumento(solicitud.getDocumento());

    usuario.setRol(solicitud.getRol());

    usuario.setFechaRegistro(LocalDate.now());

    return usuarioRepository.save(usuario);

  }
}