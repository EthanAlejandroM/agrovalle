package com.agrovalle.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void solicitudSinDatosDebeRetornar400() throws Exception {
    mockMvc.perform(post("/usuarios"))
        .andExpect(status().isBadRequest());
  }

  @Test
    void documentoInvalidoDebeRetornar400() throws Exception {
    String solicitud = """
        {
            "nombre": "Nicolle",
            "ubicacionValle": "Cali",
            "tipoDocumento": "CC",
            "documento": "123-456",
            "rol": "COMPRADOR"
        }
        """;

    mockMvc.perform(post("/usuarios")
            .contentType("application/json")
            .content(solicitud))
        .andExpect(status().isBadRequest());
    }
}