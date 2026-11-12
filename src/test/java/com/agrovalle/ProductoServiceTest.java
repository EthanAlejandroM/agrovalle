package com.agrovalle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.agrovalle.exception.AgricultorNoAutorizadoException;
import com.agrovalle.exception.AgricultorNoEncontradoException;
import com.agrovalle.exception.CantidadLoteInvalidaException;
import com.agrovalle.exception.CategoriaNoEncontradaException;
import com.agrovalle.exception.FechaCosechaInvalidaException;
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

  private static final String CORREO = "nicolle@agrovalle.com";
  private static final int ID_AGRICULTOR = 2;

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

  // ---------- Datos de apoyo ----------

  private RegistroProductoSolicitud solicitud(
      BigDecimal producida, BigDecimal disponible, LocalDate fechaCosecha, int agricultorId) {
    RegistroProductoSolicitud solicitud = new RegistroProductoSolicitud();
    solicitud.setCategoriaId(1);
    solicitud.setAgricultorId(agricultorId);
    solicitud.setNombre("Tomate chonto");
    solicitud.setTipoProducto("Hortaliza");
    solicitud.setUnidadMedida(UnidadMedida.KILOGRAMO);
    solicitud.setPrecio(new BigDecimal("2500.50"));
    solicitud.setImagen("https://ejemplo.com/tomate.jpg");
    solicitud.setFechaCosecha(fechaCosecha);
    solicitud.setCantidadProducida(producida);
    solicitud.setCantidadDisponible(disponible);
    solicitud.setUbicacionLote("Finca El Paraíso, Dagua");
    return solicitud;
  }

  private RegistroProductoSolicitud solicitud(BigDecimal producida, BigDecimal disponible) {
    return solicitud(producida, disponible, LocalDate.now().minusDays(1), ID_AGRICULTOR);
  }

  private RegistroProductoSolicitud solicitudConFecha(LocalDate fechaCosecha) {
    return solicitud(new BigDecimal("500"), new BigDecimal("450"), fechaCosecha, ID_AGRICULTOR);
  }

  private CategoriaProducto categoria() {
    CategoriaProducto categoria = new CategoriaProducto();
    categoria.setIdCategoria(1);
    categoria.setNombre("Hortalizas");
    return categoria;
  }

  private Usuario usuario(TipoUsuario rol) {
    Usuario usuario = new Usuario();
    usuario.setId(ID_AGRICULTOR);
    usuario.setNombre("Nicolle");
    usuario.setCorreo(CORREO);
    usuario.setRol(rol);
    return usuario;
  }

  private void simularUsuarioAutenticado(TipoUsuario rol) {
    when(usuarioRepository.findByCorreo(CORREO)).thenReturn(Optional.of(usuario(rol)));
  }

  private void simularCategoriaExistente() {
    when(categoriaProductoRepository.findById(1)).thenReturn(Optional.of(categoria()));
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

  private void verificarQueNoSeGuardoNada() {
    verifyNoInteractions(productoRepository, loteProduccionRepository,
        estadoTrazabilidadRepository);
  }

  // ---------- Registro válido ----------

  @Test
  @DisplayName("Registro válido con stock: guarda producto, lote y trazabilidad DISPONIBLE")
  void registroValidoDebeGuardarProductoLoteYTrazabilidadDisponible() {
    simularUsuarioAutenticado(TipoUsuario.AGRICULTOR);
    simularCategoriaExistente();
    simularGuardado();

    Integer idProducto = productoService.registrar(
        solicitud(new BigDecimal("500"), new BigDecimal("450")), CORREO);

    assertEquals(Integer.valueOf(10), idProducto);

    ArgumentCaptor<Producto> productoCaptor = ArgumentCaptor.forClass(Producto.class);
    verify(productoRepository).save(productoCaptor.capture());
    assertEquals("Tomate chonto", productoCaptor.getValue().getNombre());
    assertEquals(Integer.valueOf(ID_AGRICULTOR), productoCaptor.getValue().getAgricultor().getId());
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

  // ---------- Fecha de cosecha ----------

  @Test
  @DisplayName("Fecha de cosecha de hoy: es válida (límite permitido)")
  void fechaCosechaDeHoyDebeSerValida() {
    simularUsuarioAutenticado(TipoUsuario.AGRICULTOR);
    simularCategoriaExistente();
    simularGuardado();

    Integer idProducto = productoService.registrar(solicitudConFecha(LocalDate.now()), CORREO);

    assertEquals(Integer.valueOf(10), idProducto);
  }

  @Test
  @DisplayName("Fecha de cosecha en el pasado: es válida")
  void fechaCosechaPasadaDebeSerValida() {
    simularUsuarioAutenticado(TipoUsuario.AGRICULTOR);
    simularCategoriaExistente();
    simularGuardado();

    Integer idProducto = productoService.registrar(
        solicitudConFecha(LocalDate.now().minusMonths(3)), CORREO);

    assertEquals(Integer.valueOf(10), idProducto);
  }

  @Test
  @DisplayName("Fecha de cosecha de mañana: lanza FechaCosechaInvalidaException y no guarda nada")
  void fechaCosechaDeMananaDebeSerRechazada() {
    simularUsuarioAutenticado(TipoUsuario.AGRICULTOR);
    simularCategoriaExistente();

    FechaCosechaInvalidaException ex = assertThrows(FechaCosechaInvalidaException.class,
        () -> productoService.registrar(
            solicitudConFecha(LocalDate.now().plusDays(1)), CORREO));

    assertEquals(
        "La fecha de cosecha no puede ser posterior a la fecha de creación del producto.",
        ex.getMessage());
    verificarQueNoSeGuardoNada();
  }

  @Test
  @DisplayName("Fecha de cosecha muy futura: lanza FechaCosechaInvalidaException")
  void fechaCosechaMuyFuturaDebeSerRechazada() {
    simularUsuarioAutenticado(TipoUsuario.AGRICULTOR);
    simularCategoriaExistente();

    assertThrows(FechaCosechaInvalidaException.class,
        () -> productoService.registrar(
            solicitudConFecha(LocalDate.now().plusYears(1)), CORREO));

    verificarQueNoSeGuardoNada();
  }

  // ---------- Identidad: el agricultor viene del token ----------

  @Test
  @DisplayName("agricultorId de otra persona: lanza AgricultorNoAutorizadoException y no guarda nada")
  void agricultorDistintoAlAutenticadoDebeSerRechazado() {
    simularUsuarioAutenticado(TipoUsuario.AGRICULTOR);

    AgricultorNoAutorizadoException ex = assertThrows(AgricultorNoAutorizadoException.class,
        () -> productoService.registrar(
            solicitud(new BigDecimal("500"), new BigDecimal("450"),
                LocalDate.now().minusDays(1), 99),
            CORREO));

    assertEquals("No puede registrar productos a nombre de otro agricultor.", ex.getMessage());
    verifyNoInteractions(categoriaProductoRepository);
    verificarQueNoSeGuardoNada();
  }

  @Test
  @DisplayName("Usuario del token inexistente: lanza AgricultorNoEncontradoException y no guarda nada")
  void usuarioAutenticadoInexistenteDebeLanzarExcepcion() {
    when(usuarioRepository.findByCorreo(CORREO)).thenReturn(Optional.empty());

    assertThrows(AgricultorNoEncontradoException.class,
        () -> productoService.registrar(
            solicitud(new BigDecimal("500"), new BigDecimal("450")), CORREO));

    verifyNoInteractions(categoriaProductoRepository);
    verificarQueNoSeGuardoNada();
  }

  @Test
  @DisplayName("Usuario con rol COMPRADOR: lanza RolAgricultorInvalidoException")
  void usuarioCompradorDebeSerRechazado() {
    simularUsuarioAutenticado(TipoUsuario.COMPRADOR);

    assertThrows(RolAgricultorInvalidoException.class,
        () -> productoService.registrar(
            solicitud(new BigDecimal("500"), new BigDecimal("450")), CORREO));

    verifyNoInteractions(categoriaProductoRepository);
    verificarQueNoSeGuardoNada();
  }

  // ---------- Categoría ----------

  @Test
  @DisplayName("Categoría inexistente: lanza CategoriaNoEncontradaException y no guarda nada")
  void categoriaInexistenteDebeLanzarExcepcion() {
    simularUsuarioAutenticado(TipoUsuario.AGRICULTOR);
    when(categoriaProductoRepository.findById(1)).thenReturn(Optional.empty());

    assertThrows(CategoriaNoEncontradaException.class,
        () -> productoService.registrar(
            solicitud(new BigDecimal("500"), new BigDecimal("450")), CORREO));

    verificarQueNoSeGuardoNada();
  }

  // ---------- Cantidades ----------

  @Test
  @DisplayName("Disponible mayor que producida: lanza CantidadLoteInvalidaException")
  void disponibleMayorQueProducidaDebeSerRechazado() {
    simularUsuarioAutenticado(TipoUsuario.AGRICULTOR);
    simularCategoriaExistente();

    CantidadLoteInvalidaException ex = assertThrows(CantidadLoteInvalidaException.class,
        () -> productoService.registrar(
            solicitud(new BigDecimal("100"), new BigDecimal("150")), CORREO));

    assertEquals("La cantidad disponible no puede superar la cantidad producida.",
        ex.getMessage());
    verificarQueNoSeGuardoNada();
  }

  @Test
  @DisplayName("Disponible igual a producida: es válido")
  void disponibleIgualAProducidaDebeSerValido() {
    simularUsuarioAutenticado(TipoUsuario.AGRICULTOR);
    simularCategoriaExistente();
    simularGuardado();

    Integer idProducto = productoService.registrar(
        solicitud(new BigDecimal("100"), new BigDecimal("100")), CORREO);

    assertEquals(Integer.valueOf(10), idProducto);
  }

  @Test
  @DisplayName("Cantidad disponible 0: lanza CantidadLoteInvalidaException y no guarda nada")
  void disponibleCeroDebeSerRechazado() {
    simularUsuarioAutenticado(TipoUsuario.AGRICULTOR);
    simularCategoriaExistente();

    CantidadLoteInvalidaException ex = assertThrows(CantidadLoteInvalidaException.class,
        () -> productoService.registrar(
            solicitud(new BigDecimal("500"), BigDecimal.ZERO), CORREO));

    assertEquals("La cantidad disponible debe ser mayor que cero.", ex.getMessage());
    verificarQueNoSeGuardoNada();
  }

  @Test
  @DisplayName("Cantidad producida 0: lanza CantidadLoteInvalidaException y no guarda nada")
  void producidaCeroDebeSerRechazado() {
    simularUsuarioAutenticado(TipoUsuario.AGRICULTOR);
    simularCategoriaExistente();

    CantidadLoteInvalidaException ex = assertThrows(CantidadLoteInvalidaException.class,
        () -> productoService.registrar(
            solicitud(BigDecimal.ZERO, new BigDecimal("10")), CORREO));

    assertEquals("La cantidad producida debe ser mayor que cero.", ex.getMessage());
    verificarQueNoSeGuardoNada();
  }

  @Test
  @DisplayName("Producida y disponible en 0: lanza CantidadLoteInvalidaException y no guarda nada")
  void ambasCantidadesCeroDebenSerRechazadas() {
    simularUsuarioAutenticado(TipoUsuario.AGRICULTOR);
    simularCategoriaExistente();

    assertThrows(CantidadLoteInvalidaException.class,
        () -> productoService.registrar(
            solicitud(BigDecimal.ZERO, BigDecimal.ZERO), CORREO));

    verificarQueNoSeGuardoNada();
  }
}
