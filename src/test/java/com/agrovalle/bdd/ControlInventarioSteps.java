package com.agrovalle.bdd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.agrovalle.enums.EstadoLote;
import com.agrovalle.entity.LoteProduccion;
import com.agrovalle.repository.LoteProduccionRepository;
import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

/**
 * Pasos de HU-13 (control de inventario). Recorren la API real con MockMvc:
 * registro, inicio de sesión, publicación del producto (POST) y actualización
 * del lote (PATCH /api/v1/productos/{idProducto}/lotes/{idLote}), sobre H2 en
 * memoria.
 */
public class ControlInventarioSteps {

  private static final String URL_REGISTRO = "/api/v1/auth/register";
  private static final String URL_LOGIN = "/api/v1/auth/login";
  private static final String URL_PRODUCTOS = "/api/v1/productos";
  private static final String CONTRASENA = "AgroValle123!";
  private static final int ID_CATEGORIA = 1;
  private static final int ID_INEXISTENTE = 9999;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private LoteProduccionRepository loteProduccionRepository;

  @Autowired
  private EntityManager entityManager;

  private final Map<String, Integer> idsProducto = new HashMap<>();
  private final Map<String, Integer> idsLote = new HashMap<>();
  private final Map<String, Inventario> inventarioEsperado = new HashMap<>();
  private Integer usuarioId;
  private String tokenAcceso;
  private MvcResult resultado;

  // ---------------------------------------------------------------- Dado

  @Dado("que existe un agricultor con sesión iniciada")
  public void agricultorConSesion() throws Exception {
    iniciarSesionComo("AGRICULTOR", "902000001", "agricultor.inventario@agrovalle.test");
  }

  @Dado("que otro agricultor distinto inicia sesión")
  public void otroAgricultorInicia() throws Exception {
    iniciarSesionComo("AGRICULTOR", "902000003", "otro.agricultor.inventario@agrovalle.test");
  }

  @Dado("que un comprador inicia sesión")
  public void compradorInicia() throws Exception {
    iniciarSesionComo("COMPRADOR", "902000002", "comprador.inventario@agrovalle.test");
  }

  @Dado("que el cliente no ha iniciado sesión")
  public void sinSesion() {
    tokenAcceso = null;
  }

  @Dado("que la petición lleva un token JWT inválido")
  public void tokenInvalido() {
    tokenAcceso = "token.jwt.invalido";
  }

  @Dado("que ese agricultor tiene publicado el producto {string} con {int} unidades disponibles")
  public void productoPublicado(String nombre, int cantidad) throws Exception {
    String cuerpo = """
        {
        "categoriaId": %d,
        "agricultorId": %d,
        "nombre": "%s",
        "tipoProducto": "Hortaliza",
        "unidadMedida": "KILOGRAMO",
        "precio": 2500,
        "imagen": "https://ejemplo.com/producto.jpg",
        "fechaCosecha": "%s",
        "cantidadProducida": %d,
        "cantidadDisponible": %d,
        "ubicacionLote": "Finca El Paraíso, Dagua"
        }
        """.formatted(ID_CATEGORIA, usuarioId, nombre, fechaAyer(), cantidad, cantidad);

    MockHttpServletRequestBuilder peticion = post(URL_PRODUCTOS)
        .contentType(MediaType.APPLICATION_JSON)
        .content(cuerpo);
    conToken(peticion);
    MvcResult publicacion = mockMvc.perform(peticion).andReturn();
    exigirEstado(publicacion, 201, "publicar el producto " + nombre);

    idsProducto.put(nombre, JsonPath.read(
        publicacion.getResponse().getContentAsString(), "$.idProducto"));
    // La respuesta solo trae idProducto; el lote recién creado es el de id mayor.
    idsLote.put(nombre, loteProduccionRepository.findAll().stream()
        .map(LoteProduccion::getIdLote)
        .max(Integer::compare)
        .orElseThrow(() -> new IllegalStateException("No se creó el lote inicial.")));
    inventarioEsperado.put(nombre, new Inventario(cantidad, cantidad, 1));
  }

  @Dado("que el lote de {string} está agotado")
  public void loteAgotado(String nombre) throws Exception {
    patchearLote(idProducto(nombre), idLote(nombre), "{\"cantidadDisponible\": 0}");
    exigirEstado(resultado, 200, "agotar el lote de " + nombre);

    Inventario inventario = inventarioEsperado.get(nombre);
    inventario.disponible = 0;
    inventario.registros++;
  }

  // -------------------------------------------------------------- Cuando

  @Cuando("actualiza la cantidad disponible del lote de {string} a {int}")
  public void actualizarDisponible(String nombre, int cantidad) throws Exception {
    patchearLote(idProducto(nombre), idLote(nombre),
        "{\"cantidadDisponible\": %d}".formatted(cantidad));
  }

  @Cuando("reabastece el lote agotado de {string} con producción de {int}, "
      + "disponible {int} y cosecha de ayer")
  public void reabastecerCompleto(String nombre, int producida, int disponible)
      throws Exception {
    patchearLote(idProducto(nombre), idLote(nombre), """
        {
        "cantidadProducida": %d,
        "cantidadDisponible": %d,
        "fechaCosecha": "%s"
        }
        """.formatted(producida, disponible, fechaAyer()));
  }

  @Cuando("reabastece el lote agotado de {string} con disponible {int} "
      + "y cosecha posterior a la fecha actual")
  public void reabastecerConFechaFutura(String nombre, int disponible) throws Exception {
    patchearLote(idProducto(nombre), idLote(nombre), """
        {
        "cantidadDisponible": %d,
        "fechaCosecha": "%s"
        }
        """.formatted(disponible, LocalDate.now().plusDays(1)));
  }

  @Cuando("intenta cambiar la cantidad producida del lote de {string} a {int}")
  public void cambiarProducida(String nombre, int producida) throws Exception {
    patchearLote(idProducto(nombre), idLote(nombre),
        "{\"cantidadProducida\": %d}".formatted(producida));
  }

  @Cuando("intenta cambiar la fecha de cosecha del lote de {string} a la de ayer")
  public void cambiarFecha(String nombre) throws Exception {
    patchearLote(idProducto(nombre), idLote(nombre),
        "{\"fechaCosecha\": \"%s\"}".formatted(fechaAyer()));
  }

  @Cuando("envía la actualización del lote de {string} sin ningún campo")
  public void actualizarSinCampos(String nombre) throws Exception {
    patchearLote(idProducto(nombre), idLote(nombre), "{}");
  }

  @Cuando("actualiza un producto que no existe")
  public void actualizarProductoInexistente() throws Exception {
    patchearLote(ID_INEXISTENTE, ID_INEXISTENTE, "{\"cantidadDisponible\": 10}");
  }

  @Cuando("actualiza un lote que no existe del producto {string}")
  public void actualizarLoteInexistente(String nombre) throws Exception {
    patchearLote(idProducto(nombre), ID_INEXISTENTE, "{\"cantidadDisponible\": 10}");
  }

  @Cuando("actualiza el lote de {string} a través del producto {string} "
      + "con {int} unidades disponibles")
  public void actualizarLoteDeOtroProducto(
      String nombreLote, String nombreProducto, int cantidad) throws Exception {
    patchearLote(idProducto(nombreProducto), idLote(nombreLote),
        "{\"cantidadDisponible\": %d}".formatted(cantidad));
  }

  // ----------------------------------------------------------- Entonces

  @Entonces("la API responde con estado HTTP {int}")
  public void comprobarEstado(int codigo) throws Exception {
    assertEquals(codigo, resultado.getResponse().getStatus(),
        "Cuerpo de la respuesta: " + resultado.getResponse().getContentAsString());
  }

  @Entonces("la API informa el código de error {string}")
  public void comprobarCodigoError(String codigoEsperado) throws Exception {
    String codigo = JsonPath.read(
        resultado.getResponse().getContentAsString(), "$.codigo");

    assertEquals(codigoEsperado, codigo);
  }

  @Entonces("la API confirma que el lote se actualizó correctamente")
  public void comprobarMensaje() throws Exception {
    String mensaje = JsonPath.read(
        resultado.getResponse().getContentAsString(), "$.mensaje");

    assertEquals("Lote actualizado correctamente.", mensaje);
  }

  @Entonces("la API confirma que el lote queda en estado {string}")
  public void comprobarEstadoEnRespuesta(String estado) throws Exception {
    String estadoLote = JsonPath.read(
        resultado.getResponse().getContentAsString(), "$.estadoLote");

    assertEquals(estado, estadoLote);
  }

  @Entonces("el lote de {string} queda con {int} unidades disponibles")
  public void comprobarDisponible(String nombre, int cantidad) {
    assertEquals(0, new BigDecimal(cantidad).compareTo(
        buscarLote(nombre).getCantidadDisponible()),
        "Cantidad disponible guardada: " + buscarLote(nombre).getCantidadDisponible());
  }

  @Entonces("el lote de {string} queda con una producción de {int} unidades")
  public void comprobarProducida(String nombre, int cantidad) {
    assertEquals(0, new BigDecimal(cantidad).compareTo(
        buscarLote(nombre).getCantidadProducida()),
        "Cantidad producida guardada: " + buscarLote(nombre).getCantidadProducida());
  }

  @Entonces("la trazabilidad más reciente del lote de {string} es {string}")
  public void comprobarUltimaTrazabilidad(String nombre, String estado) {
    EstadoLote ultimo = entityManager.createQuery(
        "select e.estadoLote from EstadoTrazabilidad e where e.lote.idLote = :id "
            + "order by e.fechaHora desc, e.idEstadoTrazabilidad desc",
        EstadoLote.class)
        .setParameter("id", idLote(nombre))
        .setMaxResults(1)
        .getSingleResult();

    assertEquals(EstadoLote.valueOf(estado), ultimo);
  }

  @Entonces("la trazabilidad del lote de {string} suma {int} registros")
  public void comprobarRegistros(String nombre, int cantidad) {
    assertEquals(cantidad, contarRegistros(nombre),
        "Registros de trazabilidad del lote de " + nombre);
  }

  @Entonces("el inventario del lote de {string} no se modifica")
  public void comprobarSinCambios(String nombre) {
    Inventario esperado = inventarioEsperado.get(nombre);
    assertNotNull(esperado, "No hay inventario de referencia para " + nombre);

    LoteProduccion lote = buscarLote(nombre);
    assertEquals(0, new BigDecimal(esperado.disponible)
        .compareTo(lote.getCantidadDisponible()),
        "La cantidad disponible cambió: " + lote.getCantidadDisponible());
    assertEquals(0, new BigDecimal(esperado.producida)
        .compareTo(lote.getCantidadProducida()),
        "La cantidad producida cambió: " + lote.getCantidadProducida());
    assertEquals(esperado.registros, contarRegistros(nombre),
        "No debía registrarse trazabilidad nueva.");
  }

  // ------------------------------------------------------------ Auxiliares

  private void iniciarSesionComo(String rol, String documento, String correo)
      throws Exception {
    String registro = """
        {
        "nombre": "Usuario BDD %s",
        "ubicacionValle": "Dagua",
        "tipoDocumento": "CC",
        "documento": "%s",
        "telefono": "3001234567",
        "correo": "%s",
        "contrasena": "%s",
        "rol": "%s"
        }
        """.formatted(rol, documento, correo, CONTRASENA, rol);

    MvcResult respuestaRegistro = mockMvc.perform(post(URL_REGISTRO)
        .contentType(MediaType.APPLICATION_JSON)
        .content(registro))
        .andReturn();
    exigirEstado(respuestaRegistro, 201, "registrar al usuario");
    usuarioId = JsonPath.read(
        respuestaRegistro.getResponse().getContentAsString(), "$.id");

    String login = """
        {
        "correo": "%s",
        "contrasena": "%s"
        }
        """.formatted(correo, CONTRASENA);

    MvcResult respuestaLogin = mockMvc.perform(post(URL_LOGIN)
        .contentType(MediaType.APPLICATION_JSON)
        .content(login))
        .andReturn();
    exigirEstado(respuestaLogin, 200, "iniciar sesión");
    tokenAcceso = JsonPath.read(
        respuestaLogin.getResponse().getContentAsString(), "$.accessToken");
  }

  private void patchearLote(int idProducto, int idLote, String cuerpo) throws Exception {
    MockHttpServletRequestBuilder peticion = patch(
        URL_PRODUCTOS + "/" + idProducto + "/lotes/" + idLote)
        .contentType(MediaType.APPLICATION_JSON)
        .content(cuerpo);
    conToken(peticion);

    resultado = mockMvc.perform(peticion).andReturn();
  }

  private void conToken(MockHttpServletRequestBuilder peticion) {
    if (tokenAcceso != null) {
      peticion.header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAcceso);
    }
  }

  private LoteProduccion buscarLote(String nombre) {
    return loteProduccionRepository.findById(idLote(nombre))
        .orElseThrow(() -> new AssertionError("El lote de " + nombre + " no existe."));
  }

  private long contarRegistros(String nombre) {
    return entityManager.createQuery(
        "select count(e) from EstadoTrazabilidad e where e.lote.idLote = :id",
        Long.class)
        .setParameter("id", idLote(nombre))
        .getSingleResult();
  }

  private int idProducto(String nombre) {
    return exigirConocido(idsProducto.get(nombre), "producto", nombre);
  }

  private int idLote(String nombre) {
    return exigirConocido(idsLote.get(nombre), "lote", nombre);
  }

  private int exigirConocido(Integer id, String tipo, String nombre) {
    if (id == null) {
      throw new IllegalStateException("No se publicó el " + tipo + " " + nombre + ".");
    }
    return id;
  }

  private LocalDate fechaAyer() {
    return LocalDate.now().minusDays(1);
  }

  private void exigirEstado(MvcResult respuesta, int esperado, String accion) throws Exception {
    if (respuesta.getResponse().getStatus() != esperado) {
      throw new IllegalStateException("No se pudo " + accion + ". Estado "
          + respuesta.getResponse().getStatus() + ": "
          + respuesta.getResponse().getContentAsString());
    }
  }

  /** Inventario que se espera encontrar en base de datos para un lote. */
  private static final class Inventario {
    private int disponible;
    private final int producida;
    private int registros;

    private Inventario(int disponible, int producida, int registros) {
      this.disponible = disponible;
      this.producida = producida;
      this.registros = registros;
    }
  }
}
