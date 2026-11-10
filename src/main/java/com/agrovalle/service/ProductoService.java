
package com.agrovalle.service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agrovalle.exception.AgricultorNoEncontradoException;
import com.agrovalle.exception.CantidadLoteInvalidaException;
import com.agrovalle.exception.CategoriaNoEncontradaException;
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

  @Transactional
  public Integer registrar(RegistroProductoSolicitud solicitud) {

    // Validar que la categoría exista
    var categoria = categoriaProductoRepository.findById(
        solicitud.getCategoriaId())
        .orElseThrow(() -> new CategoriaNoEncontradaException(
            "La categoría seleccionada no existe."));

    // Buscar al agricultor
    Usuario agricultor = usuarioRepository.findById(
        solicitud.getAgricultorId())
        .orElseThrow(() -> new AgricultorNoEncontradoException(
            "El usuario seleccionado no existe."));

    // Verificar que el usuario tenga rol AGRICULTOR
    if (agricultor.getRol() != TipoUsuario.AGRICULTOR) {
      throw new RolAgricultorInvalidoException(
          "El usuario seleccionado no tiene el rol de AGRICULTOR.");
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

  private String generarCodigoLote() {
    return "LOT-" + UUID.randomUUID()
        .toString()
        .replace("-", "")
        .toUpperCase();
  }
}
