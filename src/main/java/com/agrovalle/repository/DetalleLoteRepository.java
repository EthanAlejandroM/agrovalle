package com.agrovalle.repository;

import com.agrovalle.entity.DetalleLote;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DetalleLoteRepository extends JpaRepository<DetalleLote, Integer> {

  /** Todas las asignaciones de lote de una orden, con el lote ya cargado. */
  @Query("""
      select dl from DetalleLote dl
      join fetch dl.lote
      where dl.detalleCompra.orden.id = :idOrden
      """)
  List<DetalleLote> buscarPorOrden(@Param("idOrden") Integer idOrden);
}
