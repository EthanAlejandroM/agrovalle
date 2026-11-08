package com.agrovalle;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

  boolean existsByDocumento(String documento);
}