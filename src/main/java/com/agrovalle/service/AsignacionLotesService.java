package com.agrovalle.service;

import com.agrovalle.entity.DetalleCarrito;
import com.agrovalle.entity.DetalleCompra;
import com.agrovalle.entity.DetalleLote;
import com.agrovalle.entity.LoteProduccion;
import com.agrovalle.entity.Producto;
import com.agrovalle.enums.EstadoLote;
import com.agrovalle.exception.ProductoNoDisponibleException;
import com.agrovalle.exception.StockInsuficienteException;
import com.agrovalle.repository.DetalleLoteRepository;
import com.agrovalle.repository.LoteProduccionRepository;
import java.math.BigDecimal;
import java.time.OffsetTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * HU-19: asignación automática de lotes (FIFO) para un pedido.
 *
 * No abre transacción propia: se ejecuta dentro de la de PedidoService.realizar
 * (propagation REQUIRED), así los bloqueos y los descuentos siguen siendo
 * todo o nada.
 */
@Service
public class AsignacionLotesService {

  private static final Comparator<LoteProduccion> FIFO =
      Comparator.comparing(LoteProduccion::getFechaCosecha)
          .thenComparing(LoteProduccion::getIdLote);

  private final LoteProduccionRepository loteProduccionRepository;
  private final DetalleLoteRepository detalleLoteRepository;
  private final TrazabilidadLoteService trazabilidadLoteService;

  public AsignacionLotesService(
      LoteProduccionRepository loteProduccionRepository,
      DetalleLoteRepository detalleLoteRepository,
      TrazabilidadLoteService trazabilidadLoteService) {

    this.loteProduccionRepository = loteProduccionRepository;
    this.detalleLoteRepository = detalleLoteRepository;
    this.trazabilidadLoteService = trazabilidadLoteService;
  }

  /**
   * Bloquea (en orden de Id_lote) los lotes con existencias de los productos del
   * carrito, revalida el stock y devuelve los lotes por producto, del más
   * antiguo al más nuevo.
   */
  @Transactional
  public Map<Integer, List<LoteProduccion>> bloquearYValidar(List<DetalleCarrito> items) {
    Set<Integer> idsProducto = items.stream()
        .map(i -> i.getProducto().getIdProducto())
        .collect(Collectors.toCollection(LinkedHashSet::new));

    Map<Integer, List<LoteProduccion>> lotesPorProducto = agruparEnOrdenFifo(
        loteProduccionRepository.bloquearDisponiblesPorProductos(idsProducto));

    validarDisponibilidad(items, lotesPorProducto);
    return lotesPorProducto;
  }

  /**
   * Reserva la cantidad del detalle tomando de los lotes del más antiguo al más
   * nuevo. Cada parte queda en detalle_lote; un lote que llega a 0 pasa a
   * AGOTADO y deja trazabilidad.
   */
  @Transactional
  public void reservar(
      DetalleCompra detalle, List<LoteProduccion> lotes, Integer idOrden, OffsetTime ahora) {

    BigDecimal restante = detalle.getCantidad();
    List<DetalleLote> asignaciones = new ArrayList<>();

    for (LoteProduccion lote : lotes) {
      if (restante.signum() <= 0) {
        break;
      }
      BigDecimal tomar = restante.min(lote.getCantidadDisponible());
      if (tomar.signum() <= 0) {
        continue;
      }

      lote.setCantidadDisponible(lote.getCantidadDisponible().subtract(tomar));
      loteProduccionRepository.save(lote);

      DetalleLote asignacion = new DetalleLote();
      asignacion.setDetalleCompra(detalle);
      asignacion.setLote(lote);
      asignacion.setCantidadAsignada(tomar);
      asignacion.setFechaAsignacion(ahora);
      asignaciones.add(asignacion);

      restante = restante.subtract(tomar);

      if (lote.getCantidadDisponible().signum() == 0) {
        trazabilidadLoteService.registrar(
            lote, EstadoLote.AGOTADO,
            "Lote agotado por la orden #" + idOrden + ".");
      }
    }

    detalleLoteRepository.saveAll(asignaciones);
  }

  /** Agrupa los lotes por producto y los deja del más antiguo al más nuevo. */
  private Map<Integer, List<LoteProduccion>> agruparEnOrdenFifo(List<LoteProduccion> lotes) {
    Map<Integer, List<LoteProduccion>> porProducto = new HashMap<>();
    for (LoteProduccion lote : lotes) {
      porProducto
          .computeIfAbsent(lote.getProducto().getIdProducto(), k -> new ArrayList<>())
          .add(lote);
    }
    porProducto.values().forEach(lista -> lista.sort(FIFO));
    return porProducto;
  }

  /**
   * Comprueba, con los lotes ya bloqueados, que cada ítem tenga lotes y stock
   * suficiente. Reporta todos los ítems con problema en un solo mensaje.
   */
  private void validarDisponibilidad(
      List<DetalleCarrito> items, Map<Integer, List<LoteProduccion>> lotesPorProducto) {

    List<String> sinLotes = new ArrayList<>();
    List<String> insuficientes = new ArrayList<>();

    for (DetalleCarrito item : items) {
      Producto producto = item.getProducto();
      List<LoteProduccion> lotes = lotesPorProducto.getOrDefault(
          producto.getIdProducto(), List.of());

      if (lotes.isEmpty()) {
        sinLotes.add(producto.getNombre());
        continue;
      }

      BigDecimal disponible = lotes.stream()
          .map(LoteProduccion::getCantidadDisponible)
          .reduce(BigDecimal.ZERO, BigDecimal::add);

      if (item.getCantidad().compareTo(disponible) > 0) {
        insuficientes.add(producto.getNombre()
            + " (pidió " + item.getCantidad().toPlainString()
            + ", disponible " + disponible.toPlainString() + ")");
      }
    }

    if (!sinLotes.isEmpty()) {
      throw new ProductoNoDisponibleException(
          "Productos sin stock disponible: " + String.join(", ", sinLotes) + ".");
    }
    if (!insuficientes.isEmpty()) {
      throw new StockInsuficienteException(
          "Stock insuficiente para: " + String.join("; ", insuficientes) + ".");
    }
  }
}
