package com.agrovalle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.agrovalle.service.JwtService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("JwtService - generación y validación de tokens")
class JwtServiceTest {

  // Misma clave de prueba (32 bytes en Base64) que usa el application.yml de test.
  private static final String SECRETO = "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=";
  private static final long EXPIRACION_MS = 3600000L;
  private static final String CORREO = "ana@agrovalle.test";

  private JwtService jwtService;

  @BeforeEach
  void crearServicio() {
    jwtService = new JwtService(SECRETO, EXPIRACION_MS);
  }

  private Usuario usuario(Integer id, String correo, TipoUsuario rol) {
    Usuario usuario = new Usuario();
    usuario.setId(id);
    usuario.setCorreo(correo);
    usuario.setRol(rol);
    return usuario;
  }

  private Usuario usuarioBase() {
    return usuario(1, CORREO, TipoUsuario.AGRICULTOR);
  }

  private SecretKey clave() {
    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRETO));
  }

  private Date enUnaHora() {
    return new Date(System.currentTimeMillis() + EXPIRACION_MS);
  }

  @Test
  @DisplayName("El token generado contiene correo, id y rol del usuario")
  void tokenDebeContenerDatosDelUsuario() {
    String token = jwtService.generateToken(usuarioBase());

    assertEquals(CORREO, jwtService.extractUsername(token));
    assertEquals(Integer.valueOf(1), jwtService.extractUserId(token));
    assertEquals("AGRICULTOR", jwtService.extractRole(token));
  }

  @Test
  @DisplayName("Token recién generado para el mismo usuario es válido")
  void tokenRecienGeneradoDebeSerValido() {
    Usuario usuario = usuarioBase();

    assertTrue(jwtService.isTokenValid(jwtService.generateToken(usuario), usuario));
  }

  @Test
  @DisplayName("getExpirationMs devuelve el tiempo configurado")
  void debeExponerLaExpiracionConfigurada() {
    assertEquals(EXPIRACION_MS, jwtService.getExpirationMs());
  }

  @Test
  @DisplayName("Token de otro correo no es válido para el usuario")
  void tokenDeOtroCorreoNoDebeSerValido() {
    String token = jwtService.generateToken(usuarioBase());

    assertFalse(jwtService.isTokenValid(token,
        usuario(1, "otro@agrovalle.test", TipoUsuario.AGRICULTOR)));
  }

  @Test
  @DisplayName("Token de otro id no es válido para el usuario")
  void tokenDeOtroIdNoDebeSerValido() {
    String token = jwtService.generateToken(usuarioBase());

    assertFalse(jwtService.isTokenValid(token,
        usuario(99, CORREO, TipoUsuario.AGRICULTOR)));
  }

  @Test
  @DisplayName("Token con otro rol no es válido para el usuario")
  void tokenConOtroRolNoDebeSerValido() {
    String token = jwtService.generateToken(usuarioBase());

    assertFalse(jwtService.isTokenValid(token,
        usuario(1, CORREO, TipoUsuario.COMPRADOR)));
  }

  @Test
  @DisplayName("Token vencido no es válido")
  void tokenVencidoNoDebeSerValido() {
    Usuario usuario = usuarioBase();
    String vencido = new JwtService(SECRETO, -1000L).generateToken(usuario);

    assertFalse(jwtService.isTokenValid(vencido, usuario));
  }

  @Test
  @DisplayName("Token manipulado (firma alterada) no es válido")
  void tokenManipuladoNoDebeSerValido() {
    Usuario usuario = usuarioBase();
    String manipulado = jwtService.generateToken(usuario) + "x";

    assertFalse(jwtService.isTokenValid(manipulado, usuario));
  }

  @Test
  @DisplayName("Token sin fecha de expiración no es válido")
  void tokenSinExpiracionNoDebeSerValido() {
    Usuario usuario = usuarioBase();
    String token = Jwts.builder()
        .subject(CORREO)
        .claim("id", 1)
        .claim("role", "AGRICULTOR")
        .signWith(clave())
        .compact();

    assertFalse(jwtService.isTokenValid(token, usuario));
  }

  @Test
  @DisplayName("Token sin id: extractUserId lanza IllegalArgumentException y no es válido")
  void tokenSinIdNoDebeSerValido() {
    Usuario usuario = usuarioBase();
    String token = Jwts.builder()
        .subject(CORREO)
        .claim("role", "AGRICULTOR")
        .expiration(enUnaHora())
        .signWith(clave())
        .compact();

    assertThrows(IllegalArgumentException.class, () -> jwtService.extractUserId(token));
    assertFalse(jwtService.isTokenValid(token, usuario));
  }
}
