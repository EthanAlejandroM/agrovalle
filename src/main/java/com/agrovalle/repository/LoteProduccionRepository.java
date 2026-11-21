package com.agrovalle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import com.agrovalle.entity.LoteProduccion;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface LoteProduccionRepository extends JpaRepository<LoteProduccion, Integer> {

  // stock por producto en una sola consulta (sin N+1).
  // Stock = suma de cantidad_disponible de los lotes con existencias.

  @Query("""
      select l.producto.idProducto as idProducto, sum(l.cantidadDisponible) as total
      from LoteProduccion l
      where l.producto.idProducto in :ids
        and l.cantidadDisponible > 0
      group by l.producto.idProducto
      """)
  List<StockProducto> stockPorProductos(@Param("ids") Collection<Integer> ids);

  default Map<Integer, BigDecimal> mapaStock(Collection<Integer> ids) {
    if (ids.isEmpty()) return Map.of();
    return stockPorProductos(ids).stream()
        .collect(Collectors.toMap(StockProducto::getIdProducto, StockProducto::getTotal));
  }

  // ---- HU-19: consultas con bloqueo pesimista (SELECT ... FOR UPDATE) ----
  // Siempre se ordenan por Id_lote para que dos pedidos que toquen los mismos
  // lotes los bloqueen en el mismo orden y no se produzca un deadlock. El orden
  // FIFO (fecha_cosecha) se aplica después, en memoria.

  /** Lotes con existencias de los productos indicados, bloqueados para escritura. */
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("""
      select l from LoteProduccion l
      where l.producto.idProducto in :ids
        and l.cantidadDisponible > 0
      order by l.idLote
      """)
  List<LoteProduccion> bloquearDisponiblesPorProductos(@Param("ids") Collection<Integer> ids);

  /** Lotes por id, bloqueados para escritura (se usa al devolver una reserva). */
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select l from LoteProduccion l where l.idLote in :ids order by l.idLote")
  List<LoteProduccion> bloquearPorIds(@Param("ids") Collection<Integer> ids);
}