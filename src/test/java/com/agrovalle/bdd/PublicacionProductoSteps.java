package com.agrovalle.bdd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.agrovalle.CategoriaProductoFabrica;
import com.agrovalle.CategoriaProductoRepository;
import com.agrovalle.EstadoLote;
import com.agrovalle.EstadoTrazabilidad;
import com.agrovalle.EstadoTrazabilidadRepository;
import com.agrovalle.LoteProduccion;
import com.agrovalle.LoteProduccionRepository;
import com.agrovalle.Producto;
import com.agrovalle.ProductoRepository;
import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

/**
 * Pasos de HU-02 (publicación de productos). Recorren la API real con
 * MockMvc: registro, inicio de sesión para obtener el JWT y publicación en
 * POST /api/v1/productos, sobre H2 en memoria.
 */
public class PublicacionProductoSteps {

    private static final String URL_REGISTRO = "/api/v1/auth/register";
    private static final String URL_LOGIN = "/api/v1/auth/login";
    private static final String URL_PRODUCTOS = "/api/v1/productos";
    private static final String CONTRASENA = "AgroValle123!";
    private static final int ID_CATEGORIA_EXISTENTE = 1;
    private static final int ID_CATEGORIA_INEXISTENTE = 9999;
    private static final int ID_USUARIO_SIN_SESION = 1;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoriaProductoRepository categoriaProductoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private LoteProduccionRepository loteProduccionRepository;

    @Autowired
    private EstadoTrazabilidadRepository estadoTrazabilidadRepository;

    private final List<Integer> idsProducto = new ArrayList<>();
    private Integer usuarioId;
    private Integer otroAgricultorId;
    private String tokenAcceso;
    private MvcResult resultado;

  // ---------------------------------------------------------------- Dado

    @Dado("que existe la categoría de producto {string}")
    public void existeCategoria(String nombre) {
        categoriaProductoRepository.save(CategoriaProductoFabrica.crear(
            ID_CATEGORIA_EXISTENTE, nombre, "Categoría de pruebas BDD"));
    }

    @Dado("que existe un agricultor autenticado con token JWT")
    public void agricultorAutenticado() throws Exception {
        autenticar("AGRICULTOR", "901000001", "agricultor.bdd@agrovalle.test");
    }

    @Dado("que existe un comprador autenticado con token JWT")
    public void compradorAutenticado() throws Exception {
        autenticar("COMPRADOR", "901000002", "comprador.bdd@agrovalle.test");
    }

    @Dado("que existe otro agricultor registrado")
    public void otroAgricultorRegistrado() throws Exception {
        otroAgricultorId = registrarUsuario(
            "AGRICULTOR", "901000003", "otro.agricultor.bdd@agrovalle.test");
    }

    @Dado("que no hay ninguna sesión iniciada")
    public void sinSesion() {
        usuarioId = ID_USUARIO_SIN_SESION;
        tokenAcceso = null;
    }

    @Dado("que el cliente envía un token JWT inválido")
    public void tokenInvalido() {
        usuarioId = ID_USUARIO_SIN_SESION;
        tokenAcceso = "token.jwt.invalido";
    }

  // -------------------------------------------------------------- Cuando

    @Cuando("publica el producto {string} de tipo {string} con cantidad {int} y precio {int}")
    public void publicarProducto(
        String nombre, String tipo, int cantidad, int precio) throws Exception {
        publicar(nombre, tipo, cantidad, cantidad, precio,
            ID_CATEGORIA_EXISTENTE, fechaCosechaValida());
    }

    @Cuando("publica un producto con cantidad producida {int}, "
        + "cantidad disponible {int} y precio {int}")
    public void publicarConCantidades(
        int producida, int disponible, int precio) throws Exception {
        publicar("Tomate chonto", "Hortaliza", producida, disponible, precio,
            ID_CATEGORIA_EXISTENTE, fechaCosechaValida());
    }

    @Cuando("publica un producto en una categoría que no existe")
    public void publicarEnCategoriaInexistente() throws Exception {
        publicar("Tomate chonto", "Hortaliza", 500, 500, 2500,
            ID_CATEGORIA_INEXISTENTE, fechaCosechaValida());
    }

    @Cuando("publica un producto con fecha de cosecha posterior a la fecha actual")
    public void publicarConFechaFutura() throws Exception {
        publicar("Tomate chonto", "Hortaliza", 500, 500, 2500,
            ID_CATEGORIA_EXISTENTE, LocalDate.now().plusDays(1));
    }

    @Cuando("publica un producto a nombre del otro agricultor")
    public void publicarANombreDeOtroAgricultor() throws Exception {
        publicarComo(otroAgricultorId, "Tomate chonto", "Hortaliza", 500, 500, 2500,
            ID_CATEGORIA_EXISTENTE, fechaCosechaValida());
    }

  // ----------------------------------------------------------- Entonces

    @Entonces("el sistema responde con el código HTTP {int}")
    public void comprobarCodigo(int codigo) throws Exception {
        assertEquals(codigo, resultado.getResponse().getStatus(),
            "Cuerpo de la respuesta: " + resultado.getResponse().getContentAsString());
    }

    @Entonces("la respuesta incluye un identificador de producto")
    public void comprobarIdentificador() {
        assertFalse(idsProducto.isEmpty(),
            "La respuesta no incluyó un idProducto.");
    }

    @Entonces("los dos productos reciben identificadores distintos")
    public void comprobarIdentificadoresUnicos() {
        assertEquals(2, idsProducto.size(), "Se esperaban dos productos publicados.");
        assertEquals(2, new HashSet<>(idsProducto).size(),
            "Los identificadores de producto se repitieron: " + idsProducto);
    }

    @Entonces("el producto {string} queda guardado con precio {int}")
    public void comprobarProductoGuardado(String nombre, int precio) {
        Producto producto = productoRepository.findById(ultimoIdProducto())
            .orElseThrow(() -> new AssertionError(
                "El producto no quedó guardado en la base de datos."));

        assertEquals(nombre, producto.getNombre());
        assertEquals(0, new BigDecimal(precio).compareTo(producto.getPrecio()),
            "Precio guardado: " + producto.getPrecio());
    }

    @Entonces("el lote inicial queda guardado con {int} unidades disponibles")
    public void comprobarLoteGuardado(int cantidad) {
        List<LoteProduccion> lotes = loteProduccionRepository.findAll();

        assertEquals(1, lotes.size(), "Se esperaba exactamente un lote.");
        assertEquals(0,
            new BigDecimal(cantidad).compareTo(lotes.get(0).getCantidadDisponible()),
            "Cantidad disponible guardada: " + lotes.get(0).getCantidadDisponible());
    }

    @Entonces("la trazabilidad del lote queda en estado {string}")
    public void comprobarTrazabilidad(String estado) {
        List<EstadoTrazabilidad> estados = estadoTrazabilidadRepository.findAll();

        assertEquals(1, estados.size(),
            "Se esperaba exactamente un registro de trazabilidad.");
        assertEquals(EstadoLote.valueOf(estado), estados.get(0).getEstadoLote());
    }

    @Entonces("el sistema informa el error {string}")
    public void comprobarCodigoError(String codigoEsperado) throws Exception {
        String codigo = JsonPath.read(
            resultado.getResponse().getContentAsString(), "$.codigo");

        assertEquals(codigoEsperado, codigo);
    }

    @Entonces("no se guarda ningún producto")
    public void comprobarSinProductos() {
        assertEquals(0, productoRepository.count(),
            "No debía guardarse ningún producto.");
        assertEquals(0, loteProduccionRepository.count(),
            "No debía guardarse ningún lote.");
    }

  // ------------------------------------------------------------ Auxiliares

    private void autenticar(String rol, String documento, String correo)
        throws Exception {
        usuarioId = registrarUsuario(rol, documento, correo);
        tokenAcceso = iniciarSesion(correo);
    }

    private Integer registrarUsuario(String rol, String documento, String correo)
        throws Exception {
        String cuerpo = """
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

        MvcResult registro = mockMvc.perform(post(URL_REGISTRO)
            .contentType(MediaType.APPLICATION_JSON)
            .content(cuerpo))
            .andReturn();
        exigirEstado(registro, 201, "registrar al usuario");

        return JsonPath.read(registro.getResponse().getContentAsString(), "$.id");
    }

    private String iniciarSesion(String correo) throws Exception {
        String cuerpo = """
            {
            "correo": "%s",
            "contrasena": "%s"
            }
            """.formatted(correo, CONTRASENA);

        MvcResult login = mockMvc.perform(post(URL_LOGIN)
            .contentType(MediaType.APPLICATION_JSON)
            .content(cuerpo))
            .andReturn();
        exigirEstado(login, 200, "iniciar sesión");

        return JsonPath.read(login.getResponse().getContentAsString(), "$.accessToken");
    }

    private void publicar(
        String nombre,
        String tipo,
        int producida,
        int disponible,
        int precio,
        int categoriaId,
        LocalDate fechaCosecha) throws Exception {

        publicarComo(usuarioId, nombre, tipo, producida, disponible, precio,
            categoriaId, fechaCosecha);
    }

    /**
     * Publica enviando en el cuerpo el agricultorId indicado, que puede ser
     * distinto del usuario dueño del token.
     */
    private void publicarComo(
        Integer agricultorId,
        String nombre,
        String tipo,
        int producida,
        int disponible,
        int precio,
        int categoriaId,
        LocalDate fechaCosecha) throws Exception {

        String cuerpo = """
            {
            "categoriaId": %d,
            "agricultorId": %d,
            "nombre": "%s",
            "tipoProducto": "%s",
            "unidadMedida": "KILOGRAMO",
            "precio": %d,
            "imagen": "https://ejemplo.com/producto.jpg",
            "fechaCosecha": "%s",
            "cantidadProducida": %d,
            "cantidadDisponible": %d,
            "ubicacionLote": "Finca El Paraíso, Dagua"
            }
            """.formatted(categoriaId, agricultorId, nombre, tipo, precio,
            fechaCosecha, producida, disponible);

        MockHttpServletRequestBuilder peticion = post(URL_PRODUCTOS)
            .contentType(MediaType.APPLICATION_JSON)
            .content(cuerpo);

        if (tokenAcceso != null) {
        peticion.header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAcceso);
        }

    resultado = mockMvc.perform(peticion).andReturn();

        if (resultado.getResponse().getStatus() == 201) {
        Integer idProducto = JsonPath.read(
            resultado.getResponse().getContentAsString(), "$.idProducto");
        idsProducto.add(idProducto);
        }
    }

    private LocalDate fechaCosechaValida() {
        return LocalDate.now().minusDays(1);
    }

    private Integer ultimoIdProducto() {
        assertFalse(idsProducto.isEmpty(), "No se publicó ningún producto.");
        return idsProducto.get(idsProducto.size() - 1);
    }

    private void exigirEstado(MvcResult respuesta, int esperado, String accion)
        throws Exception {
        if (respuesta.getResponse().getStatus() != esperado) {
        throw new IllegalStateException("No se pudo " + accion
            + ". Estado " + respuesta.getResponse().getStatus() + ": "
            + respuesta.getResponse().getContentAsString());
        }
    }
}
