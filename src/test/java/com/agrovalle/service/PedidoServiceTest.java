package com.agrovalle.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.agrovalle.dto.request.RealizarPedidoSolicitud;
import com.agrovalle.dto.response.PedidoRespuesta;
import com.agrovalle.entity.Carrito;
import com.agrovalle.entity.DetalleCarrito;
import com.agrovalle.entity.DetalleLote;
import com.agrovalle.entity.LoteProduccion;
import com.agrovalle.entity.OrdenCompra;
import com.agrovalle.entity.Producto;
import com.agrovalle.entity.Usuario;
import com.agrovalle.enums.EstadoLote;
import com.agrovalle.exception.CarritoVacioException;
import com.agrovalle.exception.ProductoNoDisponibleException;
import com.agrovalle.exception.StockInsuficienteException;
import com.agrovalle.exception.TotalPedidoInvalidoException;
import com.agrovalle.repository.CarritoRepository;
import com.agrovalle.repository.DetalleCompraRepository;
import com.agrovalle.repository.DetalleLoteRepository;
import com.agrovalle.repository.EnvioRepository;
import com.agrovalle.repository.LoteProduccionRepository;
import com.agrovalle.repository.OrdenCompraRepository;
import com.agrovalle.repository.PedidoAgricultorRepository;
import com.agrovalle.repository.UsuarioRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("PedidoService - realizar pedido (HU-19)")
class PedidoServiceTest {

  private static final String CORREO = "comprador@agrovalle.com";
  private static final int ID_COMPRADOR = 1;

  @Mock
  private UsuarioRepository usuarioRepository;

  @Mock
  private CarritoRepository carritoRepository;

  @Mock
  private LoteProduccionRepository loteProduccionRepository;

  @Mock
  private OrdenCompraRepository ordenCompraRepository;

  @Mock
  private PedidoAgricultorRepository pedidoAgricultorRepository;

  @Mock
  private DetalleCompraRepository detalleCompraRepository;

  @Mock
  private DetalleLoteRepository detalleLoteRepository;

  @Mock
  private EnvioRepository envioRepository;

  @Mock
  private TrazabilidadLoteService trazabilidadLoteService;

  private PedidoService pedidoService;

  @BeforeEach
  void preparar() {
    // AsignacionLotesService es real (usa los mismos mocks de repositorios) para
    // que las pruebas sigan verificando el FIFO y el descuento de lotes.
    AsignacionLotesService asignacion = new AsignacionLotesService(
        loteProduccionRepository, detalleLoteRepository, trazabilidadLoteService);
    pedidoService = new PedidoService(
        usuarioRepository, carritoRepository, ordenCompraRepository,
        pedidoAgricultorRepository, detalleCompraRepository, envioRepository,
        asignacion);
  }

  // ---------- Datos de apoyo ----------

  private Usuario usuario(int id, String nombre) {
    Usuario usuario = new Usuario();
    usuario.setId(id);
    usuario.setNombre(nombre);
    return usuario;
  }

  private Producto producto(int id, String nombre, String precio, Usuario agricultor) {
    Producto producto = new Producto();
    producto.setIdProducto(id);
    producto.setNombre(nombre);
    producto.setPrecio(new BigDecimal(precio));
    producto.setAgricultor(agricultor);
    return producto;
  }

  private LoteProduccion lote(int id, Producto producto, String fechaCosecha, String disponible) {
    LoteProduccion lote = new LoteProduccion();
    lote.setIdLote(id);
    lote.setProducto(producto);
    lote.setFechaCosecha(LocalDate.parse(fechaCosecha));
    lote.setCantidadProducida(new BigDecimal("100.00"));
    lote.setCantidadDisponible(new BigDecimal(disponible));
    return lote;
  }

  private Carrito carritoVacio(Usuario comprador) {
    Carrito carrito = new Carrito();
    carrito.setId(7);
    carrito.setComprador(comprador);
    return carrito;
  }

  private void agregarItem(Carrito carrito, Producto producto, String cantidad) {
    DetalleCarrito item = new DetalleCarrito();
    item.setCarrito(carrito);
    item.setProducto(producto);
    item.setCantidad(new BigDecimal(cantidad));
    carrito.getItems().add(item);
  }

  private RealizarPedidoSolicitud solicitud() {
    return new RealizarPedidoSolicitud("Calle 10 # 5-20, Tuluá");
  }

  private void assertDecimal(String esperado, BigDecimal real) {
    assertEquals(0, new BigDecimal(esperado).compareTo(real),
        "esperado " + esperado + " pero fue " + real);
  }

  /** Comprador autenticado con el carrito indicado (stubs comunes a casi todas las pruebas). */
  private void autenticarConCarrito(Usuario comprador, Carrito carrito) {
    when(usuarioRepository.findByCorreo(CORREO)).thenReturn(Optional.of(comprador));
    when(carritoRepository.bloquearPorComprador(ID_COMPRADOR)).thenReturn(Optional.of(carrito));
  }

  // ---------- Pruebas ----------

  @Test
  @DisplayName("Descuenta del lote más antiguo primero y reparte entre lotes si hace falta")
  void descuentaEnOrdenFifo() {
    Usuario comprador = usuario(ID_COMPRADOR, "Ana");
    Usuario agricultor = usuario(2, "Carlos");
    Producto tomate = producto(10, "Tomate chonto", "2000.00", agricultor);

    LoteProduccion loteViejo = lote(1, tomate, "2026-11-01", "5.00");
    LoteProduccion loteNuevo = lote(2, tomate, "2026-11-05", "10.00");

    Carrito carrito = carritoVacio(comprador);
    agregarItem(carrito, tomate, "7.00");

    autenticarConCarrito(comprador, carrito);
    // La consulta devuelve primero el lote nuevo: el servicio debe reordenar por fecha.
    when(loteProduccionRepository.bloquearDisponiblesPorProductos(any()))
        .thenReturn(List.of(loteNuevo, loteViejo));

    PedidoRespuesta respuesta = pedidoService.realizar(CORREO, solicitud());

    // 7 = 5 del lote viejo + 2 del nuevo
    assertDecimal("0.00", loteViejo.getCantidadDisponible());
    assertDecimal("8.00", loteNuevo.getCantidadDisponible());

    @SuppressWarnings("unchecked")
    ArgumentCaptor<Iterable<DetalleLote>> captor = ArgumentCaptor.forClass(Iterable.class);
    verify(detalleLoteRepository).saveAll(captor.capture());
    List<DetalleLote> asignaciones = new ArrayList<>();
    captor.getValue().forEach(asignaciones::add);

    assertEquals(2, asignaciones.size());
    assertEquals(1, asignaciones.get(0).getLote().getIdLote());
    assertDecimal("5.00", asignaciones.get(0).getCantidadAsignada());
    assertEquals(2, asignaciones.get(1).getLote().getIdLote());
    assertDecimal("2.00", asignaciones.get(1).getCantidadAsignada());

    // Solo el lote que llegó a 0 deja trazabilidad AGOTADO
    verify(trazabilidadLoteService).registrar(eq(loteViejo), eq(EstadoLote.AGOTADO), anyString());
    verify(trazabilidadLoteService, never())
        .registrar(eq(loteNuevo), any(EstadoLote.class), anyString());

    assertDecimal("14000.00", respuesta.total());
    assertEquals(1, respuesta.pedidos().size());
    assertEquals("PENDIENTE", respuesta.pedidos().get(0).estado());
    assertDecimal("14000.00", respuesta.pedidos().get(0).subtotal());
  }

  @Test
  @DisplayName("Crea un pedido por agricultor y deja el carrito vacío")
  void creaUnPedidoPorAgricultor() {
    Usuario comprador = usuario(ID_COMPRADOR, "Ana");
    Usuario agricultor1 = usuario(2, "Carlos");
    Usuario agricultor2 = usuario(3, "Marta");
    Producto tomate = producto(10, "Tomate chonto", "1000.00", agricultor1);
    Producto cebolla = producto(11, "Cebolla larga", "500.00", agricultor2);

    Carrito carrito = carritoVacio(comprador);
    agregarItem(carrito, tomate, "2.00");
    agregarItem(carrito, cebolla, "3.00");

    autenticarConCarrito(comprador, carrito);
    when(loteProduccionRepository.bloquearDisponiblesPorProductos(any()))
        .thenReturn(List.of(
            lote(1, tomate, "2026-11-01", "10.00"),
            lote(2, cebolla, "2026-11-01", "10.00")));

    PedidoRespuesta respuesta = pedidoService.realizar(CORREO, solicitud());

    assertDecimal("3500.00", respuesta.total());
    assertEquals(2, respuesta.pedidos().size());
    assertEquals(2, respuesta.pedidos().get(0).idAgricultor());
    assertDecimal("2000.00", respuesta.pedidos().get(0).subtotal());
    assertEquals(3, respuesta.pedidos().get(1).idAgricultor());
    assertDecimal("1500.00", respuesta.pedidos().get(1).subtotal());

    // Un envío por pedido y el carrito queda vacío
    verify(envioRepository).saveAll(any());
    verify(ordenCompraRepository).save(any(OrdenCompra.class));
    assertTrue(carrito.getItems().isEmpty());
    verify(carritoRepository).save(carrito);
  }

  @Test
  @DisplayName("Rechaza el pedido si la cantidad supera el stock y no modifica nada")
  void rechazaStockInsuficiente() {
    Usuario comprador = usuario(ID_COMPRADOR, "Ana");
    Producto tomate = producto(10, "Tomate chonto", "2000.00", usuario(2, "Carlos"));

    Carrito carrito = carritoVacio(comprador);
    agregarItem(carrito, tomate, "20.00");

    autenticarConCarrito(comprador, carrito);
    when(loteProduccionRepository.bloquearDisponiblesPorProductos(any()))
        .thenReturn(List.of(
            lote(1, tomate, "2026-11-01", "5.00"),
            lote(2, tomate, "2026-11-05", "10.00")));

    assertThrows(StockInsuficienteException.class,
        () -> pedidoService.realizar(CORREO, solicitud()));

    verify(ordenCompraRepository, never()).save(any(OrdenCompra.class));
    verify(loteProduccionRepository, never()).save(any(LoteProduccion.class));
    verify(carritoRepository, never()).save(any(Carrito.class));
    assertEquals(1, carrito.getItems().size());
  }

  @Test
  @DisplayName("Rechaza el pedido si un producto ya no tiene lotes con existencias")
  void rechazaProductoSinLotes() {
    Usuario comprador = usuario(ID_COMPRADOR, "Ana");
    Producto tomate = producto(10, "Tomate chonto", "2000.00", usuario(2, "Carlos"));

    Carrito carrito = carritoVacio(comprador);
    agregarItem(carrito, tomate, "1.00");

    autenticarConCarrito(comprador, carrito);
    when(loteProduccionRepository.bloquearDisponiblesPorProductos(any()))
        .thenReturn(List.of());

    assertThrows(ProductoNoDisponibleException.class,
        () -> pedidoService.realizar(CORREO, solicitud()));

    verify(ordenCompraRepository, never()).save(any(OrdenCompra.class));
  }

  @Test
  @DisplayName("Rechaza el pedido si el carrito no tiene ítems")
  void rechazaCarritoVacio() {
    Usuario comprador = usuario(ID_COMPRADOR, "Ana");
    autenticarConCarrito(comprador, carritoVacio(comprador));

    assertThrows(CarritoVacioException.class,
        () -> pedidoService.realizar(CORREO, solicitud()));

    verify(loteProduccionRepository, never()).bloquearDisponiblesPorProductos(any());
    verify(ordenCompraRepository, never()).save(any(OrdenCompra.class));
  }

  @Test
  @DisplayName("Rechaza el pedido si el comprador no tiene carrito")
  void rechazaSinCarrito() {
    Usuario comprador = usuario(ID_COMPRADOR, "Ana");
    when(usuarioRepository.findByCorreo(CORREO)).thenReturn(Optional.of(comprador));
    when(carritoRepository.bloquearPorComprador(ID_COMPRADOR)).thenReturn(Optional.empty());

    assertThrows(CarritoVacioException.class,
        () -> pedidoService.realizar(CORREO, solicitud()));
  }

  @Test
  @DisplayName("Rechaza un total que no cabe en numeric(10,2)")
  void rechazaTotalExcedido() {
    Usuario comprador = usuario(ID_COMPRADOR, "Ana");
    Producto caro = producto(10, "Producto caro", "99999999.99", usuario(2, "Carlos"));

    Carrito carrito = carritoVacio(comprador);
    agregarItem(carrito, caro, "2.00");

    autenticarConCarrito(comprador, carrito);
    when(loteProduccionRepository.bloquearDisponiblesPorProductos(any()))
        .thenReturn(List.of(lote(1, caro, "2026-11-01", "10.00")));

    assertThrows(TotalPedidoInvalidoException.class,
        () -> pedidoService.realizar(CORREO, solicitud()));

    verify(ordenCompraRepository, never()).save(any(OrdenCompra.class));
  }
}

