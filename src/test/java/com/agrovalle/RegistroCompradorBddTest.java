package com.agrovalle;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Traducción a JUnit 5 del escenario BDD de HU-11 (Registro de Clientes/Compradores).
 * Recorre Controller -> Service -> Repository -> base de datos de pruebas (H2).
 * Cada prueba se revierte al terminar (@Transactional), por lo que no se contaminan entre sí.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("HU-11: Registro de Compradores (BDD de integración)")
class RegistroCompradorBddTest {

  private static final String URL = "/api/v1/auth/register";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UsuarioRepository usuarioRepository;

  private static String solicitud(String nombre, String documento, String rol) {
    return """
        {
          "nombre": "%s",
          "ubicacionValle": "Cali",
          "tipoDocumento": "CC",
          "documento": "%s",
          "rol": "%s"
        }
        """.formatted(nombre, documento, rol);
  }

  @Test
  @DisplayName("Escenario 1: registro válido responde 201 y persiste el comprador")
  void registroValidoDebePersistirComprador() throws Exception {
    // Given: el usuario ingresa a /api/v1/auth/register y el documento no existe aún
    assertFalse(usuarioRepository.existsByDocumento("5550001112"));

    // When: envía nombre, ubicación del Valle y un documento válido con rol COMPRADOR
    mockMvc.perform(post(URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(solicitud("Restaurante El Trapiche", "5550001112", "COMPRADOR")))
        // Then: el sistema responde 201 Created con el id del recurso creado
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").isNumber())
        .andExpect(jsonPath("$.rol").value("COMPRADOR"));

    // And: el registro persiste con rol COMPRADOR y los datos enviados
    List<Usuario> usuarios = usuarioRepository.findAll();
    assertEquals(1, usuarios.size());

    Usuario guardado = usuarios.get(0);
    assertEquals("Restaurante El Trapiche", guardado.getNombre());
    assertEquals("Cali", guardado.getUbicacionValle());
    assertEquals("5550001112", guardado.getDocumento());
    assertEquals(TipoUsuario.COMPRADOR, guardado.getRol());
    assertEquals(LocalDate.now(), guardado.getFechaRegistro());
  }

  @Test
  @DisplayName("Escenario 2: documento ya registrado responde 409 y no crea otro registro")
  void documentoDuplicadoNoDebeCrearSegundoRegistro() throws Exception {
    // Given: ya existe un comprador registrado con ese documento
    mockMvc.perform(post(URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(solicitud("Tienda La Esquina", "5550002223", "COMPRADOR")))
        .andExpect(status().isCreated());

    // When: otro usuario intenta registrarse con el mismo documento
    mockMvc.perform(post(URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(solicitud("Otra Persona", "5550002223", "COMPRADOR")))
        // Then: el sistema responde 409 Conflict con un error estructurado
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.codigo").value("DOCUMENTO_DUPLICADO"));

    // And: sigue existiendo un único registro y es el original
    List<Usuario> usuarios = usuarioRepository.findAll();
    assertEquals(1, usuarios.size());
    assertEquals("Tienda La Esquina", usuarios.get(0).getNombre());
  }

  @Test
  @DisplayName("Escenario 3: documento con formato inválido responde 400 y no persiste nada")
  void documentoInvalidoNoDebePersistir() throws Exception {
    // Given: el usuario ingresa a /api/v1/auth/register
    // When: envía un documento con formato inválido (contiene guion)
    mockMvc.perform(post(URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(solicitud("Nicolle", "555-000", "COMPRADOR")))
        // Then: el sistema responde 400 Bad Request indicando el campo documento
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.codigo").value("VALIDACION_FALLIDA"))
        .andExpect(jsonPath("$.errores[?(@.campo == 'documento')]").isNotEmpty());

    // And: no se guarda ningún registro
    assertTrue(usuarioRepository.findAll().isEmpty());
  }

  @Test
  @DisplayName("Escenario 4: ambos roles se registran desde el mismo endpoint")
  void ambosRolesSeRegistranDesdeElMismoEndpoint() throws Exception {
    // Given: el mismo endpoint /api/v1/auth/register sirve a agricultores y compradores
    // When: se registra un AGRICULTOR y un COMPRADOR con documentos distintos
    mockMvc.perform(post(URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(solicitud("Finca Dagua", "7770001", "AGRICULTOR")))
        // Then: ambos responden 201 Created
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.rol").value("AGRICULTOR"));

    mockMvc.perform(post(URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(solicitud("Restaurante Cali", "7770002", "COMPRADOR")))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.rol").value("COMPRADOR"));

    // And: cada uno persiste con su rol correspondiente
    List<Usuario> usuarios = usuarioRepository.findAll();
    assertEquals(2, usuarios.size());
    assertTrue(usuarios.stream().anyMatch(
        u -> "7770001".equals(u.getDocumento()) && u.getRol() == TipoUsuario.AGRICULTOR));
    assertTrue(usuarios.stream().anyMatch(
        u -> "7770002".equals(u.getDocumento()) && u.getRol() == TipoUsuario.COMPRADOR));
  }

  @Test
  @DisplayName("Escenario 5: un documento ya usado por un agricultor no puede ser comprador")
  void documentoDeAgricultorNoPuedeRegistrarseComoComprador() throws Exception {
    // Given: existe un agricultor registrado con un documento
    mockMvc.perform(post(URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(solicitud("Finca Dagua", "8880001", "AGRICULTOR")))
        .andExpect(status().isCreated());

    // When: el mismo documento intenta registrarse con rol COMPRADOR
    mockMvc.perform(post(URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(solicitud("Finca Dagua", "8880001", "COMPRADOR")))
        // Then: el sistema responde 409 Conflict, porque el documento es único por usuario
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.codigo").value("DOCUMENTO_DUPLICADO"));

    // And: el usuario original conserva su rol de AGRICULTOR
    List<Usuario> usuarios = usuarioRepository.findAll();
    assertEquals(1, usuarios.size());
    assertEquals(TipoUsuario.AGRICULTOR, usuarios.get(0).getRol());
  }
}