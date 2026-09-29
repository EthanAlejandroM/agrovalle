package com.agrovalle.controller;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void solicitudSinDatosDebeRetornar400() throws Exception {
    mockMvc.perform(post("/api/v1/auth/register"))
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

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType("application/json")
                .content(solicitud))
            .andExpect(status().isBadRequest());
    }

    @Test
    void registroValidoDebeRetornar201YPersistir() throws Exception {
        String solicitud = """
            {
                "nombre": "Ethan",
                "ubicacionValle": "Dagua",
                "tipoDocumento": "CC",
                "documento": "1234567890",
                "rol": "AGRICULTOR"
            }
            """;

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType("application/json")
                .content(solicitud))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    void documentoDuplicadoDebeRetornar409() throws Exception {
        String solicitud = """
            {
                "nombre": "Bairon",
                "ubicacionValle": "Palmira",
                "tipoDocumento": "CC",
                "documento": "999999999",
                "rol": "AGRICULTOR"
            }
            """;

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType("application/json")
                .content(solicitud))
            .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType("application/json")
                .content(solicitud))
            .andExpect(status().isConflict());
    }
}