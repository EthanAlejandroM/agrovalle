package com.agrovalle;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

  boolean existsByDocumento(String documento);

  boolean existsByCorreo(String correo);

  Optional<Usuario> findByCorreo(String correo);
}