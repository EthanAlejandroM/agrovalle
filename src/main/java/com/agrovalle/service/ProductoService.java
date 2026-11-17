
package com.agrovalle.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agrovalle.ActualizarLoteSolicitud;
import com.agrovalle.ActualizarProductoSolicitud;
import com.agrovalle.exception.AgricultorNoAutorizadoException;
import com.agrovalle.exception.CampoNoEditableException;
import com.agrovalle.exception.DatosProductoInvalidosException;
import com.agrovalle.exception.LoteNoEncontradoException;
import com.agrovalle.exception.ProductoNoEncontradoException;
import com.agrovalle.exception.SolicitudSinCambiosException;
import com.agrovalle.exception.AgricultorNoEncontradoException;
import com.agrovalle.exception.CantidadLoteInvalidaException;
import com.agrovalle.exception.CategoriaNoEncontradaException;
import com.agrovalle.exception.FechaCosechaInvalidaException;
import com.agrovalle.EstadoLote;
import com.agrovalle.EstadoTrazabilidad;
import com.agrovalle.EstadoTrazabilidadRepository;
import com.agrovalle.LoteProduccion;
import com.agrovalle.LoteProduccionRepository;
import com.agrovalle.Producto;
import com.agrovalle.ProductoRepository;
import com.agrovalle.RegistroProductoSolicitud;
import com.agrovalle.exception.RolAgricultorInvalidoException;
import com.agrovalle.TipoUsuario;
import com.agrovalle.Usuario;
import com.agrovalle.UsuarioRepository;
import com.agrovalle.CategoriaProductoRepository;

@Service
public class ProductoService {

  private final ProductoRepository productoRepository;
  private final CategoriaProductoRepository categoriaProductoRepository;
  private final UsuarioRepository usuarioRepository;
  private final LoteProduccionRepository loteProduccionRepository;
  private final EstadoTrazabilidadRepository estadoTrazabilidadRepository;

  public ProductoService(
      ProductoRepository productoRepository,
      CategoriaProductoRepository categoriaProductoRepository,
      UsuarioRepository usuarioRepository,
      LoteProduccionRepository loteProduccionRepository,
      EstadoTrazabilidadRepository estadoTrazabilidadRepository) {

    this.productoRepository = productoRepository;
    this.categoriaProductoRepository = categoriaProductoRepository;
    this.usuarioRepository = usuarioRepository;
    this.loteProduccionRepository = loteProduccionRepository;
    this.estadoTrazabilidadRepository = estadoTrazabilidadRepository;
  }

  /**
   * Registra un producto con su primer lote.
   *
   * @param solicitud       datos del producto y del lote
   * @param correoAutenticado correo (subject del token) del usuario que hace la
   *                        petición; el agricultor del cuerpo debe ser esa
   *                        misma persona
   */
  @Transactional
  public Integer registrar(RegistroProductoSolicitud solicitud, String correoAutenticado) {

    // El agricultor se identifica por el token, no por el cuerpo.
    Usuario agricultor = usuarioRepository.findByCorreo(correoAutenticado)
        .orElseThrow(() -> new AgricultorNoEncontradoException(
            "El usuario autenticado no existe."));

    // El agricultorId del cuerpo debe coincidir con quien inició sesión.
    if (!agricultor.getId().equals(solicitud.getAgricultorId())) {
      throw new AgricultorNoAutorizadoException(
          "No puede registrar productos a nombre de otro agricultor.");
    }

    // Verificar que el usuario tenga rol AGRICULTOR
    if (agricultor.getRol() != TipoUsuario.AGRICULTOR) {
      throw new RolAgricultorInvalidoException(
          "El usuario seleccionado no tiene el rol de AGRICULTOR.");
    }

    // Validar que la categoría exista
    var categoria = categoriaProductoRepository.findById(
        solicitud.getCategoriaId())
        .orElseThrow(() -> new CategoriaNoEncontradaException(
            "La categoría seleccionada no existe."));

    // La cosecha no puede ser posterior al día en que se crea el producto
    if (solicitud.getFechaCosecha().isAfter(LocalDate.now())) {
      throw new FechaCosechaInvalidaException(
          "La fecha de cosecha no puede ser posterior a la fecha de creación del producto.");
    }

    // Validar las cantidades del lote
    if (solicitud.getCantidadProducida().compareTo(BigDecimal.ZERO) <= 0) {
      throw new CantidadLoteInvalidaException(
          "La cantidad producida debe ser mayor que cero.");
    }

    if (solicitud.getCantidadDisponible().compareTo(BigDecimal.ZERO) <= 0) {
      throw new CantidadLoteInvalidaException(
          "La cantidad disponible debe ser mayor que cero.");
    }

    if (solicitud.getCantidadDisponible().compareTo(
        solicitud.getCantidadProducida()) > 0) {

      throw new CantidadLoteInvalidaException(
          "La cantidad disponible no puede superar la cantidad producida.");
    }

    // Construir el producto
    Producto producto = new Producto();

    producto.setCategoria(categoria);
    producto.setAgricultor(agricultor);
    producto.setNombre(solicitud.getNombre());
    producto.setTipoProducto(solicitud.getTipoProducto());
    producto.setUnidadMedida(solicitud.getUnidadMedida());
    producto.setPrecio(solicitud.getPrecio());
    producto.setImagen(solicitud.getImagen());

    // Guardar el producto
    Producto productoGuardado = productoRepository.save(producto);

    // Construir el primer lote
    LoteProduccion lote = new LoteProduccion();

    lote.setCodigoLote(generarCodigoLote());
    lote.setProducto(productoGuardado);
    lote.setFechaCosecha(solicitud.getFechaCosecha());
    lote.setCantidadProducida(solicitud.getCantidadProducida());
    lote.setCantidadDisponible(solicitud.getCantidadDisponible());
    lote.setUbicacionLote(solicitud.getUbicacionLote());

    // Guardar el lote
    LoteProduccion loteGuardado = loteProduccionRepository.save(lote);

  
    // Crear el primer registro de trazabilidad
    // Como las cantidades siempre son mayores que cero, el lote nace DISPONIBLE.
    EstadoTrazabilidad trazabilidad = new EstadoTrazabilidad();

    trazabilidad.setLote(loteGuardado);
    trazabilidad.setFechaHora(OffsetDateTime.now());
    trazabilidad.setEstadoLote(EstadoLote.DISPONIBLE);
    trazabilidad.setDescripcion(
        "Registro inicial del lote. Stock disponible.");
    estadoTrazabilidadRepository.save(trazabilidad);

    // Devolver el identificador del producto registrado
    return productoGuardado.getIdProducto();
  }

  // ------------------------------------------------------------------
  // Actualización de producto
  // ------------------------------------------------------------------

  /**
   * Actualiza el precio y/o la imagen de un producto. Solo su dueño puede
   * hacerlo.
   */
  @Transactional
  public void actualizarProducto(
      Integer idProducto, ActualizarProductoSolicitud solicitud, String correoAutenticado) {

    Producto producto = buscarProductoDelUsuario(idProducto, correoAutenticado);

    if (solicitud.getPrecio() == null && solicitud.getImagen() == null) {
      throw new SolicitudSinCambiosException(
          "Debe enviar al menos el precio o la imagen para actualizar.");
    }

    if (solicitud.getPrecio() != null) {
      if (solicitud.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
        throw new DatosProductoInvalidosException("El precio debe ser mayor que cero.");
      }
      producto.setPrecio(solicitud.getPrecio());
    }

    if (solicitud.getImagen() != null) {
      if (solicitud.getImagen().isBlank()) {
        throw new DatosProductoInvalidosException("La imagen no puede estar vacía.");
      }
      producto.setImagen(solicitud.getImagen());
    }

    productoRepository.save(producto);
  }

  // ------------------------------------------------------------------
  // Actualización de lote
  // ------------------------------------------------------------------

  /**
   * Actualiza un lote según su estado actual:
   * <ul>
   * <li>DISPONIBLE: solo cantidadDisponible (0 permitido, no puede superar la
   * producida). Si queda en 0 el lote pasa a AGOTADO. Cada edición registra
   * trazabilidad.</li>
   * <li>AGOTADO: cantidadProducida, cantidadDisponible (&gt; 0, no puede superar
   * la producida) y fechaCosecha (no posterior a hoy). Si se reabastece el lote
   * pasa a DISPONIBLE.</li>
   * </ul>
   *
   * @return el estado en que queda el lote
   */
  @Transactional
  public EstadoLote actualizarLote(
      Integer idProducto,
      Integer idLote,
      ActualizarLoteSolicitud solicitud,
      String correoAutenticado) {

    buscarProductoDelUsuario(idProducto, correoAutenticado);

    LoteProduccion lote = loteProduccionRepository.findById(idLote)
        .filter(l -> l.getProducto() != null
            && idProducto.equals(l.getProducto().getIdProducto()))
        .orElseThrow(() -> new LoteNoEncontradoException(
            "El lote seleccionado no existe para este producto."));

    if (solicitud.getCantidadDisponible() == null
        && solicitud.getCantidadProducida() == null
        && solicitud.getFechaCosecha() == null) {
      throw new SolicitudSinCambiosException(
          "Debe enviar al menos un campo del lote para actualizar.");
    }

    EstadoLote estadoActual = estadoTrazabilidadRepository
        .findFirstByLoteOrderByFechaHoraDescIdEstadoTrazabilidadDesc(lote)
        .map(EstadoTrazabilidad::getEstadoLote)
        .orElseGet(() -> lote.getCantidadDisponible().compareTo(BigDecimal.ZERO) > 0
            ? EstadoLote.DISPONIBLE
            : EstadoLote.AGOTADO);

    if (estadoActual == EstadoLote.DISPONIBLE) {
      return actualizarLoteDisponible(lote, solicitud);
    }
    return actualizarLoteAgotado(lote, solicitud);
  }

  private EstadoLote actualizarLoteDisponible(
      LoteProduccion lote, ActualizarLoteSolicitud solicitud) {

    if (solicitud.getCantidadProducida() != null || solicitud.getFechaCosecha() != null) {
      throw new CampoNoEditableException(
          "Con el lote en estado DISPONIBLE solo se puede modificar la cantidad disponible.");
    }

    BigDecimal nuevaDisponible = solicitud.getCantidadDisponible();

    if (nuevaDisponible.compareTo(BigDecimal.ZERO) < 0) {
      throw new CantidadLoteInvalidaException(
          "La cantidad disponible no puede ser negativa.");
    }

    if (nuevaDisponible.compareTo(lote.getCantidadProducida()) > 0) {
      throw new CantidadLoteInvalidaException(
          "La cantidad disponible no puede superar la cantidad producida.");
    }

    BigDecimal disponibleAnterior = lote.getCantidadDisponible();
    lote.setCantidadDisponible(nuevaDisponible);
    loteProduccionRepository.save(lote);

    // Cada edición de cantidad deja un registro de trazabilidad.
    String detalle = "Cantidad disponible actualizada manualmente de "
        + disponibleAnterior.toPlainString() + " a " + nuevaDisponible.toPlainString() + ".";

    if (nuevaDisponible.compareTo(BigDecimal.ZERO) == 0) {
      registrarTrazabilidad(lote, EstadoLote.AGOTADO, detalle + " Stock agotado.");
      return EstadoLote.AGOTADO;
    }
    registrarTrazabilidad(lote, EstadoLote.DISPONIBLE, detalle);
    return EstadoLote.DISPONIBLE;
  }

  private EstadoLote actualizarLoteAgotado(
      LoteProduccion lote, ActualizarLoteSolicitud solicitud) {

    BigDecimal nuevaDisponible = solicitud.getCantidadDisponible();
    BigDecimal nuevaProducida = solicitud.getCantidadProducida();

    if (nuevaDisponible == null) {
      throw new CantidadLoteInvalidaException(
          "La cantidad disponible es obligatoria para reabastecer un lote AGOTADO.");
    }

    if (nuevaDisponible.compareTo(BigDecimal.ZERO) <= 0) {
      throw new CantidadLoteInvalidaException(
          "La cantidad disponible debe ser mayor que cero.");
    }

    if (nuevaProducida != null && nuevaProducida.compareTo(BigDecimal.ZERO) <= 0) {
      throw new CantidadLoteInvalidaException(
          "La cantidad producida debe ser mayor que cero.");
    }

    BigDecimal producidaFinal = nuevaProducida != null
        ? nuevaProducida
        : lote.getCantidadProducida();

    if (nuevaDisponible.compareTo(producidaFinal) > 0) {
      throw new CantidadLoteInvalidaException(
          "La cantidad disponible no puede superar la cantidad producida.");
    }

    if (solicitud.getFechaCosecha() != null
        && solicitud.getFechaCosecha().isAfter(LocalDate.now())) {
      throw new FechaCosechaInvalidaException(
          "La fecha de cosecha no puede ser posterior a la fecha de actualización del lote.");
    }

    if (nuevaProducida != null) {
      lote.setCantidadProducida(nuevaProducida);
    }
    if (solicitud.getFechaCosecha() != null) {
      lote.setFechaCosecha(solicitud.getFechaCosecha());
    }
    BigDecimal disponibleAnterior = lote.getCantidadDisponible();
    lote.setCantidadDisponible(nuevaDisponible);
    loteProduccionRepository.save(lote);

    registrarTrazabilidad(lote, EstadoLote.DISPONIBLE,
        "Lote reabastecido. Cantidad disponible de "
            + disponibleAnterior.toPlainString() + " a " + nuevaDisponible.toPlainString()
            + ". Stock disponible.");
    return EstadoLote.DISPONIBLE;
  }

  // ------------------------------------------------------------------
  // Apoyo
  // ------------------------------------------------------------------

  /**
   * Busca el producto y comprueba que pertenezca al usuario autenticado.
   * Primero se valida la existencia (404) y luego la propiedad (403).
   */
  private Producto buscarProductoDelUsuario(Integer idProducto, String correoAutenticado) {
    Producto producto = productoRepository.findById(idProducto)
        .orElseThrow(() -> new ProductoNoEncontradoException(
            "El producto seleccionado no existe."));

    Usuario usuario = usuarioRepository.findByCorreo(correoAutenticado)
        .orElseThrow(() -> new AgricultorNoEncontradoException(
            "El usuario autenticado no existe."));

    if (!usuario.getId().equals(producto.getAgricultor().getId())) {
      throw new AgricultorNoAutorizadoException(
          "No puede modificar productos de otro agricultor.");
    }
    return producto;
  }

  private void registrarTrazabilidad(
      LoteProduccion lote, EstadoLote estado, String descripcion) {
    EstadoTrazabilidad trazabilidad = new EstadoTrazabilidad();
    trazabilidad.setLote(lote);
    trazabilidad.setFechaHora(OffsetDateTime.now());
    trazabilidad.setEstadoLote(estado);
    trazabilidad.setDescripcion(descripcion);
    estadoTrazabilidadRepository.save(trazabilidad);
  }

  private String generarCodigoLote() {
    return "LOT-" + UUID.randomUUID()
        .toString()
        .replace("-", "")
        .toUpperCase();
  }
}
