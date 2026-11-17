package com.agrovalle.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.agrovalle.LoteProduccion;
import com.agrovalle.RegistroProductoSolicitud;
import com.agrovalle.exception.AgricultorNoAutorizadoException;
import com.agrovalle.exception.AgricultorNoEncontradoException;
import com.agrovalle.exception.CantidadLoteInvalidaException;
import com.agrovalle.exception.CategoriaNoEncontradaException;
import com.agrovalle.exception.FechaCosechaInvalidaException;
import com.agrovalle.exception.RolAgricultorInvalidoException;
import com.agrovalle.service.ProductoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(ProductoController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("ProductoController - POST /api/v1/productos")
class ProductoControllerTest {

  private static final String URL = "/api/v1/productos";
  private static final String CORREO = "nicolle@agrovalle.com";

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private ProductoService productoService;

  private static String solicitud(
      String nombre, String unidad, String producida, String disponible) {
    return """
        {
          "categoriaId": 1,
          "agricultorId": 2,
          "nombre": "%s",
          "tipoProducto": "Hortaliza",
          "unidadMedida": "%s",
          "precio": 2500.50,
          "imagen": "https://ejemplo.com/tomate.jpg",
          "fechaCosecha": "2026-11-01",
          "cantidadProducida": %s,
          "cantidadDisponible": %s,
          "ubicacionLote": "Finca El Paraíso, Dagua"
        }
        """.formatted(nombre, unidad, producida, disponible);
  }

  private static String solicitudValida() {
    return solicitud("Tomate chonto", "KILOGRAMO", "500", "450");
  }

  private ResultActions enviar(String cuerpo) throws Exception {
    return mockMvc.perform(post(URL)
        .principal(new UsernamePasswordAuthenticationToken(CORREO, null))
        .contentType(MediaType.APPLICATION_JSON)
        .content(cuerpo));
  }

  /** Cuerpo válido en forma de mapa, para sobrescribir o quitar campos. */
  private static Map<String, Object> cuerpoValido() {
    Map<String, Object> cuerpo = new LinkedHashMap<>();
    cuerpo.put("categoriaId", 1);
    cuerpo.put("agricultorId", 2);
    cuerpo.put("nombre", "Tomate chonto");
    cuerpo.put("tipoProducto", "Hortaliza");
    cuerpo.put("unidadMedida", "KILOGRAMO");
    cuerpo.put("precio", 2500.50);
    cuerpo.put("imagen", "https://ejemplo.com/tomate.jpg");
    cuerpo.put("fechaCosecha", "2026-11-01");
    cuerpo.put("cantidadProducida", 500);
    cuerpo.put("cantidadDisponible", 450);
    cuerpo.put("ubicacionLote", "Finca El Paraíso, Dagua");
    return cuerpo;
  }

  @Test
  @DisplayName("Solicitud válida: responde 201 Created con el id del producto")
  void solicitudValidaDebeRetornar201() throws Exception {
    when(productoService.registrar(any(RegistroProductoSolicitud.class), anyString())).thenReturn(7);

    enviar(solicitudValida())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.idProducto").value(7))
        .andExpect(jsonPath("$.mensaje")
            .value("Producto y lote registrados correctamente."));
  }

  @Test
  @DisplayName("Nombre vacío: responde 400 con VALIDACION_FALLIDA")
  void nombreVacioDebeRetornar400() throws Exception {
    enviar(solicitud("", "KILOGRAMO", "500", "450"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.codigo").value("VALIDACION_FALLIDA"))
        .andExpect(jsonPath("$.errores[?(@.campo == 'nombre')]").isNotEmpty());
  }

  @Test
  @DisplayName("Unidad de medida desconocida: responde 400 con SOLICITUD_ILEGIBLE")
  void unidadDesconocidaDebeRetornar400() throws Exception {
    enviar(solicitud("Tomate chonto", "CAJA", "500", "450"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.codigo").value("SOLICITUD_ILEGIBLE"));
  }

  @Test
  @DisplayName("Categoría inexistente: responde 404 con CATEGORIA_NO_ENCONTRADA")
  void categoriaInexistenteDebeRetornar404() throws Exception {
    when(productoService.registrar(any(RegistroProductoSolicitud.class), anyString()))
        .thenThrow(new CategoriaNoEncontradaException("La categoría seleccionada no existe."));

    enviar(solicitudValida())
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.codigo").value("CATEGORIA_NO_ENCONTRADA"));
  }

  @Test
  @DisplayName("Agricultor inexistente: responde 404 con AGRICULTOR_NO_ENCONTRADO")
  void agricultorInexistenteDebeRetornar404() throws Exception {
    when(productoService.registrar(any(RegistroProductoSolicitud.class), anyString()))
        .thenThrow(new AgricultorNoEncontradoException("El usuario seleccionado no existe."));

    enviar(solicitudValida())
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.codigo").value("AGRICULTOR_NO_ENCONTRADO"));
  }

  @Test
  @DisplayName("Usuario que no es agricultor: responde 403 con ROL_AGRICULTOR_INVALIDO")
  void usuarioNoAgricultorDebeRetornar403() throws Exception {
    when(productoService.registrar(any(RegistroProductoSolicitud.class), anyString()))
        .thenThrow(new RolAgricultorInvalidoException(
            "El usuario seleccionado no tiene el rol de AGRICULTOR."));

    enviar(solicitudValida())
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.codigo").value("ROL_AGRICULTOR_INVALIDO"));
  }

  @Test
  @DisplayName("Disponible mayor que producida: responde 400 con CANTIDAD_LOTE_INVALIDA")
  void cantidadInvalidaDebeRetornar400() throws Exception {
    when(productoService.registrar(any(RegistroProductoSolicitud.class), anyString()))
        .thenThrow(new CantidadLoteInvalidaException(
            "La cantidad disponible no puede superar la cantidad producida."));

    enviar(solicitud("Tomate chonto", "KILOGRAMO", "100", "150"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.codigo").value("CANTIDAD_LOTE_INVALIDA"));
  }

  @Test
  @DisplayName("Cantidad disponible 0: responde 400 con VALIDACION_FALLIDA")
  void disponibleCeroDebeRetornar400() throws Exception {
    enviar(solicitud("Tomate chonto", "KILOGRAMO", "500", "0"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.codigo").value("VALIDACION_FALLIDA"))
        .andExpect(jsonPath("$.errores[?(@.campo == 'cantidadDisponible')]").isNotEmpty());

    verifyNoInteractions(productoService);
  }

  @Test
  @DisplayName("Cantidad producida 0: responde 400 con VALIDACION_FALLIDA")
  void producidaCeroDebeRetornar400() throws Exception {
    enviar(solicitud("Tomate chonto", "KILOGRAMO", "0", "10"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.codigo").value("VALIDACION_FALLIDA"))
        .andExpect(jsonPath("$.errores[?(@.campo == 'cantidadProducida')]").isNotEmpty());

    verifyNoInteractions(productoService);
  }

  @Test
  @DisplayName("Cantidades negativas: responde 400 con VALIDACION_FALLIDA")
  void cantidadesNegativasDebenRetornar400() throws Exception {
    enviar(solicitud("Tomate chonto", "KILOGRAMO", "-5", "-1"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.codigo").value("VALIDACION_FALLIDA"))
        .andExpect(jsonPath("$.errores[?(@.campo == 'cantidadProducida')]").isNotEmpty())
        .andExpect(jsonPath("$.errores[?(@.campo == 'cantidadDisponible')]").isNotEmpty());

    verifyNoInteractions(productoService);
  }

  @Test
  @DisplayName("El controlador entrega al servicio el correo del usuario autenticado (token)")
  void debePasarAlServicioElCorreoDelUsuarioAutenticado() throws Exception {
    when(productoService.registrar(any(RegistroProductoSolicitud.class), anyString()))
        .thenReturn(7);

    enviar(solicitudValida()).andExpect(status().isCreated());

    verify(productoService).registrar(any(RegistroProductoSolicitud.class), eq(CORREO));
  }

  @Test
  @DisplayName("Agricultor distinto al autenticado: responde 403 con AGRICULTOR_NO_AUTORIZADO")
  void agricultorDistintoAlAutenticadoDebeRetornar403() throws Exception {
    when(productoService.registrar(any(RegistroProductoSolicitud.class), anyString()))
        .thenThrow(new AgricultorNoAutorizadoException(
            "No puede registrar productos a nombre de otro agricultor."));

    enviar(solicitudValida())
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.codigo").value("AGRICULTOR_NO_AUTORIZADO"));
  }

  @Test
  @DisplayName("Fecha de cosecha futura: responde 400 con FECHA_COSECHA_INVALIDA")
  void fechaCosechaFuturaDebeRetornar400() throws Exception {
    when(productoService.registrar(any(RegistroProductoSolicitud.class), anyString()))
        .thenThrow(new FechaCosechaInvalidaException(
            "La fecha de cosecha no puede ser posterior a la fecha de creación del producto."));

    enviar(solicitudValida())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.codigo").value("FECHA_COSECHA_INVALIDA"));
  }

  @Test
  @DisplayName("Conflicto de concurrencia sobre el lote: responde 409 con CONFLICTO_CONCURRENCIA")
  void conflictoDeConcurrenciaDebeRetornar409() throws Exception {
    when(productoService.registrar(any(RegistroProductoSolicitud.class), anyString()))
        .thenThrow(new ObjectOptimisticLockingFailureException(LoteProduccion.class, 1));

    enviar(solicitudValida())
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.codigo").value("CONFLICTO_CONCURRENCIA"));
  }

  @ParameterizedTest(name = "Campo obligatorio ausente: {0} -> 400 VALIDACION_FALLIDA")
  @ValueSource(strings = {
      "categoriaId", "agricultorId", "nombre", "tipoProducto", "unidadMedida",
      "precio", "imagen", "fechaCosecha", "cantidadProducida", "cantidadDisponible",
      "ubicacionLote"})
  void campoObligatorioAusenteDebeRetornar400(String campo) throws Exception {
    Map<String, Object> cuerpo = cuerpoValido();
    cuerpo.remove(campo);

    enviar(objectMapper.writeValueAsString(cuerpo))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.codigo").value("VALIDACION_FALLIDA"))
        .andExpect(jsonPath("$.errores[?(@.campo == '" + campo + "')]").isNotEmpty());

    verifyNoInteractions(productoService);
  }

  @ParameterizedTest(name = "Texto en blanco: {0} -> 400 VALIDACION_FALLIDA")
  @ValueSource(strings = {"tipoProducto", "imagen", "ubicacionLote"})
  void textoEnBlancoDebeRetornar400(String campo) throws Exception {
    Map<String, Object> cuerpo = cuerpoValido();
    cuerpo.put(campo, "   ");

    enviar(objectMapper.writeValueAsString(cuerpo))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.codigo").value("VALIDACION_FALLIDA"))
        .andExpect(jsonPath("$.errores[?(@.campo == '" + campo + "')]").isNotEmpty());

    verifyNoInteractions(productoService);
  }

  @ParameterizedTest(name = "Precio {0}: responde 400 con VALIDACION_FALLIDA")
  @ValueSource(doubles = {0, -10, 0.001})
  void precioNoPositivoDebeRetornar400(double precio) throws Exception {
    Map<String, Object> cuerpo = cuerpoValido();
    cuerpo.put("precio", precio);

    enviar(objectMapper.writeValueAsString(cuerpo))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.codigo").value("VALIDACION_FALLIDA"))
        .andExpect(jsonPath("$.errores[?(@.campo == 'precio')]").isNotEmpty());

    verifyNoInteractions(productoService);
  }

  @Test
  @DisplayName("Fecha de cosecha con formato inválido: responde 400 con SOLICITUD_ILEGIBLE")
  void fechaConFormatoInvalidoDebeRetornar400() throws Exception {
    Map<String, Object> cuerpo = cuerpoValido();
    cuerpo.put("fechaCosecha", "01/11/2026");

    enviar(objectMapper.writeValueAsString(cuerpo))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.codigo").value("SOLICITUD_ILEGIBLE"));

    verifyNoInteractions(productoService);
  }
}
