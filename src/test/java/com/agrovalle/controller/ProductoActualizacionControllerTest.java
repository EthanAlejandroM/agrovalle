package com.agrovalle.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.agrovalle.ActualizarLoteSolicitud;
import com.agrovalle.ActualizarProductoSolicitud;
import com.agrovalle.EstadoLote;
import com.agrovalle.exception.AgricultorNoAutorizadoException;
import com.agrovalle.exception.CampoNoEditableException;
import com.agrovalle.exception.CantidadLoteInvalidaException;
import com.agrovalle.exception.DatosProductoInvalidosException;
import com.agrovalle.exception.FechaCosechaInvalidaException;
import com.agrovalle.exception.LoteNoEncontradoException;
import com.agrovalle.exception.ProductoNoEncontradoException;
import com.agrovalle.exception.SolicitudSinCambiosException;
import com.agrovalle.service.ProductoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(ProductoController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("ProductoController - PATCH de producto y lote")
class ProductoActualizacionControllerTest {

  private static final String CORREO = "nicolle@agrovalle.com";
  private static final String URL_PRODUCTO = "/api/v1/productos/10";
  private static final String URL_LOTE = "/api/v1/productos/10/lotes/5";

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private ProductoService productoService;

  private ResultActions patchear(String url, String cuerpo) throws Exception {
    return mockMvc.perform(patch(url)
        .principal(new UsernamePasswordAuthenticationToken(CORREO, null))
        .contentType(MediaType.APPLICATION_JSON)
        .content(cuerpo));
  }

  // =====================================================================
  // PATCH /api/v1/productos/{idProducto}
  // =====================================================================

  @Test
  @DisplayName("Producto: precio e imagen válidos responden 200 y pasan el correo del token")
  void productoValidoDebeRetornar200() throws Exception {
    patchear(URL_PRODUCTO, """
        {"precio": 3000, "imagen": "https://ejemplo.com/nueva.jpg"}
        """)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.idProducto").value(10))
        .andExpect(jsonPath("$.mensaje").value("Producto actualizado correctamente."));

    verify(productoService).actualizarProducto(
        eq(10), any(ActualizarProductoSolicitud.class), eq(CORREO));
  }

  @Test
  @DisplayName("Producto: solo precio es válido a nivel de controlador")
  void productoSoloPrecioDebeRetornar200() throws Exception {
    patchear(URL_PRODUCTO, "{\"precio\": 3000}").andExpect(status().isOk());
  }

  @ParameterizedTest(name = "Producto: precio {0} responde 400 VALIDACION_FALLIDA")
  @ValueSource(strings = {"0", "-5", "0.001"})
  void productoPrecioNoPositivoDebeRetornar400(String precio) throws Exception {
    patchear(URL_PRODUCTO, "{\"precio\": " + precio + "}")
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.codigo").value("VALIDACION_FALLIDA"))
        .andExpect(jsonPath("$.errores[?(@.campo == 'precio')]").isNotEmpty());

    verifyNoInteractions(productoService);
  }

  @Test
  @DisplayName("Producto: imagen en blanco responde 400 VALIDACION_FALLIDA")
  void productoImagenEnBlancoDebeRetornar400() throws Exception {
    patchear(URL_PRODUCTO, "{\"imagen\": \"   \"}")
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.codigo").value("VALIDACION_FALLIDA"))
        .andExpect(jsonPath("$.errores[?(@.campo == 'imagen')]").isNotEmpty());

    verifyNoInteractions(productoService);
  }

  @Test
  @DisplayName("Producto: precio con texto responde 400 SOLICITUD_ILEGIBLE")
  void productoPrecioConTextoDebeRetornar400() throws Exception {
    patchear(URL_PRODUCTO, "{\"precio\": \"abc\"}")
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.codigo").value("SOLICITUD_ILEGIBLE"));
  }

  @Test
  @DisplayName("Producto inexistente: responde 404 PRODUCTO_NO_ENCONTRADO")
  void productoInexistenteDebeRetornar404() throws Exception {
    doThrow(new ProductoNoEncontradoException("El producto seleccionado no existe."))
        .when(productoService)
        .actualizarProducto(eq(10), any(ActualizarProductoSolicitud.class), anyString());

    patchear(URL_PRODUCTO, "{\"precio\": 3000}")
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.codigo").value("PRODUCTO_NO_ENCONTRADO"));
  }

  @Test
  @DisplayName("Producto de otro agricultor: responde 403 AGRICULTOR_NO_AUTORIZADO")
  void productoAjenoDebeRetornar403() throws Exception {
    doThrow(new AgricultorNoAutorizadoException("No puede modificar productos de otro agricultor."))
        .when(productoService)
        .actualizarProducto(eq(10), any(ActualizarProductoSolicitud.class), anyString());

    patchear(URL_PRODUCTO, "{\"precio\": 3000}")
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.codigo").value("AGRICULTOR_NO_AUTORIZADO"));
  }

  @Test
  @DisplayName("Producto: cuerpo sin cambios responde 400 SOLICITUD_SIN_CAMBIOS")
  void productoSinCambiosDebeRetornar400() throws Exception {
    doThrow(new SolicitudSinCambiosException("Debe enviar al menos el precio o la imagen."))
        .when(productoService)
        .actualizarProducto(eq(10), any(ActualizarProductoSolicitud.class), anyString());

    patchear(URL_PRODUCTO, "{}")
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.codigo").value("SOLICITUD_SIN_CAMBIOS"));
  }

  @Test
  @DisplayName("Producto: datos inválidos detectados por el servicio responden 400 DATOS_PRODUCTO_INVALIDOS")
  void productoDatosInvalidosDelServicioDebeRetornar400() throws Exception {
    doThrow(new DatosProductoInvalidosException("El precio debe ser mayor que cero."))
        .when(productoService)
        .actualizarProducto(eq(10), any(ActualizarProductoSolicitud.class), anyString());

    patchear(URL_PRODUCTO, "{\"precio\": 3000}")
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.codigo").value("DATOS_PRODUCTO_INVALIDOS"));
  }

  // =====================================================================
  // PATCH /api/v1/productos/{idProducto}/lotes/{idLote}
  // =====================================================================

  @Test
  @DisplayName("Lote: actualización válida responde 200 con el estado resultante")
  void loteValidoDebeRetornar200() throws Exception {
    when(productoService.actualizarLote(
        eq(10), eq(5), any(ActualizarLoteSolicitud.class), eq(CORREO)))
        .thenReturn(EstadoLote.AGOTADO);

    patchear(URL_LOTE, "{\"cantidadDisponible\": 0}")
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.idLote").value(5))
        .andExpect(jsonPath("$.estadoLote").value("AGOTADO"))
        .andExpect(jsonPath("$.mensaje").value("Lote actualizado correctamente."));
  }

  @Test
  @DisplayName("Lote: cantidad disponible negativa responde 400 VALIDACION_FALLIDA")
  void loteDisponibleNegativaDebeRetornar400() throws Exception {
    patchear(URL_LOTE, "{\"cantidadDisponible\": -1}")
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.codigo").value("VALIDACION_FALLIDA"))
        .andExpect(jsonPath("$.errores[?(@.campo == 'cantidadDisponible')]").isNotEmpty());

    verifyNoInteractions(productoService);
  }

  @ParameterizedTest(name = "Lote: cantidad producida {0} responde 400 VALIDACION_FALLIDA")
  @ValueSource(strings = {"0", "-10"})
  void loteProducidaNoPositivaDebeRetornar400(String producida) throws Exception {
    patchear(URL_LOTE, "{\"cantidadProducida\": " + producida + ", \"cantidadDisponible\": 5}")
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.codigo").value("VALIDACION_FALLIDA"))
        .andExpect(jsonPath("$.errores[?(@.campo == 'cantidadProducida')]").isNotEmpty());

    verifyNoInteractions(productoService);
  }

  @Test
  @DisplayName("Lote: fecha con formato inválido responde 400 SOLICITUD_ILEGIBLE")
  void loteFechaConFormatoInvalidoDebeRetornar400() throws Exception {
    patchear(URL_LOTE, "{\"fechaCosecha\": \"01/11/2026\", \"cantidadDisponible\": 5}")
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.codigo").value("SOLICITUD_ILEGIBLE"));

    verifyNoInteractions(productoService);
  }

  @Test
  @DisplayName("Lote inexistente: responde 404 LOTE_NO_ENCONTRADO")
  void loteInexistenteDebeRetornar404() throws Exception {
    when(productoService.actualizarLote(
        eq(10), eq(5), any(ActualizarLoteSolicitud.class), anyString()))
        .thenThrow(new LoteNoEncontradoException("El lote seleccionado no existe para este producto."));

    patchear(URL_LOTE, "{\"cantidadDisponible\": 10}")
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.codigo").value("LOTE_NO_ENCONTRADO"));
  }

  @Test
  @DisplayName("Lote de producto inexistente: responde 404 PRODUCTO_NO_ENCONTRADO")
  void loteConProductoInexistenteDebeRetornar404() throws Exception {
    when(productoService.actualizarLote(
        eq(10), eq(5), any(ActualizarLoteSolicitud.class), anyString()))
        .thenThrow(new ProductoNoEncontradoException("El producto seleccionado no existe."));

    patchear(URL_LOTE, "{\"cantidadDisponible\": 10}")
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.codigo").value("PRODUCTO_NO_ENCONTRADO"));
  }

  @Test
  @DisplayName("Lote de otro agricultor: responde 403 AGRICULTOR_NO_AUTORIZADO")
  void loteAjenoDebeRetornar403() throws Exception {
    when(productoService.actualizarLote(
        eq(10), eq(5), any(ActualizarLoteSolicitud.class), anyString()))
        .thenThrow(new AgricultorNoAutorizadoException(
            "No puede modificar productos de otro agricultor."));

    patchear(URL_LOTE, "{\"cantidadDisponible\": 10}")
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.codigo").value("AGRICULTOR_NO_AUTORIZADO"));
  }

  @Test
  @DisplayName("Lote: campo no editable en el estado actual responde 409 CAMPO_NO_EDITABLE")
  void loteCampoNoEditableDebeRetornar409() throws Exception {
    when(productoService.actualizarLote(
        eq(10), eq(5), any(ActualizarLoteSolicitud.class), anyString()))
        .thenThrow(new CampoNoEditableException(
            "Con el lote en estado DISPONIBLE solo se puede modificar la cantidad disponible."));

    patchear(URL_LOTE, "{\"cantidadProducida\": 600, \"cantidadDisponible\": 10}")
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.codigo").value("CAMPO_NO_EDITABLE"));
  }

  @Test
  @DisplayName("Lote: cantidades inválidas detectadas por el servicio responden 400 CANTIDAD_LOTE_INVALIDA")
  void loteCantidadInvalidaDebeRetornar400() throws Exception {
    when(productoService.actualizarLote(
        eq(10), eq(5), any(ActualizarLoteSolicitud.class), anyString()))
        .thenThrow(new CantidadLoteInvalidaException(
            "La cantidad disponible no puede superar la cantidad producida."));

    patchear(URL_LOTE, "{\"cantidadDisponible\": 9999}")
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.codigo").value("CANTIDAD_LOTE_INVALIDA"));
  }

  @Test
  @DisplayName("Lote: fecha futura detectada por el servicio responde 400 FECHA_COSECHA_INVALIDA")
  void loteFechaFuturaDebeRetornar400() throws Exception {
    when(productoService.actualizarLote(
        eq(10), eq(5), any(ActualizarLoteSolicitud.class), anyString()))
        .thenThrow(new FechaCosechaInvalidaException(
            "La fecha de cosecha no puede ser posterior a la fecha de actualización del lote."));

    patchear(URL_LOTE, "{\"fechaCosecha\": \"2999-01-01\", \"cantidadDisponible\": 5}")
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.codigo").value("FECHA_COSECHA_INVALIDA"));
  }

  @Test
  @DisplayName("Lote: cuerpo sin cambios responde 400 SOLICITUD_SIN_CAMBIOS")
  void loteSinCambiosDebeRetornar400() throws Exception {
    when(productoService.actualizarLote(
        eq(10), eq(5), any(ActualizarLoteSolicitud.class), anyString()))
        .thenThrow(new SolicitudSinCambiosException(
            "Debe enviar al menos un campo del lote para actualizar."));

    patchear(URL_LOTE, "{}")
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.codigo").value("SOLICITUD_SIN_CAMBIOS"));
  }
}
