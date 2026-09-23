package com.agrovalle;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UsuarioRepositoryTest {

  @Autowired
  private UsuarioRepository usuarioRepository;

  @Test
  void repositoryDebeEstarDisponible() {
    assertNotNull(usuarioRepository);
  }
}