
package com.agrovalle;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EstadoTrazabilidadRepository extends JpaRepository<EstadoTrazabilidad, Integer> {

  /** Último registro de trazabilidad del lote = su estado actual. */
  Optional<EstadoTrazabilidad> findFirstByLoteOrderByFechaHoraDescIdEstadoTrazabilidadDesc(
      LoteProduccion lote);

}
