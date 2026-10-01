package com.agrovalle.service;

import com.agrovalle.RegistroUsuarioSolicitud;
import com.agrovalle.Usuario;
import com.agrovalle.UsuarioRepository;
import com.agrovalle.exception.DocumentoDuplicadoException;
import java.time.LocalDate;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

  private final UsuarioRepository usuarioRepository;

  public UsuarioService(UsuarioRepository usuarioRepository) {
    this.usuarioRepository = usuarioRepository;
  }

  public Usuario registrar(RegistroUsuarioSolicitud solicitud) {

    if (usuarioRepository.existsByDocumento(solicitud.getDocumento())) {
      throw new DocumentoDuplicadoException(
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
