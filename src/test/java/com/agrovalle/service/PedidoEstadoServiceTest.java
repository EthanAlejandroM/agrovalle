package com.agrovalle.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

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
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("PedidoEstadoService - confirmar pago y liberar reserva (HU-19 / HU-07)")
class PedidoEstadoServiceTest {

  private static final int ID_ORDEN = 100;

  @Mock
  private OrdenCompraRepository ordenCompraRepository;

  @Mock
  private PedidoAgricultorRepository pedidoAgricultorRepository;

  @Mock
  private DetalleLoteRepository detalleLoteRepository;

  @Mock
  private LoteProduccionRepository loteProduccionRepository;

  @Mock
  private TrazabilidadLoteService trazabilidadLoteService;

  @InjectMocks
  private PedidoEstadoService pedidoEstadoService;

  // ---------- Datos de apoyo ----------

  private OrdenCompra orden(String total) {
    OrdenCompra orden = new OrdenCompra();
    orden.setId(ID_ORDEN);
    orden.setTotal(new BigDecimal(total));
    return orden;
  }

  private LoteProduccion lote(int id, String producida, String disponible) {
    LoteProduccion lote = new LoteProduccion();
    lote.setIdLote(id);
    lote.setCantidadProducida(new BigDecimal(producida));
    lote.setCantidadDisponible(new BigDecimal(disponible));
    return lote;
  }

  private DetalleLote asignacion(LoteProduccion lote, String cantidad) {
    DetalleLote asignacion = new DetalleLote();
    asignacion.setLote(lote);
    asignacion.setCantidadAsignada(new BigDecimal(cantidad));
    return asignacion;
  }

  private void assertDecimal(String esperado, BigDecimal real) {
    assertEquals(0, new BigDecimal(esperado).compareTo(real),
        "esperado " + esperado + " pero fue " + real);
  }

  // ---------- confirmarPago ----------

  @Test
  @DisplayName("Confirmar pago pasa los pedidos PENDIENTE de la orden a PAGADO")
  void confirmarPagoPasaAPagado() {
    when(ordenCompraRepository.findById(ID_ORDEN)).thenReturn(Optional.of(orden("14000.00")));
    when(pedidoAgricultorRepository.cambiarEstado(
        ID_ORDEN, EstadoPedido.PENDIENTE, EstadoPedido.PAGADO)).thenReturn(1);

    pedidoEstadoService.confirmarPago(ID_ORDEN, new BigDecimal("14000.0"));

    verify(pedidoAgricultorRepository).cambiarEstado(
        ID_ORDEN, EstadoPedido.PENDIENTE, EstadoPedido.PAGADO);
    // El stock ya se descontó al crear el pedido: no se toca de nuevo
    verifyNoInteractions(loteProduccionRepository);
  }

  @Test
  @DisplayName("Confirmar pago rechaza un monto distinto al total de la orden")
  void confirmarPagoRechazaMontoDistinto() {
    when(ordenCompraRepository.findById(ID_ORDEN)).thenReturn(Optional.of(orden("14000.00")));

    assertThrows(MontoPagoInvalidoException.class,
        () -> pedidoEstadoService.confirmarPago(ID_ORDEN, new BigDecimal("13999.99")));

    verify(pedidoAgricultorRepository, never()).cambiarEstado(any(), any(), any());
  }

  @Test
  @DisplayName("Confirmar pago rechaza una orden que ya no está PENDIENTE")
  void confirmarPagoRechazaEstadoInvalido() {
    when(ordenCompraRepository.findById(ID_ORDEN)).thenReturn(Optional.of(orden("14000.00")));
    when(pedidoAgricultorRepository.cambiarEstado(
        ID_ORDEN, EstadoPedido.PENDIENTE, EstadoPedido.PAGADO)).thenReturn(0);

    assertThrows(EstadoPedidoInvalidoException.class,
        () -> pedidoEstadoService.confirmarPago(ID_ORDEN, new BigDecimal("14000.00")));
  }

  @Test
  @DisplayName("Confirmar pago rechaza una orden que no existe")
  void confirmarPagoRechazaOrdenInexistente() {
    when(ordenCompraRepository.findById(ID_ORDEN)).thenReturn(Optional.empty());

    assertThrows(PedidoNoEncontradoException.class,
        () -> pedidoEstadoService.confirmarPago(ID_ORDEN, new BigDecimal("1.00")));
  }

  // ---------- liberarReserva ----------

  @Test
  @DisplayName("Liberar reserva devuelve lo asignado y reabre un lote AGOTADO")
  void liberarReservaDevuelveStock() {
    LoteProduccion lote = lote(1, "5.00", "0.00");

    when(ordenCompraRepository.findById(ID_ORDEN)).thenReturn(Optional.of(orden("10000.00")));
    when(pedidoAgricultorRepository.cambiarEstado(
        ID_ORDEN, EstadoPedido.PENDIENTE, EstadoPedido.CANCELADO)).thenReturn(1);
    when(detalleLoteRepository.buscarPorOrden(ID_ORDEN))
        .thenReturn(List.of(asignacion(lote, "5.00")));
    when(loteProduccionRepository.bloquearPorIds(any())).thenReturn(List.of(lote));

    pedidoEstadoService.liberarReserva(ID_ORDEN);

    assertDecimal("5.00", lote.getCantidadDisponible());
    verify(trazabilidadLoteService).registrar(eq(lote), eq(EstadoLote.DISPONIBLE), anyString());
  }

  @Test
  @DisplayName("Liberar reserva nunca deja el lote por encima de la cantidad producida")
  void liberarReservaRespetaCantidadProducida() {
    LoteProduccion lote = lote(1, "5.00", "3.00");

    when(ordenCompraRepository.findById(ID_ORDEN)).thenReturn(Optional.of(orden("10000.00")));
    when(pedidoAgricultorRepository.cambiarEstado(
        ID_ORDEN, EstadoPedido.PENDIENTE, EstadoPedido.CANCELADO)).thenReturn(1);
    when(detalleLoteRepository.buscarPorOrden(ID_ORDEN))
        .thenReturn(List.of(asignacion(lote, "4.00")));
    when(loteProduccionRepository.bloquearPorIds(any())).thenReturn(List.of(lote));

    pedidoEstadoService.liberarReserva(ID_ORDEN);

    assertDecimal("5.00", lote.getCantidadDisponible());
    // El lote no estaba agotado: no hay cambio de estado que registrar
    verify(trazabilidadLoteService, never()).registrar(any(), any(), anyString());
  }

  @Test
  @DisplayName("Liberar reserva rechaza una orden que ya no está PENDIENTE y no toca el stock")
  void liberarReservaRechazaEstadoInvalido() {
    when(ordenCompraRepository.findById(ID_ORDEN)).thenReturn(Optional.of(orden("10000.00")));
    when(pedidoAgricultorRepository.cambiarEstado(
        ID_ORDEN, EstadoPedido.PENDIENTE, EstadoPedido.CANCELADO)).thenReturn(0);

    assertThrows(EstadoPedidoInvalidoException.class,
        () -> pedidoEstadoService.liberarReserva(ID_ORDEN));

    verifyNoInteractions(loteProduccionRepository);
    verifyNoInteractions(trazabilidadLoteService);
  }
}
