package com.agrovalle.service;

import com.agrovalle.entity.DetalleLote;
import com.agrovalle.entity.LoteProduccion;
import com.agrovalle.entity.OrdenCompra;
import com.agrovalle.enums.EstadoLote;
import com.agrovalle.enums.EstadoPedido;
import com.agrovalle.exception.EstadoPedidoInvalidoException;
import com.agrovalle.exception.MontoPagoInvalidoException;
import com.agrovalle.exception.PedidoNoEncontradoException;
import com.agrovalle.repository.DetalleLoteRepository;
import com.agrovalle.repository.LoteProduccionRepository;
import com.agrovalle.repository.OrdenCompraRepository;
import com.agrovalle.repository.PedidoAgricultorRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Transiciones de estado de una orden que HU-07 (pago) y el vencimiento del
 * plazo de pago usan sobre los pedidos creados en HU-19.
 *
 * PENDIENTE -> PAGADO     confirmarPago: el descuento de stock ya hecho al crear
 *                         el pedido pasa a ser definitivo.
 * PENDIENTE -> CANCELADO  liberarReserva: se devuelve el stock a los lotes.
 *
 * Cada cambio es un UPDATE ... WHERE estado = 'PENDIENTE': si pago y vencimiento
 * llegan a la vez, solo uno afecta filas y el otro recibe conflicto.
 */
@Service
public class PedidoEstadoService {

  private final OrdenCompraRepository ordenCompraRepository;
  private final PedidoAgricultorRepository pedidoAgricultorRepository;
  private final DetalleLoteRepository detalleLoteRepository;
  private final LoteProduccionRepository loteProduccionRepository;
  private final TrazabilidadLoteService trazabilidadLoteService;

  public PedidoEstadoService(
      OrdenCompraRepository ordenCompraRepository,
      PedidoAgricultorRepository pedidoAgricultorRepository,
      DetalleLoteRepository detalleLoteRepository,
      LoteProduccionRepository loteProduccionRepository,
      TrazabilidadLoteService trazabilidadLoteService) {

    this.ordenCompraRepository = ordenCompraRepository;
    this.pedidoAgricultorRepository = pedidoAgricultorRepository;
    this.detalleLoteRepository = detalleLoteRepository;
    this.loteProduccionRepository = loteProduccionRepository;
    this.trazabilidadLoteService = trazabilidadLoteService;
  }

  /**
   * Pago aprobado: pasa a PAGADO todos los pedidos PENDIENTE de la orden.
   *
   * @param montoPagado monto que informa el pago; debe coincidir con el total
   *                    de la orden (que solo incluye productos)
   */
  @Transactional
  public void confirmarPago(Integer idOrden, BigDecimal montoPagado) {
    OrdenCompra orden = buscarOrden(idOrden);

    if (montoPagado == null || montoPagado.compareTo(orden.getTotal()) != 0) {
      throw new MontoPagoInvalidoException(
          "El monto pagado no coincide con el total de la orden ("
              + orden.getTotal().toPlainString() + ").");
    }

    int actualizados = pedidoAgricultorRepository.cambiarEstado(
        idOrden, EstadoPedido.PENDIENTE, EstadoPedido.PAGADO);

    if (actualizados == 0) {
      throw new EstadoPedidoInvalidoException(
          "La orden no tiene pedidos pendientes de pago.");
    }
  }

  /**
   * Pago rechazado, cancelación o plazo vencido: pasa a CANCELADO los pedidos
   * PENDIENTE y devuelve a cada lote exactamente lo registrado en detalle_lote,
   * sin superar nunca cantidad_producida. Un lote que estaba AGOTADO vuelve a
   * DISPONIBLE y deja trazabilidad.
   */
  @Transactional
  public void liberarReserva(Integer idOrden) {
    buscarOrden(idOrden);

    int actualizados = pedidoAgricultorRepository.cambiarEstado(
        idOrden, EstadoPedido.PENDIENTE, EstadoPedido.CANCELADO);

    if (actualizados == 0) {
      throw new EstadoPedidoInvalidoException(
          "La orden no tiene pedidos pendientes que cancelar.");
    }

    List<DetalleLote> asignaciones = detalleLoteRepository.buscarPorOrden(idOrden);

    // Lotes bloqueados en orden de Id_lote, igual que al crear el pedido
    Set<Integer> idsLote = asignaciones.stream()
        .map(a -> a.getLote().getIdLote())
        .collect(Collectors.toCollection(TreeSet::new));
    Map<Integer, LoteProduccion> lotes = loteProduccionRepository.bloquearPorIds(idsLote)
        .stream()
        .collect(Collectors.toMap(LoteProduccion::getIdLote, Function.identity()));

    for (DetalleLote asignacion : asignaciones) {
      LoteProduccion lote = lotes.get(asignacion.getLote().getIdLote());

      BigDecimal anterior = lote.getCantidadDisponible();
      BigDecimal nueva = anterior.add(asignacion.getCantidadAsignada())
          .min(lote.getCantidadProducida());

      lote.setCantidadDisponible(nueva);
      loteProduccionRepository.save(lote);

      if (anterior.signum() == 0 && nueva.signum() > 0) {
        trazabilidadLoteService.registrar(
            lote, EstadoLote.DISPONIBLE,
            "Stock devuelto por la cancelación de la orden #" + idOrden + ".");
      }
    }
  }

  private OrdenCompra buscarOrden(Integer idOrden) {
    return ordenCompraRepository.findById(idOrden)
        .orElseThrow(() -> new PedidoNoEncontradoException("La orden indicada no existe."));
  }
}
