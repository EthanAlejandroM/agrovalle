package com.agrovalle.bdd;

import com.agrovalle.repository.CategoriaProductoRepository;
import com.agrovalle.repository.EstadoTrazabilidadRepository;
import com.agrovalle.repository.LoteProduccionRepository;
import com.agrovalle.repository.ProductoRepository;
import com.agrovalle.repository.UsuarioRepository;
import io.cucumber.java.Before;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Deja la base de datos H2 vacía antes de cada escenario para que ninguno
 * dependa de los datos que dejó otro (el contexto de Spring se comparte).
 */
public class Hooks {

  @Autowired
  private UsuarioRepository usuarioRepository;

  @Autowired
  private CategoriaProductoRepository categoriaProductoRepository;

  @Autowired
  private ProductoRepository productoRepository;

  @Autowired
  private LoteProduccionRepository loteProduccionRepository;

  @Autowired
  private EstadoTrazabilidadRepository estadoTrazabilidadRepository;

  @Before
  public void limpiarBaseDeDatos() {
    // Orden inverso a las llaves foráneas: trazabilidad -> lote -> producto
    // -> categoría y usuario.
    estadoTrazabilidadRepository.deleteAll();
    loteProduccionRepository.deleteAll();
    productoRepository.deleteAll();
    categoriaProductoRepository.deleteAll();
    usuarioRepository.deleteAll();
  }
}
