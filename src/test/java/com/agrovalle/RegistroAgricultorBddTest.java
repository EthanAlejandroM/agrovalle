package com.agrovalle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Traducción a JUnit 5 del escenario BDD de HU-01 (Registro de Agricultores).
 * Recorre Controller -> Service -> Repository -> base de datos de pruebas (H2).
 * Cada prueba se revierte al terminar (@Transactional), por lo que no se contaminan entre sí.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("HU-01: Registro de Agricultores (BDD de integración)")
class RegistroAgricultorBddTest {

  private static final String URL = "/api/v1/auth/register";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UsuarioRepository usuarioRepository;

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

  @Test
  @DisplayName("Escenario 1: registro válido responde 201 y persiste en la base de datos")
  void registroValidoDebePersistirAgricultor() throws Exception {
    // Given: el usuario ingresa a /api/v1/auth/register y el documento no existe aún
    assertFalse(usuarioRepository.existsByDocumento("1234567890"));

    // When: envía nombre, ubicación del Valle y un documento válido
    mockMvc.perform(post(URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(solicitud("Ethan", "1234567890")))
        // Then: el sistema responde 201 Created con el id del recurso creado
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").isNumber());

    // And: el registro persiste con los datos enviados
    List<Usuario> usuarios = usuarioRepository.findAll();
    assertEquals(1, usuarios.size());

    Usuario guardado = usuarios.get(0);
    assertEquals("Ethan", guardado.getNombre());
    assertEquals("Dagua", guardado.getUbicacionValle());
    assertEquals("1234567890", guardado.getDocumento());
    assertEquals(TipoUsuario.AGRICULTOR, guardado.getRol());
    assertEquals(LocalDate.now(), guardado.getFechaRegistro());
  }

  @Test
  @DisplayName("Escenario 2: documento ya registrado responde 409 y no crea otro registro")
  void documentoDuplicadoNoDebeCrearSegundoRegistro() throws Exception {
    // Given: ya existe un agricultor registrado con ese documento
    mockMvc.perform(post(URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(solicitud("Bairon", "999999999")))
        .andExpect(status().isCreated());

    // When: otro usuario intenta registrarse con el mismo documento
    mockMvc.perform(post(URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(solicitud("Otra Persona", "999999999")))
        // Then: el sistema responde 409 Conflict con un error estructurado
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.codigo").value("DOCUMENTO_DUPLICADO"));

    // And: sigue existiendo un único registro y es el original
    List<Usuario> usuarios = usuarioRepository.findAll();
    assertEquals(1, usuarios.size());
    assertEquals("Bairon", usuarios.get(0).getNombre());
  }

  @Test
  @DisplayName("Escenario 3: documento con formato inválido responde 400 y no persiste nada")
  void documentoInvalidoNoDebePersistir() throws Exception {
    // Given: el usuario ingresa a /api/v1/auth/register
    // When: envía un documento con formato inválido (contiene guion)
    mockMvc.perform(post(URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(solicitud("Nicolle", "123-456")))
        // Then: el sistema responde 400 Bad Request indicando el campo documento
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.codigo").value("VALIDACION_FALLIDA"))
        .andExpect(jsonPath("$.errores[?(@.campo == 'documento')]").isNotEmpty());

    // And: no se guarda ningún registro
    assertTrue(usuarioRepository.findAll().isEmpty());
  }
}
