package com.agrovalle.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.agrovalle.RegistroUsuarioSolicitud;
import com.agrovalle.TipoDocumento;
import com.agrovalle.TipoUsuario;
import com.agrovalle.Usuario;
import com.agrovalle.exception.DocumentoDuplicadoException;
import com.agrovalle.service.UsuarioService;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UsuarioController.class)
@DisplayName("UsuarioController - POST /api/v1/auth/register (HU-01)")
class UsuarioControllerTest {

    private static final String URL = "/api/v1/auth/register";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    private static String solicitud(String nombre, String documento) {
        return """
            {
            "nombre": "%s",
            "ubicacionValle": "Dagua",
            "tipoDocumento": "CC",
            "documento": "%s",
            "rol": "AGRICULTOR"
            }
            """.formatted(nombre, documento);
    }

    private Usuario usuarioGuardado() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Ethan");
        usuario.setUbicacionValle("Dagua");
        usuario.setTipoDocumento(TipoDocumento.CC);
        usuario.setDocumento("1234567890");
        usuario.setRol(TipoUsuario.AGRICULTOR);
        usuario.setFechaRegistro(LocalDate.now());
        return usuario;
    }

    @Test
    @DisplayName("Solicitud válida: responde 201 Created con el id del usuario")
    void solicitudValidaDebeRetornar201() throws Exception {
        when(usuarioService.registrar(any(RegistroUsuarioSolicitud.class)))
            .thenReturn(usuarioGuardado());

        mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(solicitud("Ethan", "1234567890")))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.rol").value("AGRICULTOR"));
    }

    @Test
    @DisplayName("El campo ubicacion_valle del backlog se acepta como alias de ubicacionValle")
    void aliasUbicacionValleDebeSerAceptado() throws Exception {
        when(usuarioService.registrar(any(RegistroUsuarioSolicitud.class)))
            .thenReturn(usuarioGuardado());

        String json = """
            {
            "nombre": "Ethan",
            "ubicacion_valle": "Dagua",
            "tipoDocumento": "CC",
            "documento": "1234567890",
            "rol": "AGRICULTOR"
            }
            """;

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isCreated());

        ArgumentCaptor<RegistroUsuarioSolicitud> captor =
            ArgumentCaptor.forClass(RegistroUsuarioSolicitud.class);
        verify(usuarioService).registrar(captor.capture());
        assertEquals("Dagua", captor.getValue().getUbicacionValle());
    }

    @Test
    @DisplayName("Sin cuerpo: responde 400 SOLICITUD_ILEGIBLE")
    void solicitudSinCuerpoDebeRetornar400() throws Exception {
        mockMvc.perform(post(URL))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.codigo").value("SOLICITUD_ILEGIBLE"));

        verifyNoInteractions(usuarioService);
    }

    @Test
    @DisplayName("JSON vacío: responde 400 VALIDACION_FALLIDA con los 5 campos faltantes")
    void camposFaltantesDebenRetornar400ConDetallePorCampo() throws Exception {
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.codigo").value("VALIDACION_FALLIDA"))
            .andExpect(jsonPath("$.detalle").isNotEmpty())
            .andExpect(jsonPath("$.errores", hasSize(5)));

        verifyNoInteractions(usuarioService);
    }

    @Test
    @DisplayName("Documento con guion: responde 400 y señala el campo documento")
    void documentoInvalidoDebeRetornar400() throws Exception {
        mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(solicitud("Nicolle", "123-456")))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.codigo").value("VALIDACION_FALLIDA"))
            .andExpect(jsonPath("$.errores[?(@.campo == 'documento')]").isNotEmpty());

        verifyNoInteractions(usuarioService);
    }

    @Test
    @DisplayName("Nombre vacío: responde 400 y señala el campo nombre")
    void nombreVacioDebeRetornar400() throws Exception {
        mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(solicitud("", "1234567890")))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errores[?(@.campo == 'nombre')]").isNotEmpty());
    }

    @Test
    @DisplayName("Rol inexistente: responde 400 SOLICITUD_ILEGIBLE")
    void rolInexistenteDebeRetornar400() throws Exception {
        String json = solicitud("Ethan", "1234567890").replace("AGRICULTOR", "ADMIN");

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.codigo").value("SOLICITUD_ILEGIBLE"));
    }

    @Test
    @DisplayName("Servicio lanza DocumentoDuplicadoException: responde 409 DOCUMENTO_DUPLICADO")
    void documentoDuplicadoDebeRetornar409() throws Exception {
        when(usuarioService.registrar(any(RegistroUsuarioSolicitud.class)))
            .thenThrow(new DocumentoDuplicadoException(
                "Ya existe un usuario registrado con ese documento."));

        mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(solicitud("Bairon", "999999999")))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(jsonPath("$.codigo").value("DOCUMENTO_DUPLICADO"))
            .andExpect(jsonPath("$.detalle")
                .value("Ya existe un usuario registrado con ese documento."));
    }
}
