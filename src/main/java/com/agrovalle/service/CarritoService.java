
package com.agrovalle.service;

import com.agrovalle.dto.request.AgregarItemSolicitud;
import com.agrovalle.dto.response.CarritoRespuesta;
import com.agrovalle.entity.Carrito;
import com.agrovalle.entity.DetalleCarrito;
import com.agrovalle.entity.Producto;
import com.agrovalle.entity.Usuario;
import com.agrovalle.enums.TipoUsuario;
import com.agrovalle.enums.UnidadMedida;
import com.agrovalle.exception.CantidadCarritoInvalidaException;
import com.agrovalle.exception.CarritoConcurrenteException;
import com.agrovalle.exception.ItemCarritoNoEncontradoException;
import com.agrovalle.exception.ProductoNoEncontradoException;
import com.agrovalle.exception.ProductoSinStockException;
import com.agrovalle.exception.RolCompradorInvalidoException;
import com.agrovalle.exception.StockInsuficienteException;
import com.agrovalle.repository.CarritoRepository;
import com.agrovalle.repository.LoteProduccionRepository;
import com.agrovalle.repository.ProductoRepository;
import com.agrovalle.repository.UsuarioRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CarritoService {

  // Inyecciones de dependecias

  private final CarritoRepository carritoRepo;
  private final ProductoRepository productoRepo;
  private final LoteProduccionRepository loteRepo;
  private final UsuarioRepository usuarioRepo;

  public CarritoService(
      CarritoRepository carritoRepo,
      ProductoRepository productoRepo,
      LoteProduccionRepository loteRepo,
      UsuarioRepository usuarioRepo) {

    this.carritoRepo = carritoRepo;
    this.productoRepo = productoRepo;
    this.loteRepo = loteRepo;
    this.usuarioRepo = usuarioRepo;
  }


  // Consultar Carrito - GET (V-18.11)
  /**
   * Devuelve el carrito del usuario.
   * Si no existe, devuelve una lista vacía y subtotal 0.
   */
  @Transactional(readOnly = true)
  public CarritoRespuesta obtener(Integer idUsuario) {

    // Solo el comprador usa el carrito.
    validarComprador(idUsuario);

    return carritoRepo.findByCompradorId(idUsuario)
        .map(this::construirRespuesta)
        .orElse(new CarritoRespuesta(
            List.of(),
            BigDecimal.ZERO.setScale(2)
        ));
  }

  // AGREGAR EL PRODUCTO AL CARRITO  (T-18.5)
  /**
   * Agrega un producto al carrito del usuario autenticado.
   * Crea el carrito si no existe.
   * Suma la cantidad si el producto ya está agregado.
   * Valida que no se supere el stock disponible.
   */
  @Transactional
  public CarritoRespuesta agregar(
      Integer idUsuario,
      AgregarItemSolicitud req) {

    // Solo el comprador usa el carrito.
    validarComprador(idUsuario);

    // V-18.3: verificar que el producto exista.
    Producto producto = productoRepo.findById(req.idProducto())
        .orElseThrow(() -> new ProductoNoEncontradoException(
            "No existe el producto con id " + req.idProducto()
        ));

    // V-18.6: Unidad y vulto  solo aceptan cantidades enteras.
    validarUnidadDiscreta(producto, req.cantidad());

    // V-18.4: verificar que el producto tenga stock.
    BigDecimal stock = stockDe(producto.getIdProducto());

    if (stock.signum() <= 0) {
      throw new ProductoSinStockException();
    }
    // Crear el carrito si todavía no existe.
    Carrito carrito = carritoRepo.findByCompradorId(idUsuario)
        .orElseGet(() -> nuevoCarrito(idUsuario));

    // Buscar si el producto ya está en el carrito.
    Optional<DetalleCarrito> existente =
        buscarItem(carrito, producto.getIdProducto());

    // Sumar la cantidad nueva con la cantidad existente.
    BigDecimal acumulada = req.cantidad().add(
        existente.map(DetalleCarrito::getCantidad)
            .orElse(BigDecimal.ZERO)
    );

    // V-18.7: verificar que la cantidad acumulada no supere el stock.
    if (acumulada.compareTo(stock) > 0) {
      throw new StockInsuficienteException(stock);
    }
    // Actualizar el producto existente o agregar uno nuevo.
    if (existente.isPresent()) {
      existente.get().setCantidad(acumulada);
    } else {
      DetalleCarrito detalle = new DetalleCarrito();
      detalle.setCarrito(carrito);
      detalle.setProducto(producto);
      detalle.setCantidad(acumulada);
      carrito.getItems().add(detalle);
    }
    // Actualizar la fecha de modificación.
    carrito.setFechaActualizacion(OffsetDateTime.now());
    // V-18.13: guardar y ejecutar el flush para detectar conflictos.
    // Si dos peticiones del mismo comprador crean el carrito o el mismo producto
    // a la vez, el UNIQUE de PostgreSQL rechaza la segunda: se responde 409.
    try {
      return construirRespuesta(guardar(carrito));
    } catch (DataIntegrityViolationException ex) {
      throw new CarritoConcurrenteException();
    }
  }

  // MODIFICAR CANTIDAD DE UN PRODUCTO (T-18.6)
  /**
   * Cambia la cantidad de un producto que ya está
   * en el carrito del usuario.
   */
  @Transactional
  public CarritoRespuesta modificarCantidad(
      Integer idUsuario,
      Integer idProducto,
      BigDecimal cantidad) {

    // Solo el comprador usa el carrito.
    validarComprador(idUsuario);

    // V-18.8: verificar que el carrito y el producto existan.
    Carrito carrito = carritoRepo.findByCompradorId(idUsuario)
        .orElseThrow(ItemCarritoNoEncontradoException::new);
    DetalleCarrito item = buscarItem(carrito, idProducto)
        .orElseThrow(ItemCarritoNoEncontradoException::new);

    // V-18.9: no permitir cantidades iguales o inferiores a cero.
    if (cantidad.signum() <= 0) {
      throw new CantidadCarritoInvalidaException(
          "Para quitar el producto use DELETE"
      );
    }

    // V-18.6: validar unidades discretas.
    validarUnidadDiscreta(item.getProducto(), cantidad);

    // V-18.7: verificar que no se supere el stock.
    BigDecimal stock = stockDe(idProducto);
    if (cantidad.compareTo(stock) > 0) {
      throw new StockInsuficienteException(stock);
    }
    // Actualizar la cantidad.
    item.setCantidad(cantidad);
    carrito.setFechaActualizacion(OffsetDateTime.now());
    return construirRespuesta(guardar(carrito));
  }

  // ELIMINAR PRODUCTO DEL CARRITO (T-18.6)
  /**
   * Elimina un producto del carrito del usuario.
   */
  @Transactional
  public CarritoRespuesta eliminarItem(
      Integer idUsuario,
      Integer idProducto) {

    // Solo el comprador usa el carrito.
    validarComprador(idUsuario);

    // V-18.8: verificar que el carrito exista.
    Carrito carrito = carritoRepo.findByCompradorId(idUsuario)
        .orElseThrow(ItemCarritoNoEncontradoException::new);

    // Buscar el producto dentro del carrito.
    DetalleCarrito item = buscarItem(carrito, idProducto)
        .orElseThrow(ItemCarritoNoEncontradoException::new);

    // Eliminar el producto.
    // orphanRemoval se encarga de eliminarlo de la BD.
    carrito.getItems().remove(item);
    carrito.setFechaActualizacion(OffsetDateTime.now());
    return construirRespuesta(guardar(carrito));
  }

  // CONSTRUIR RESPUESTA DEL CARRITO (T-18.7)
  /**
   * Construye la respuesta del carrito con sus subtotales.
   * Utiliza el precio vigente del producto.
   * El precio nunca es recibido desde el cliente.
   */
  private CarritoRespuesta construirRespuesta(Carrito carrito) {

    // Obtener los identificadores de todos los productos.
    List<Integer> ids = carrito.getItems().stream()
        .map(item -> item.getProducto().getIdProducto())
        .toList();

    // Consultar el stock de todos los productos.
    Map<Integer, BigDecimal> stock = loteRepo.mapaStock(ids);
    List<CarritoRespuesta.ItemCarrito> items = new ArrayList<>();
    BigDecimal total = BigDecimal.ZERO;

    // Recorrer los productos del carrito.
    for (DetalleCarrito detalle : carrito.getItems()) {
      Producto producto = detalle.getProducto();

      // Calcular subtotal: precio vigente por cantidad.
      BigDecimal subtotal = producto.getPrecio()
          .multiply(detalle.getCantidad())
          .setScale(2, RoundingMode.HALF_UP);

      // Obtener stock disponible.
      // Si no existe información de stock, se devuelve cero.
      BigDecimal disponible = stock.getOrDefault(
          producto.getIdProducto(),
          BigDecimal.ZERO
      );

      // V-18.12: verificar si el stock es suficiente.
      boolean suficiente = disponible.compareTo(
          detalle.getCantidad()
      ) >= 0;

      // Construir el detalle de respuesta.
      items.add(new CarritoRespuesta.ItemCarrito(
          producto.getIdProducto(),
          producto.getNombre(),
          producto.getUnidadMedida().name(),
          producto.getPrecio(),
          detalle.getCantidad(),
          subtotal,
          disponible,
          suficiente
      ));

      // Acumular el total del carrito.
      total = total.add(subtotal);
    }

    // Devolver la respuesta con el total calculado.
    return new CarritoRespuesta(
        items,
        total.setScale(2, RoundingMode.HALF_UP)
    );
  }


  // MÉTODOS AUXILIARES
  /**
   * Verifica que el usuario autenticado sea COMPRADOR.
   * Los agricultores solo pueden ver el catálogo, no comprar.
   */
  private void validarComprador(Integer idUsuario) {

    Usuario usuario = usuarioRepo.findById(idUsuario)
        .orElseThrow(RolCompradorInvalidoException::new);

    if (usuario.getRol() != TipoUsuario.COMPRADOR) {
      throw new RolCompradorInvalidoException();
    }
  }
  /**
   * Consulta el stock disponible de un producto.
   */
  private BigDecimal stockDe(Integer idProducto) {

    return loteRepo.mapaStock(List.of(idProducto))
        .getOrDefault(idProducto, BigDecimal.ZERO);
  }
  /**
   * Busca un producto dentro del carrito.
   */
  private Optional<DetalleCarrito> buscarItem(
      Carrito carrito,
      Integer idProducto) {

    return carrito.getItems().stream()
        .filter(item -> item.getProducto()
            .getIdProducto()
            .equals(idProducto))
        .findFirst();
  }
  /**
   * Crea un carrito nuevo para el usuario.
   */
  private Carrito nuevoCarrito(Integer idUsuario) {

    Usuario comprador = usuarioRepo.findById(idUsuario)
        .orElseThrow(() -> new IllegalStateException(
            "Usuario no encontrado"
        ));

    Carrito carrito = new Carrito();

    carrito.setComprador(comprador);
    carrito.setFechaCreacion(OffsetDateTime.now());
    carrito.setFechaActualizacion(OffsetDateTime.now());

    return carrito;
  }
  /**
   * Valida que los productos con unidad UNIDAD o BULTO
   * solamente acepten cantidades enteras.
   */
  private void validarUnidadDiscreta(
      Producto producto,
      BigDecimal cantidad) {

    boolean discreta =
        producto.getUnidadMedida() == UnidadMedida.UNIDAD
            || producto.getUnidadMedida() == UnidadMedida.BULTO;

    if (discreta && cantidad.stripTrailingZeros().scale() > 0) {

      throw new CantidadCarritoInvalidaException(
          "Este producto solo admite cantidades enteras"
      );
    }
  }
  /**
   * Guarda el carrito y hace flush para detectar aquí los choques de
   * concurrencia: UNIQUE de PostgreSQL o @Version. Responde 409.
   */
  private Carrito guardar(Carrito carrito) {
    try {
      return carritoRepo.saveAndFlush(carrito);
    } catch (DataIntegrityViolationException
             | OptimisticLockingFailureException ex) {
      throw new CarritoConcurrenteException();
    }
  }
}

