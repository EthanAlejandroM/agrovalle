package com.agrovalle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.agrovalle.exception.AgricultorNoEncontradoException;
import com.agrovalle.exception.CantidadLoteInvalidaException;
import com.agrovalle.exception.CategoriaNoEncontradaException;
import com.agrovalle.exception.RolAgricultorInvalidoException;
import com.agrovalle.service.ProductoService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductoService - registro de productos con su primer lote")
class ProductoServiceTest {

  @Mock
  private ProductoRepository productoRepository;

  @Mock
  private CategoriaProductoRepository categoriaProductoRepository;

  @Mock
  private UsuarioRepository usuarioRepository;

  @Mock
  private LoteProduccionRepository loteProduccionRepository;

  @Mock
  private EstadoTrazabilidadRepository estadoTrazabilidadRepository;

  @InjectMocks
  private ProductoService productoService;

  private RegistroProductoSolicitud solicitud(
      BigDecimal producida, BigDecimal disponible) {
    RegistroProductoSolicitud solicitud = new RegistroProductoSolicitud();
    solicitud.setCategoriaId(1);
    solicitud.setAgricultorId(2);
    solicitud.setNombre("Tomate chonto");
    solicitud.setTipoProducto("Hortaliza");
    solicitud.setUnidadMedida(UnidadMedida.KILOGRAMO);
    solicitud.setPrecio(new BigDecimal("2500.50"));
    solicitud.setImagen("https://ejemplo.com/tomate.jpg");
    solicitud.setFechaCosecha(LocalDate.of(2026, 11, 1));
    solicitud.setCantidadProducida(producida);
    solicitud.setCantidadDisponible(disponible);
    solicitud.setUbicacionLote("Finca El Paraíso, Dagua");
    return solicitud;
  }

  private CategoriaProducto categoria() {
    CategoriaProducto categoria = new CategoriaProducto();
    categoria.setIdCategoria(1);
    categoria.setNombre("Hortalizas");
    return categoria;
  }

  private Usuario usuario(TipoUsuario rol) {
    Usuario usuario = new Usuario();
    usuario.setId(2);
    usuario.setNombre("Nicolle");
    usuario.setRol(rol);
    return usuario;
  }

  private void simularGuardado() {
    when(productoRepository.save(any(Producto.class))).thenAnswer(invocacion -> {
      Producto producto = invocacion.getArgument(0);
      producto.setIdProducto(10);
      return producto;
    });
    when(loteProduccionRepository.save(any(LoteProduccion.class)))
        .thenAnswer(invocacion -> invocacion.getArgument(0));
  }

  @Test
  @DisplayName("Registro válido con stock: guarda producto, lote y trazabilidad DISPONIBLE")
  void registroValidoDebeGuardarProductoLoteYTrazabilidadDisponible() {
    when(categoriaProductoRepository.findById(1)).thenReturn(Optional.of(categoria()));
    when(usuarioRepository.findById(2))
        .thenReturn(Optional.of(usuario(TipoUsuario.AGRICULTOR)));
    simularGuardado();

    Integer idProducto = productoService.registrar(
        solicitud(new BigDecimal("500"), new BigDecimal("450")));

    assertEquals(Integer.valueOf(10), idProducto);

    ArgumentCaptor<Producto> productoCaptor = ArgumentCaptor.forClass(Producto.class);
    verify(productoRepository).save(productoCaptor.capture());
    assertEquals("Tomate chonto", productoCaptor.getValue().getNombre());
    assertEquals(Integer.valueOf(2), productoCaptor.getValue().getAgricultor().getId());
    assertEquals(UnidadMedida.KILOGRAMO, productoCaptor.getValue().getUnidadMedida());

    ArgumentCaptor<LoteProduccion> loteCaptor = ArgumentCaptor.forClass(LoteProduccion.class);
    verify(loteProduccionRepository).save(loteCaptor.capture());
    assertTrue(loteCaptor.getValue().getCodigoLote().startsWith("LOT-"));
    assertEquals(Integer.valueOf(10), loteCaptor.getValue().getProducto().getIdProducto());
    assertEquals(new BigDecimal("450"), loteCaptor.getValue().getCantidadDisponible());

    ArgumentCaptor<EstadoTrazabilidad> estadoCaptor =
        ArgumentCaptor.forClass(EstadoTrazabilidad.class);
    verify(estadoTrazabilidadRepository).save(estadoCaptor.capture());
    assertEquals(EstadoLote.DISPONIBLE, estadoCaptor.getValue().getEstadoLote());
    assertTrue(estadoCaptor.getValue().getDescripcion().contains("Stock disponible"));
  }

  @Test
  @DisplayName("Categoría inexistente: lanza CategoriaNoEncontradaException y no guarda nada")
  void categoriaInexistenteDebeLanzarExcepcion() {
    when(categoriaProductoRepository.findById(1)).thenReturn(Optional.empty());

    assertThrows(CategoriaNoEncontradaException.class,
        () -> productoService.registrar(
            solicitud(new BigDecimal("500"), new BigDecimal("450"))));

    verifyNoInteractions(usuarioRepository, productoRepository,
        loteProduccionRepository, estadoTrazabilidadRepository);
  }

  @Test
  @DisplayName("Agricultor inexistente: lanza AgricultorNoEncontradoException y no guarda nada")
  void agricultorInexistenteDebeLanzarExcepcion() {
    when(categoriaProductoRepository.findById(1)).thenReturn(Optional.of(categoria()));
    when(usuarioRepository.findById(2)).thenReturn(Optional.empty());

    assertThrows(AgricultorNoEncontradoException.class,
        () -> productoService.registrar(
            solicitud(new BigDecimal("500"), new BigDecimal("450"))));

    verifyNoInteractions(productoRepository, loteProduccionRepository,
        estadoTrazabilidadRepository);
  }

  @Test
  @DisplayName("Usuario con rol COMPRADOR: lanza RolAgricultorInvalidoException")
  void usuarioCompradorDebeSerRechazado() {
    when(categoriaProductoRepository.findById(1)).thenReturn(Optional.of(categoria()));
    when(usuarioRepository.findById(2))
        .thenReturn(Optional.of(usuario(TipoUsuario.COMPRADOR)));

    assertThrows(RolAgricultorInvalidoException.class,
        () -> productoService.registrar(
            solicitud(new BigDecimal("500"), new BigDecimal("450"))));

    verifyNoInteractions(productoRepository, loteProduccionRepository,
        estadoTrazabilidadRepository);
  }

  @Test
  @DisplayName("Disponible mayor que producida: lanza CantidadLoteInvalidaException")
  void disponibleMayorQueProducidaDebeSerRechazado() {
    when(categoriaProductoRepository.findById(1)).thenReturn(Optional.of(categoria()));
    when(usuarioRepository.findById(2))
        .thenReturn(Optional.of(usuario(TipoUsuario.AGRICULTOR)));

    assertThrows(CantidadLoteInvalidaException.class,
        () -> productoService.registrar(
            solicitud(new BigDecimal("100"), new BigDecimal("150"))));

    verifyNoInteractions(productoRepository, loteProduccionRepository,
        estadoTrazabilidadRepository);
  }

  @Test
  @DisplayName("Cantidad disponible 0: lanza CantidadLoteInvalidaException y no guarda nada")
  void disponibleCeroDebeSerRechazado() {
    when(categoriaProductoRepository.findById(1)).thenReturn(Optional.of(categoria()));
    when(usuarioRepository.findById(2))
        .thenReturn(Optional.of(usuario(TipoUsuario.AGRICULTOR)));

    CantidadLoteInvalidaException ex = assertThrows(CantidadLoteInvalidaException.class,
        () -> productoService.registrar(
            solicitud(new BigDecimal("500"), BigDecimal.ZERO)));

    assertEquals("La cantidad disponible debe ser mayor que cero.", ex.getMessage());
    verifyNoInteractions(productoRepository, loteProduccionRepository,
        estadoTrazabilidadRepository);
  }

  @Test
  @DisplayName("Cantidad producida 0: lanza CantidadLoteInvalidaException y no guarda nada")
  void producidaCeroDebeSerRechazado() {
    when(categoriaProductoRepository.findById(1)).thenReturn(Optional.of(categoria()));
    when(usuarioRepository.findById(2))
        .thenReturn(Optional.of(usuario(TipoUsuario.AGRICULTOR)));

    CantidadLoteInvalidaException ex = assertThrows(CantidadLoteInvalidaException.class,
        () -> productoService.registrar(
            solicitud(BigDecimal.ZERO, new BigDecimal("10"))));

    assertEquals("La cantidad producida debe ser mayor que cero.", ex.getMessage());
    verifyNoInteractions(productoRepository, loteProduccionRepository,
        estadoTrazabilidadRepository);
  }

  @Test
  @DisplayName("Producida y disponible en 0: lanza CantidadLoteInvalidaException y no guarda nada")
  void ambasCantidadesCeroDebenSerRechazadas() {
    when(categoriaProductoRepository.findById(1)).thenReturn(Optional.of(categoria()));
    when(usuarioRepository.findById(2))
        .thenReturn(Optional.of(usuario(TipoUsuario.AGRICULTOR)));

    assertThrows(CantidadLoteInvalidaException.class,
        () -> productoService.registrar(
            solicitud(BigDecimal.ZERO, BigDecimal.ZERO)));

    verifyNoInteractions(productoRepository, loteProduccionRepository,
        estadoTrazabilidadRepository);
  }
}
