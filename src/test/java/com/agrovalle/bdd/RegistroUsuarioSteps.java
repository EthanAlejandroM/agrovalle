
package com.agrovalle.bdd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import com.agrovalle.TipoUsuario;
import com.agrovalle.Usuario;
import com.agrovalle.UsuarioRepository;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

public class RegistroUsuarioSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private String nombre;
    private String documento;
    private String rol;
    private MvcResult resultado;

    @Dado("que ingreso los datos válidos de un agricultor")
    public void datosValidosAgricultor() {
        preparar("Finca BDD", "901234567", "AGRICULTOR");
    }

    @Dado("que ingreso los datos válidos de un comprador")
    public void datosValidosComprador() {
        preparar("Comprador BDD", "901234568", "COMPRADOR");
    }

    @Dado("que ingreso los datos de un agricultor con un documento inválido")
    public void agricultorConDocumentoInvalido() {
        preparar("Finca BDD", "123-456", "AGRICULTOR");
    }

    @Dado("que ingreso los datos de un comprador con un documento inválido")
    public void compradorConDocumentoInvalido() {
        preparar("Comprador BDD", "123-456", "COMPRADOR");
    }

    @Dado("que ya existe un agricultor con ese documento")
    public void agricultorExistente() {
        preparar("Finca existente", "901234569", "AGRICULTOR");
        registrarInicial();
    }

    @Dado("que ya existe un comprador con ese documento")
    public void compradorExistente() {
        preparar("Comprador existente", "901234570", "COMPRADOR");
        registrarInicial();
    }

    @Cuando("envío la solicitud de registro")
    public void enviarSolicitudRegistro() throws Exception {
        resultado = mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(solicitud()))
            .andReturn();
    }

    @Cuando("intento registrar otro agricultor con el mismo documento")
    public void duplicarAgricultor() throws Exception {
        preparar("Otro agricultor", documento, "AGRICULTOR");
        enviarSolicitudRegistro();
    }

    @Cuando("intento registrar otro comprador con el mismo documento")
    public void duplicarComprador() throws Exception {
        preparar("Otro comprador", documento, "COMPRADOR");
        enviarSolicitudRegistro();
    }

    @Entonces("el sistema responde con código {int}")
    public void comprobarCodigo(int codigo) {
        assertEquals(codigo, resultado.getResponse().getStatus());
    }

    @Entonces("el agricultor queda registrado en el sistema")
    public void comprobarAgricultorRegistrado() {
        assertTrue(usuarioRepository.existsByDocumento(documento));
    }

    @Entonces("el comprador queda registrado con el rol COMPRADOR")
    public void comprobarCompradorRegistrado() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        assertTrue(usuarios.stream().anyMatch(
            usuario -> documento.equals(usuario.getDocumento())
                && usuario.getRol() == TipoUsuario.COMPRADOR));
    }

    @Entonces("el sistema informa que el documento está duplicado")
    public void comprobarDocumentoDuplicado() throws Exception {
        assertEquals("DOCUMENTO_DUPLICADO",
            codigoError());
    }

    @Entonces("el sistema informa que la validación ha fallado")
    public void comprobarValidacionFallida() throws Exception {
        assertEquals("VALIDACION_FALLIDA",
            codigoError());
    }

    private void preparar(String nombre, String documento, String rol) {
        this.nombre = nombre;
        this.documento = documento;
        this.rol = rol;
    }

    private void registrarInicial() {
        try {
        resultado = mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(solicitud()))
            .andReturn();

        assertEquals(201, resultado.getResponse().getStatus());
        } catch (Exception e) {
        throw new IllegalStateException("No se pudo crear el registro inicial", e);
        }
    }

    private String solicitud() {
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

    private String codigoError() throws Exception {
        return com.jayway.jsonpath.JsonPath.read(
            resultado.getResponse().getContentAsString(), "$.codigo");
    }
}
