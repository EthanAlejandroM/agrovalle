package com.agrovalle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.agrovalle.entity.LoteProduccion;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}