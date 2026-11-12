package com.agrovalle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.agrovalle.exception.AgricultorNoAutorizadoException;
import com.agrovalle.exception.AgricultorNoEncontradoException;
import com.agrovalle.exception.CampoNoEditableException;
import com.agrovalle.exception.CantidadLoteInvalidaException;
import com.agrovalle.exception.DatosProductoInvalidosException;
import com.agrovalle.exception.FechaCosechaInvalidaException;
import com.agrovalle.exception.LoteNoEncontradoException;
import com.agrovalle.exception.ProductoNoEncontradoException;
import com.agrovalle.exception.SolicitudSinCambiosException;
import com.agrovalle.service.ProductoService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductoService - actualización de producto y lote")
class ProductoActualizacionServiceTest {

  private static final String CORREO = "nicolle@agrovalle.com";
  private static final int ID_AGRICULTOR = 2;
  private static final int ID_PRODUCTO = 10;
  private static final int ID_LOTE = 5;

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

  private Usuario usuario(int id) {
    Usuario usuario = new Usuario();
    usuario.setId(id);
    usuario.setCorreo(CORREO);
    usuario.setRol(TipoUsuario.AGRICULTOR);
    return usuario;
  }

  private Producto producto(int idAgricultor) {
    Producto producto = new Producto();
    producto.setIdProducto(ID_PRODUCTO);
    producto.setAgricultor(usuario(idAgricultor));
    producto.setPrecio(new BigDecimal("2500.50"));
    producto.setImagen("https://ejemplo.com/original.jpg");
    return producto;
  }

  private LoteProduccion lote(String producida, String disponible) {
    LoteProduccion lote = new LoteProduccion();
    lote.setIdLote(ID_LOTE);
    lote.setProducto(producto(ID_AGRICULTOR));
    lote.setFechaCosecha(LocalDate.now().minusDays(10));
    lote.setCantidadProducida(new BigDecimal(producida));
    lote.setCantidadDisponible(new BigDecimal(disponible));
    return lote;
  }

  private void simularProductoDelUsuario() {
    when(productoRepository.findById(ID_PRODUCTO))
        .thenReturn(Optional.of(producto(ID_AGRICULTOR)));
    when(usuarioRepository.findByCorreo(CORREO)).thenReturn(Optional.of(usuario(ID_AGRICULTOR)));
  }

  private void simularLote(LoteProduccion lote, EstadoLote estado) {
    simularProductoDelUsuario();
    when(loteProduccionRepository.findById(ID_LOTE)).thenReturn(Optional.of(lote));
    EstadoTrazabilidad trazabilidad = new EstadoTrazabilidad();
    trazabilidad.setEstadoLote(estado);
    when(estadoTrazabilidadRepository
        .findFirstByLoteOrderByFechaHoraDescIdEstadoTrazabilidadDesc(lote))
        .thenReturn(Optional.of(trazabilidad));
  }

  private ActualizarLoteSolicitud solicitudLote(
      String producida, String disponible, LocalDate fecha) {
    ActualizarLoteSolicitud solicitud = new ActualizarLoteSolicitud();
    solicitud.setCantidadProducida(producida == null ? null : new BigDecimal(producida));
    solicitud.setCantidadDisponible(disponible == null ? null : new BigDecimal(disponible));
    solicitud.setFechaCosecha(fecha);
    return solicitud;
  }

  private ActualizarProductoSolicitud solicitudProducto(String precio, String imagen) {
    ActualizarProductoSolicitud solicitud = new ActualizarProductoSolicitud();
    solicitud.setPrecio(precio == null ? null : new BigDecimal(precio));
    solicitud.setImagen(imagen);
    return solicitud;
  }

  private EstadoLote actualizarLote(ActualizarLoteSolicitud solicitud) {
    return productoService.actualizarLote(ID_PRODUCTO, ID_LOTE, solicitud, CORREO);
  }

  // =====================================================================
  // PRODUCTO
  // =====================================================================

  @Nested
  @DisplayName("Actualizar producto (solo precio e imagen)")
  class ActualizarProducto {

    @Test
    @DisplayName("Precio e imagen válidos: actualiza ambos")
    void debeActualizarPrecioEImagen() {
      simularProductoDelUsuario();

      productoService.actualizarProducto(ID_PRODUCTO,
          solicitudProducto("3000", "https://ejemplo.com/nueva.jpg"), CORREO);

      ArgumentCaptor<Producto> captor = ArgumentCaptor.forClass(Producto.class);
      verify(productoRepository).save(captor.capture());
      assertEquals(new BigDecimal("3000"), captor.getValue().getPrecio());
      assertEquals("https://ejemplo.com/nueva.jpg", captor.getValue().getImagen());
    }

    @Test
    @DisplayName("Solo precio: la imagen no cambia")
    void soloPrecioNoDebeCambiarImagen() {
      simularProductoDelUsuario();

      productoService.actualizarProducto(ID_PRODUCTO, solicitudProducto("3000", null), CORREO);

      ArgumentCaptor<Producto> captor = ArgumentCaptor.forClass(Producto.class);
      verify(productoRepository).save(captor.capture());
      assertEquals(new BigDecimal("3000"), captor.getValue().getPrecio());
      assertEquals("https://ejemplo.com/original.jpg", captor.getValue().getImagen());
    }

    @Test
    @DisplayName("Solo imagen: el precio no cambia")
    void soloImagenNoDebeCambiarPrecio() {
      simularProductoDelUsuario();

      productoService.actualizarProducto(ID_PRODUCTO,
          solicitudProducto(null, "https://ejemplo.com/nueva.jpg"), CORREO);

      ArgumentCaptor<Producto> captor = ArgumentCaptor.forClass(Producto.class);
      verify(productoRepository).save(captor.capture());
      assertEquals(new BigDecimal("2500.50"), captor.getValue().getPrecio());
      assertEquals("https://ejemplo.com/nueva.jpg", captor.getValue().getImagen());
    }

    @Test
    @DisplayName("Producto inexistente: lanza ProductoNoEncontradoException")
    void productoInexistenteDebeLanzarExcepcion() {
      when(productoRepository.findById(ID_PRODUCTO)).thenReturn(Optional.empty());

      assertThrows(ProductoNoEncontradoException.class,
          () -> productoService.actualizarProducto(
              ID_PRODUCTO, solicitudProducto("3000", null), CORREO));

      verify(productoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Producto de otro agricultor: lanza AgricultorNoAutorizadoException")
    void productoDeOtroAgricultorDebeSerRechazado() {
      when(productoRepository.findById(ID_PRODUCTO)).thenReturn(Optional.of(producto(99)));
      when(usuarioRepository.findByCorreo(CORREO)).thenReturn(Optional.of(usuario(ID_AGRICULTOR)));

      assertThrows(AgricultorNoAutorizadoException.class,
          () -> productoService.actualizarProducto(
              ID_PRODUCTO, solicitudProducto("3000", null), CORREO));

      verify(productoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Usuario del token inexistente: lanza AgricultorNoEncontradoException")
    void usuarioAutenticadoInexistenteDebeLanzarExcepcion() {
      when(productoRepository.findById(ID_PRODUCTO))
          .thenReturn(Optional.of(producto(ID_AGRICULTOR)));
      when(usuarioRepository.findByCorreo(CORREO)).thenReturn(Optional.empty());

      assertThrows(AgricultorNoEncontradoException.class,
          () -> productoService.actualizarProducto(
              ID_PRODUCTO, solicitudProducto("3000", null), CORREO));

      verify(productoRepository, never()).save(any());
    }

    @ParameterizedTest(name = "Precio {0}: lanza DatosProductoInvalidosException")
    @ValueSource(strings = {"0", "-1", "-2500.50"})
    void precioNoPositivoDebeSerRechazado(String precio) {
      simularProductoDelUsuario();

      DatosProductoInvalidosException ex = assertThrows(DatosProductoInvalidosException.class,
          () -> productoService.actualizarProducto(
              ID_PRODUCTO, solicitudProducto(precio, null), CORREO));

      assertEquals("El precio debe ser mayor que cero.", ex.getMessage());
      verify(productoRepository, never()).save(any());
    }

    @ParameterizedTest(name = "Imagen \"{0}\": lanza DatosProductoInvalidosException")
    @ValueSource(strings = {"", "   "})
    void imagenEnBlancoDebeSerRechazada(String imagen) {
      simularProductoDelUsuario();

      assertThrows(DatosProductoInvalidosException.class,
          () -> productoService.actualizarProducto(
              ID_PRODUCTO, solicitudProducto(null, imagen), CORREO));

      verify(productoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Sin precio ni imagen: lanza SolicitudSinCambiosException")
    void solicitudVaciaDebeSerRechazada() {
      simularProductoDelUsuario();

      assertThrows(SolicitudSinCambiosException.class,
          () -> productoService.actualizarProducto(
              ID_PRODUCTO, solicitudProducto(null, null), CORREO));

      verify(productoRepository, never()).save(any());
    }
  }

  // =====================================================================
  // LOTE - validaciones comunes
  // =====================================================================

  @Nested
  @DisplayName("Actualizar lote - existencia y propiedad")
  class ActualizarLoteComun {

    @Test
    @DisplayName("Producto inexistente: lanza ProductoNoEncontradoException")
    void productoInexistenteDebeLanzarExcepcion() {
      when(productoRepository.findById(ID_PRODUCTO)).thenReturn(Optional.empty());

      assertThrows(ProductoNoEncontradoException.class,
          () -> actualizarLote(solicitudLote(null, "10", null)));

      verifyNoInteractions(loteProduccionRepository, estadoTrazabilidadRepository);
    }

    @Test
    @DisplayName("Producto de otro agricultor: lanza AgricultorNoAutorizadoException")
    void productoDeOtroAgricultorDebeSerRechazado() {
      when(productoRepository.findById(ID_PRODUCTO)).thenReturn(Optional.of(producto(99)));
      when(usuarioRepository.findByCorreo(CORREO)).thenReturn(Optional.of(usuario(ID_AGRICULTOR)));

      assertThrows(AgricultorNoAutorizadoException.class,
          () -> actualizarLote(solicitudLote(null, "10", null)));

      verifyNoInteractions(loteProduccionRepository, estadoTrazabilidadRepository);
    }

    @Test
    @DisplayName("Lote inexistente: lanza LoteNoEncontradoException")
    void loteInexistenteDebeLanzarExcepcion() {
      simularProductoDelUsuario();
      when(loteProduccionRepository.findById(ID_LOTE)).thenReturn(Optional.empty());

      assertThrows(LoteNoEncontradoException.class,
          () -> actualizarLote(solicitudLote(null, "10", null)));

      verify(loteProduccionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Lote que pertenece a otro producto: lanza LoteNoEncontradoException")
    void loteDeOtroProductoDebeSerRechazado() {
      simularProductoDelUsuario();
      LoteProduccion loteAjeno = lote("500", "100");
      Producto otroProducto = producto(ID_AGRICULTOR);
      otroProducto.setIdProducto(77);
      loteAjeno.setProducto(otroProducto);
      when(loteProduccionRepository.findById(ID_LOTE)).thenReturn(Optional.of(loteAjeno));

      assertThrows(LoteNoEncontradoException.class,
          () -> actualizarLote(solicitudLote(null, "10", null)));

      verify(loteProduccionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Sin ningún campo: lanza SolicitudSinCambiosException")
    void solicitudVaciaDebeSerRechazada() {
      simularProductoDelUsuario();
      when(loteProduccionRepository.findById(ID_LOTE))
          .thenReturn(Optional.of(lote("500", "100")));

      assertThrows(SolicitudSinCambiosException.class,
          () -> actualizarLote(solicitudLote(null, null, null)));

      verify(loteProduccionRepository, never()).save(any());
    }
  }

  // =====================================================================
  // LOTE en estado DISPONIBLE: solo cantidad disponible
  // =====================================================================

  @Nested
  @DisplayName("Actualizar lote DISPONIBLE (solo cantidad disponible)")
  class ActualizarLoteDisponible {

    @Test
    @DisplayName("Nueva cantidad válida: actualiza, sigue DISPONIBLE y registra trazabilidad")
    void cantidadValidaDebeActualizar() {
      LoteProduccion lote = lote("500", "450");
      simularLote(lote, EstadoLote.DISPONIBLE);

      EstadoLote estado = actualizarLote(solicitudLote(null, "200", null));

      assertEquals(EstadoLote.DISPONIBLE, estado);
      assertEquals(new BigDecimal("200"), lote.getCantidadDisponible());
      verify(loteProduccionRepository).save(lote);
      ArgumentCaptor<EstadoTrazabilidad> captor =
          ArgumentCaptor.forClass(EstadoTrazabilidad.class);
      verify(estadoTrazabilidadRepository).save(captor.capture());
      assertEquals(EstadoLote.DISPONIBLE, captor.getValue().getEstadoLote());
      assertEquals(lote, captor.getValue().getLote());
      assertEquals("Cantidad disponible actualizada manualmente de 450 a 200.",
          captor.getValue().getDescripcion());
    }

    @Test
    @DisplayName("Cantidad igual a la producida: es válida")
    void cantidadIgualAProducidaDebeSerValida() {
      LoteProduccion lote = lote("500", "450");
      simularLote(lote, EstadoLote.DISPONIBLE);

      actualizarLote(solicitudLote(null, "500", null));

      assertEquals(new BigDecimal("500"), lote.getCantidadDisponible());
      verify(estadoTrazabilidadRepository).save(any(EstadoTrazabilidad.class));
    }

    @Test
    @DisplayName("Cantidad 0: es válida y el lote pasa a AGOTADO con trazabilidad")
    void cantidadCeroDebePasarAAgotado() {
      LoteProduccion lote = lote("500", "450");
      simularLote(lote, EstadoLote.DISPONIBLE);

      EstadoLote estado = actualizarLote(solicitudLote(null, "0", null));

      assertEquals(EstadoLote.AGOTADO, estado);
      assertEquals(0, lote.getCantidadDisponible().compareTo(BigDecimal.ZERO));
      ArgumentCaptor<EstadoTrazabilidad> captor =
          ArgumentCaptor.forClass(EstadoTrazabilidad.class);
      verify(estadoTrazabilidadRepository).save(captor.capture());
      assertEquals(EstadoLote.AGOTADO, captor.getValue().getEstadoLote());
      assertEquals(lote, captor.getValue().getLote());
      assertEquals(
          "Cantidad disponible actualizada manualmente de 450 a 0. Stock agotado.",
          captor.getValue().getDescripcion());
    }

    @Test
    @DisplayName("Dos ediciones seguidas generan dos registros de trazabilidad")
    void cadaEdicionDebeRegistrarSuTrazabilidad() {
      LoteProduccion lote = lote("500", "450");
      simularLote(lote, EstadoLote.DISPONIBLE);

      actualizarLote(solicitudLote(null, "300", null));
      actualizarLote(solicitudLote(null, "250", null));

      verify(estadoTrazabilidadRepository, times(2)).save(any(EstadoTrazabilidad.class));
    }

    @Test
    @DisplayName("Cantidad mayor que la producida: lanza CantidadLoteInvalidaException")
    void cantidadMayorQueProducidaDebeSerRechazada() {
      LoteProduccion lote = lote("500", "450");
      simularLote(lote, EstadoLote.DISPONIBLE);

      CantidadLoteInvalidaException ex = assertThrows(CantidadLoteInvalidaException.class,
          () -> actualizarLote(solicitudLote(null, "501", null)));

      assertEquals("La cantidad disponible no puede superar la cantidad producida.",
          ex.getMessage());
      assertEquals(new BigDecimal("450"), lote.getCantidadDisponible());
      verify(loteProduccionRepository, never()).save(any());
      verify(estadoTrazabilidadRepository, never()).save(any());
    }

    @Test
    @DisplayName("Cantidad negativa: lanza CantidadLoteInvalidaException")
    void cantidadNegativaDebeSerRechazada() {
      LoteProduccion lote = lote("500", "450");
      simularLote(lote, EstadoLote.DISPONIBLE);

      assertThrows(CantidadLoteInvalidaException.class,
          () -> actualizarLote(solicitudLote(null, "-1", null)));

      verify(loteProduccionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Intentar cambiar la cantidad producida: lanza CampoNoEditableException")
    void cambiarProducidaDebeSerRechazado() {
      LoteProduccion lote = lote("500", "450");
      simularLote(lote, EstadoLote.DISPONIBLE);

      assertThrows(CampoNoEditableException.class,
          () -> actualizarLote(solicitudLote("600", "100", null)));

      assertEquals(new BigDecimal("500"), lote.getCantidadProducida());
      verify(loteProduccionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Intentar cambiar la fecha de cosecha: lanza CampoNoEditableException")
    void cambiarFechaDebeSerRechazado() {
      LoteProduccion lote = lote("500", "450");
      simularLote(lote, EstadoLote.DISPONIBLE);

      assertThrows(CampoNoEditableException.class,
          () -> actualizarLote(solicitudLote(null, "100", LocalDate.now())));

      verify(loteProduccionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Enviar solo la fecha (sin cantidad): lanza CampoNoEditableException")
    void soloFechaDebeSerRechazada() {
      LoteProduccion lote = lote("500", "450");
      simularLote(lote, EstadoLote.DISPONIBLE);

      assertThrows(CampoNoEditableException.class,
          () -> actualizarLote(solicitudLote(null, null, LocalDate.now())));

      verify(loteProduccionRepository, never()).save(any());
    }
  }

  // =====================================================================
  // LOTE en estado AGOTADO: producida, disponible y fecha
  // =====================================================================

  @Nested
  @DisplayName("Actualizar lote AGOTADO (producida, disponible y fecha)")
  class ActualizarLoteAgotado {

    @Test
    @DisplayName("Los tres campos válidos: actualiza y el lote pasa a DISPONIBLE con trazabilidad")
    void actualizacionCompletaDebePasarADisponible() {
      LoteProduccion lote = lote("500", "0");
      simularLote(lote, EstadoLote.AGOTADO);
      LocalDate fecha = LocalDate.now().minusDays(2);

      EstadoLote estado = actualizarLote(solicitudLote("800", "300", fecha));

      assertEquals(EstadoLote.DISPONIBLE, estado);
      assertEquals(new BigDecimal("800"), lote.getCantidadProducida());
      assertEquals(new BigDecimal("300"), lote.getCantidadDisponible());
      assertEquals(fecha, lote.getFechaCosecha());
      verify(loteProduccionRepository).save(lote);
      ArgumentCaptor<EstadoTrazabilidad> captor =
          ArgumentCaptor.forClass(EstadoTrazabilidad.class);
      verify(estadoTrazabilidadRepository).save(captor.capture());
      assertEquals(EstadoLote.DISPONIBLE, captor.getValue().getEstadoLote());
    }

    @Test
    @DisplayName("Solo disponible (≤ producida actual): actualiza y conserva producida y fecha")
    void soloDisponibleDebeConservarLoDemas() {
      LoteProduccion lote = lote("500", "0");
      LocalDate fechaOriginal = lote.getFechaCosecha();
      simularLote(lote, EstadoLote.AGOTADO);

      EstadoLote estado = actualizarLote(solicitudLote(null, "100", null));

      assertEquals(EstadoLote.DISPONIBLE, estado);
      assertEquals(new BigDecimal("500"), lote.getCantidadProducida());
      assertEquals(new BigDecimal("100"), lote.getCantidadDisponible());
      assertEquals(fechaOriginal, lote.getFechaCosecha());
    }

    @Test
    @DisplayName("Fecha de hoy: es válida (límite permitido)")
    void fechaDeHoyDebeSerValida() {
      LoteProduccion lote = lote("500", "0");
      simularLote(lote, EstadoLote.AGOTADO);

      actualizarLote(solicitudLote(null, "100", LocalDate.now()));

      assertEquals(LocalDate.now(), lote.getFechaCosecha());
    }

    @Test
    @DisplayName("Disponible igual a producida: es válido")
    void disponibleIgualAProducidaDebeSerValido() {
      LoteProduccion lote = lote("500", "0");
      simularLote(lote, EstadoLote.AGOTADO);

      actualizarLote(solicitudLote("400", "400", null));

      assertEquals(new BigDecimal("400"), lote.getCantidadDisponible());
    }

    @Test
    @DisplayName("Disponible 0: lanza CantidadLoteInvalidaException (no se acepta 0)")
    void disponibleCeroDebeSerRechazada() {
      LoteProduccion lote = lote("500", "0");
      simularLote(lote, EstadoLote.AGOTADO);

      CantidadLoteInvalidaException ex = assertThrows(CantidadLoteInvalidaException.class,
          () -> actualizarLote(solicitudLote("600", "0", null)));

      assertEquals("La cantidad disponible debe ser mayor que cero.", ex.getMessage());
      verify(loteProduccionRepository, never()).save(any());
      verify(estadoTrazabilidadRepository, never()).save(any());
    }

    @Test
    @DisplayName("Disponible negativa: lanza CantidadLoteInvalidaException")
    void disponibleNegativaDebeSerRechazada() {
      LoteProduccion lote = lote("500", "0");
      simularLote(lote, EstadoLote.AGOTADO);

      assertThrows(CantidadLoteInvalidaException.class,
          () -> actualizarLote(solicitudLote(null, "-5", null)));

      verify(loteProduccionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Sin disponible (solo producida): lanza CantidadLoteInvalidaException")
    void sinDisponibleDebeSerRechazado() {
      LoteProduccion lote = lote("500", "0");
      simularLote(lote, EstadoLote.AGOTADO);

      assertThrows(CantidadLoteInvalidaException.class,
          () -> actualizarLote(solicitudLote("600", null, null)));

      verify(loteProduccionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Disponible mayor que la nueva producida: lanza CantidadLoteInvalidaException")
    void disponibleMayorQueNuevaProducidaDebeSerRechazada() {
      LoteProduccion lote = lote("500", "0");
      simularLote(lote, EstadoLote.AGOTADO);

      CantidadLoteInvalidaException ex = assertThrows(CantidadLoteInvalidaException.class,
          () -> actualizarLote(solicitudLote("100", "150", null)));

      assertEquals("La cantidad disponible no puede superar la cantidad producida.",
          ex.getMessage());
      assertEquals(new BigDecimal("500"), lote.getCantidadProducida());
      verify(loteProduccionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Disponible mayor que la producida actual (sin enviar producida): lanza CantidadLoteInvalidaException")
    void disponibleMayorQueProducidaActualDebeSerRechazada() {
      LoteProduccion lote = lote("500", "0");
      simularLote(lote, EstadoLote.AGOTADO);

      assertThrows(CantidadLoteInvalidaException.class,
          () -> actualizarLote(solicitudLote(null, "501", null)));

      verify(loteProduccionRepository, never()).save(any());
    }

    @ParameterizedTest(name = "Producida {0}: lanza CantidadLoteInvalidaException")
    @ValueSource(strings = {"0", "-10"})
    void producidaNoPositivaDebeSerRechazada(String producida) {
      LoteProduccion lote = lote("500", "0");
      simularLote(lote, EstadoLote.AGOTADO);

      CantidadLoteInvalidaException ex = assertThrows(CantidadLoteInvalidaException.class,
          () -> actualizarLote(solicitudLote(producida, "10", null)));

      assertEquals("La cantidad producida debe ser mayor que cero.", ex.getMessage());
      verify(loteProduccionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Fecha de cosecha futura: lanza FechaCosechaInvalidaException")
    void fechaFuturaDebeSerRechazada() {
      LoteProduccion lote = lote("500", "0");
      LocalDate fechaOriginal = lote.getFechaCosecha();
      simularLote(lote, EstadoLote.AGOTADO);

      FechaCosechaInvalidaException ex = assertThrows(FechaCosechaInvalidaException.class,
          () -> actualizarLote(solicitudLote(null, "100", LocalDate.now().plusDays(1))));

      assertEquals(
          "La fecha de cosecha no puede ser posterior a la fecha de actualización del lote.",
          ex.getMessage());
      assertEquals(fechaOriginal, lote.getFechaCosecha());
      verify(loteProduccionRepository, never()).save(any());
      verify(estadoTrazabilidadRepository, never()).save(any());
    }
  }

  // =====================================================================
  // Estado derivado cuando no hay trazabilidad
  // =====================================================================

  @Test
  @DisplayName("Lote sin trazabilidad y con stock: se trata como DISPONIBLE")
  void sinTrazabilidadConStockSeTrataComoDisponible() {
    simularProductoDelUsuario();
    LoteProduccion lote = lote("500", "450");
    when(loteProduccionRepository.findById(ID_LOTE)).thenReturn(Optional.of(lote));
    when(estadoTrazabilidadRepository
        .findFirstByLoteOrderByFechaHoraDescIdEstadoTrazabilidadDesc(lote))
        .thenReturn(Optional.empty());

    assertThrows(CampoNoEditableException.class,
        () -> actualizarLote(solicitudLote("600", "100", null)));
  }
}
