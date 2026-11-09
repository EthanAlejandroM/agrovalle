package com.agrovalle.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.agrovalle.AuthResponseSolicitud;
import com.agrovalle.InicioSesionSolicitud;
import com.agrovalle.service.AuthService;
import com.agrovalle.service.UsuarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(UsuarioController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("UsuarioController - POST /api/v1/auth/login")
class UsuarioControllerLoginTest {

  private static final String URL = "/api/v1/auth/login";

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private UsuarioService usuarioService;

  @MockitoBean
  private AuthService authService;

  private static String solicitud(String correo, String contrasena) {
    return """
        {
          "correo": "%s",
          "contrasena": "%s"
        }
        """.formatted(correo, contrasena);
  }

  private ResultActions enviar(String cuerpo) throws Exception {
    return mockMvc.perform(post(URL)
        .contentType(MediaType.APPLICATION_JSON)
        .content(cuerpo));
  }

  @Test
  @DisplayName("Credenciales correctas: responde 200 con el token Bearer")
  void credencialesCorrectasDebenRetornar200() throws Exception {
    when(authService.iniciarSesion(any(InicioSesionSolicitud.class)))
        .thenReturn(new AuthResponseSolicitud("token-jwt", 3600000L));

    enviar(solicitud("ana@agrovalle.test", "Clave12345"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").value("token-jwt"))
        .andExpect(jsonPath("$.tokenType").value("Bearer"))
        .andExpect(jsonPath("$.expiresIn").value(3600000));
  }

  @Test
  @DisplayName("Credenciales incorrectas: responde 401 con CREDENCIALES_INVALIDAS")
  void credencialesIncorrectasDebenRetornar401() throws Exception {
    when(authService.iniciarSesion(any(InicioSesionSolicitud.class)))
        .thenThrow(new BadCredentialsException("Correo o contraseña incorrectos."));

    enviar(solicitud("ana@agrovalle.test", "ClaveIncorrecta"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.status").value(401))
        .andExpect(jsonPath("$.codigo").value("CREDENCIALES_INVALIDAS"))
        .andExpect(jsonPath("$.detalle").value("Correo o contraseña incorrectos."));
  }

  @Test
  @DisplayName("Correo con formato inválido: responde 400 con VALIDACION_FALLIDA")
  void correoInvalidoDebeRetornar400() throws Exception {
    enviar(solicitud("no-es-correo", "Clave12345"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.codigo").value("VALIDACION_FALLIDA"))
        .andExpect(jsonPath("$.errores[?(@.campo == 'correo')]").isNotEmpty());
  }

  @Test
  @DisplayName("Contraseña vacía: responde 400 con VALIDACION_FALLIDA")
  void contrasenaVaciaDebeRetornar400() throws Exception {
    enviar(solicitud("ana@agrovalle.test", ""))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.codigo").value("VALIDACION_FALLIDA"))
        .andExpect(jsonPath("$.errores[?(@.campo == 'contrasena')]").isNotEmpty());
  }

  @Test
  @DisplayName("Cuerpo que no es JSON válido: responde 400 con SOLICITUD_ILEGIBLE")
  void cuerpoIlegibleDebeRetornar400() throws Exception {
    enviar("esto no es json")
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.codigo").value("SOLICITUD_ILEGIBLE"));
  }
}
