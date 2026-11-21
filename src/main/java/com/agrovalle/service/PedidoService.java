package com.agrovalle.service;


import com.agrovalle.dto.request.RealizarPedidoSolicitud;
import com.agrovalle.dto.response.PedidoRespuesta;
import com.agrovalle.entity.Carrito;
import com.agrovalle.entity.DetalleCarrito;
import com.agrovalle.entity.DetalleCompra;
import com.agrovalle.entity.Envio;
import com.agrovalle.entity.LoteProduccion;
import com.agrovalle.entity.OrdenCompra;
import com.agrovalle.entity.PedidoAgricultor;
import com.agrovalle.entity.Producto;
import com.agrovalle.entity.Usuario;
import com.agrovalle.enums.EstadoPedido;
import com.agrovalle.exception.CarritoVacioException;
import com.agrovalle.exception.CompradorNoEncontradoException;
import com.agrovalle.exception.TotalPedidoInvalidoException;
import com.agrovalle.repository.CarritoRepository;
import com.agrovalle.repository.DetalleCompraRepository;
import com.agrovalle.repository.EnvioRepository;
import com.agrovalle.repository.OrdenCompraRepository;
import com.agrovalle.repository.PedidoAgricultorRepository;
import com.agrovalle.repository.UsuarioRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * HU-19: convierte el carrito persistido del comprador en una orden.
 *
 * Todo ocurre en una sola transacción (todo o nada): se bloquea el carrito, se
 * delega en AsignacionLotesService el bloqueo y la validación de los lotes, se
 * crean la orden, un pedido PENDIENTE por agricultor, sus detalles, el envío
 * provisional y la reserva por lote, y se vacía el carrito.
 *
 * El rol COMPRADOR se valida en el controlador (T-19.13). Un agricultor no tiene
 * carrito, así que aquí obtendría CARRITO_VACIO.
 */
@Service
public class PedidoService {

  private static final String MENSAJE_CARRITO_VACIO =
      "El carrito está vacío. Agregue productos antes de realizar el pedido.";

  // Máximo que admite numeric(10,2)
  private static final BigDecimal TOTAL_MAXIMO = new BigDecimal("99999999.99");

  private final UsuarioRepository usuarioRepository;
  private final CarritoRepository carritoRepository;
  private final OrdenCompraRepository ordenCompraRepository;
  private final PedidoAgricultorRepository pedidoAgricultorRepository;
  private final DetalleCompraRepository detalleCompraRepository;
  private final EnvioRepository envioRepository;
  private final AsignacionLotesService asignacionLotesService;

  public PedidoService(
      UsuarioRepository usuarioRepository,
      CarritoRepository carritoRepository,
      OrdenCompraRepository ordenCompraRepository,
      PedidoAgricultorRepository pedidoAgricultorRepository,
      DetalleCompraRepository detalleCompraRepository,
      EnvioRepository envioRepository,
      AsignacionLotesService asignacionLotesService) {

    this.usuarioRepository = usuarioRepository;
    this.carritoRepository = carritoRepository;
    this.ordenCompraRepository = ordenCompraRepository;
    this.pedidoAgricultorRepository = pedidoAgricultorRepository;
    this.detalleCompraRepository = detalleCompraRepository;
    this.envioRepository = envioRepository;
    this.asignacionLotesService = asignacionLotesService;
  }

  /** Línea ya valorada del carrito: precio vigente y total de la línea. */
  private record Linea(DetalleCarrito item, BigDecimal precio, BigDecimal total) {
  }

  /**
   * @param correoComprador correo (subject del token) del comprador autenticado
   */
  @Transactional
  public PedidoRespuesta realizar(String correoComprador, RealizarPedidoSolicitud solicitud) {

    Usuario comprador = usuarioRepository.findByCorreo(correoComprador)
        .orElseThrow(() -> new CompradorNoEncontradoException(
            "El usuario autenticado no existe."));

    // El carrito se bloquea: un segundo "Realizar pedido" simultáneo espera aquí
    // y, al continuar, lo encuentra vacío.
    Carrito carrito = carritoRepository.bloquearPorComprador(comprador.getId())
        .orElseThrow(() -> new CarritoVacioException(MENSAJE_CARRITO_VACIO));

    List<DetalleCarrito> items = new ArrayList<>(carrito.getItems());
    if (items.isEmpty()) {
      throw new CarritoVacioException(MENSAJE_CARRITO_VACIO);
    }

    // Lotes bloqueados y stock revalidado (el carrito solo avisaba)
    Map<Integer, List<LoteProduccion>> lotesPorProducto =
        asignacionLotesService.bloquearYValidar(items);

    // ---- Valoración: precio vigente, total por línea y subtotal por agricultor ----
    List<Linea> lineas = new ArrayList<>();
    Map<Integer, Usuario> agricultores = new LinkedHashMap<>();
    Map<Integer, BigDecimal> subtotalPorAgricultor = new LinkedHashMap<>();
    BigDecimal totalOrden = BigDecimal.ZERO;

    for (DetalleCarrito item : items) {
      Producto producto = item.getProducto();
      BigDecimal precio = producto.getPrecio();
      BigDecimal totalLinea = precio.multiply(item.getCantidad())
          .setScale(2, RoundingMode.HALF_UP);

      lineas.add(new Linea(item, precio, totalLinea));
      totalOrden = totalOrden.add(totalLinea);

      Usuario agricultor = producto.getAgricultor();
      agricultores.putIfAbsent(agricultor.getId(), agricultor);
      subtotalPorAgricultor.merge(agricultor.getId(), totalLinea, BigDecimal::add);
    }

    if (totalOrden.compareTo(TOTAL_MAXIMO) > 0) {
      throw new TotalPedidoInvalidoException(
          "El total del pedido supera el máximo permitido. Divida la compra en varios pedidos.");
    }

    // ---- Orden ----
    OffsetTime ahoraHora = OffsetTime.now();
    LocalDateTime ahora = LocalDateTime.now();

    OrdenCompra orden = new OrdenCompra();
    orden.setComprador(comprador);
    orden.setTotal(totalOrden);
    orden.setFechaOrden(ahoraHora);
    ordenCompraRepository.save(orden);

    // ---- Un pedido PENDIENTE por agricultor, con su envío provisional ----
    Map<Integer, PedidoAgricultor> pedidos = new LinkedHashMap<>();
    List<Envio> envios = new ArrayList<>();
    String direccion = solicitud.direccionEntrega().trim();

    for (Map.Entry<Integer, BigDecimal> entrada : subtotalPorAgricultor.entrySet()) {
      PedidoAgricultor pedido = new PedidoAgricultor();
      pedido.setOrden(orden);
      pedido.setAgricultor(agricultores.get(entrada.getKey()));
      pedido.setFechaCreacion(ahora);
      pedido.setEstadoPedido(EstadoPedido.PENDIENTE);
      pedido.setSubtotalPedido(entrada.getValue());
      pedidos.put(entrada.getKey(), pedido);

      Envio envio = new Envio();
      envio.setPedido(pedido);
      envio.setDireccionEntrega(direccion);
      // Valores provisionales: el envío se cobra aparte al entregar y los datos
      // reales se completan al despachar el pedido.
      envio.setCostoEnvio(new BigDecimal("0.00"));
      envio.setFechaEnvio(LocalDate.now());
      envios.add(envio);
    }
    pedidoAgricultorRepository.saveAll(pedidos.values());
    envioRepository.saveAll(envios);

    // ---- Detalles y asignación (reserva) por lote ----
    for (Linea linea : lineas) {
      DetalleCompra detalle = new DetalleCompra();
      detalle.setOrden(orden);
      detalle.setProducto(linea.item().getProducto());
      detalle.setCantidad(linea.item().getCantidad());
      detalle.setPrecioUnitario(linea.precio());
      detalle.setTotal(linea.total());
      detalleCompraRepository.save(detalle);

      asignacionLotesService.reservar(
          detalle,
          lotesPorProducto.get(linea.item().getProducto().getIdProducto()),
          orden.getId(),
          ahoraHora);
    }

    // ---- El carrito queda vacío (orphanRemoval borra los ítems) ----
    carrito.getItems().clear();
    carrito.setFechaActualizacion(OffsetDateTime.now());
    carritoRepository.save(carrito);

    List<PedidoRespuesta.PedidoAgricultorResumen> resumen = new ArrayList<>();
    for (Map.Entry<Integer, PedidoAgricultor> entrada : pedidos.entrySet()) {
      PedidoAgricultor pedido = entrada.getValue();
      Usuario agricultor = agricultores.get(entrada.getKey());
      resumen.add(new PedidoRespuesta.PedidoAgricultorResumen(
          pedido.getId(),
          agricultor.getId(),
          agricultor.getNombre(),
          pedido.getEstadoPedido().name(),
          pedido.getSubtotalPedido()));
    }

    return new PedidoRespuesta(orden.getId(), orden.getTotal(), resumen);
  }
}
