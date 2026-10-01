package com.agrovalle.controller;

import com.agrovalle.RegistroUsuarioSolicitud;
import com.agrovalle.TipoDocumento;
import com.agrovalle.TipoUsuario;
import com.agrovalle.Usuario;
import com.agrovalle.exception.DocumentoDuplicadoException;
import com.agrovalle.service.UsuarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsuarioController.class)
@DisplayName("UsuarioController - POST /api/v1/auth/register con rol COMPRADOR (HU-11)")
class UsuarioControllerCompradorTest {

    private static final String URL = "/api/v1/auth/register";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    private static String solicitud(String documento, String rol) {
        return """
        {
          "nombre": "Restaurante El Trapiche",
          "ubicacionValle": "Cali",
          "tipoDocumento": "CC",
          "documento": "%s",
          "rol": %s
        }
        """.formatted(documento, rol);
    }

    private Usuario compradorGuardado() {
        Usuario usuario = new Usuario();
        usuario.setId(7L);
        usuario.setNombre("Restaurante El Trapiche");
        usuario.setUbicacionValle("Cali");
        usuario.setTipoDocumento(TipoDocumento.CC);
        usuario.setDocumento("5550001112");
        usuario.setRol(TipoUsuario.COMPRADOR);
        usuario.setFechaRegistro(LocalDate.now());
        return usuario;
    }

    @Test
    @DisplayName("Solicitud válida con rol COMPRADOR: responde 201 y entrega el rol al servicio")
    void solicitudValidaDebeRetornar201() throws Exception {
        when(usuarioService.registrar(any(RegistroUsuarioSolicitud.class)))
            .thenReturn(compradorGuardado());

        mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(solicitud("5550001112", "\"COMPRADOR\"")))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(7))
            .andExpect(jsonPath("$.rol").value("COMPRADOR"));

        ArgumentCaptor<RegistroUsuarioSolicitud> captor =
            ArgumentCaptor.forClass(RegistroUsuarioSolicitud.class);
        verify(usuarioService).registrar(captor.capture());
        assertEquals(TipoUsuario.COMPRADOR, captor.getValue().getRol());
    }

    @Test
    @DisplayName("Documento duplicado: responde 409 DOCUMENTO_DUPLICADO (mismo formato que HU-01)")
    void documentoDuplicadoDebeRetornar409() throws Exception {
        when(usuarioService.registrar(any(RegistroUsuarioSolicitud.class)))
            .thenThrow(new DocumentoDuplicadoException(
                "Ya existe un usuario registrado con ese documento."));

        mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(solicitud("5550001112", "\"COMPRADOR\"")))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(jsonPath("$.codigo").value("DOCUMENTO_DUPLICADO"))
            .andExpect(jsonPath("$.detalle")
                .value("Ya existe un usuario registrado con ese documento."));
    }

    @Test
    @DisplayName("Documento con guion: responde 400 y señala el campo documento")
    void documentoInvalidoDebeRetornar400() throws Exception {
        mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(solicitud("555-000", "\"COMPRADOR\"")))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.codigo").value("VALIDACION_FALLIDA"))
            .andExpect(jsonPath("$.errores[?(@.campo == 'documento')]").isNotEmpty());

        verifyNoInteractions(usuarioService);
    }

    @Test
    @DisplayName("Rol ausente (null): responde 400 y señala el campo rol")
    void rolAusenteDebeRetornar400() throws Exception {
        mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(solicitud("5550001112", "null")))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.codigo").value("VALIDACION_FALLIDA"))
            .andExpect(jsonPath("$.errores[?(@.campo == 'rol')]").isNotEmpty());

        verifyNoInteractions(usuarioService);
    }
}