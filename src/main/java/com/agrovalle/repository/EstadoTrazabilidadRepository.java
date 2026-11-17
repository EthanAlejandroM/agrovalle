
package com.agrovalle.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.agrovalle.entity.EstadoTrazabilidad;
import com.agrovalle.entity.LoteProduccion;

public interface EstadoTrazabilidadRepository extends JpaRepository<EstadoTrazabilidad, Integer> {

  /** Último registro de trazabilidad del lote = su estado actual. */
  Optional<EstadoTrazabilidad> findFirstByLoteOrderByFechaHoraDescIdEstadoTrazabilidadDesc(
      LoteProduccion lote);

}
