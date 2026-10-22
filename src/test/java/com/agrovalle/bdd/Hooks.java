package com.agrovalle.bdd;

import com.agrovalle.UsuarioRepository;
import io.cucumber.java.Before;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Deja la base de datos H2 vacía antes de cada escenario para que ninguno
 * dependa de los datos que dejó otro (el contexto de Spring se comparte).
 */
public class Hooks {

  @Autowired
  private UsuarioRepository usuarioRepository;

  @Before
  public void limpiarBaseDeDatos() {
    usuarioRepository.deleteAll();
  }
}
