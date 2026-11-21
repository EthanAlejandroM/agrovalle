package com.agrovalle.service;

import com.agrovalle.entity.EstadoTrazabilidad;
import com.agrovalle.entity.LoteProduccion;
import com.agrovalle.enums.EstadoLote;
import com.agrovalle.repository.EstadoTrazabilidadRepository;
import java.time.OffsetDateTime;
import org.springframework.stereotype.Service;

/**
 * Registro de trazabilidad de lotes compartido por los servicios de pedido.
 * Hace lo mismo que ProductoService.registrarTrazabilidad; cuando el equipo lo
 * decida, ProductoService puede usar este componente en lugar de su copia
 * privada.
 */
@Service
public class TrazabilidadLoteService {

  private final EstadoTrazabilidadRepository estadoTrazabilidadRepository;

  public TrazabilidadLoteService(EstadoTrazabilidadRepository estadoTrazabilidadRepository) {
    this.estadoTrazabilidadRepository = estadoTrazabilidadRepository;
  }

  public void registrar(LoteProduccion lote, EstadoLote estado, String descripcion) {
    EstadoTrazabilidad trazabilidad = new EstadoTrazabilidad();
    trazabilidad.setLote(lote);
    trazabilidad.setFechaHora(OffsetDateTime.now());
    trazabilidad.setEstadoLote(estado);
    trazabilidad.setDescripcion(descripcion);
    estadoTrazabilidadRepository.save(trazabilidad);
  }
}
