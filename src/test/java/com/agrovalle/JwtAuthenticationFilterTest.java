package com.agrovalle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.agrovalle.service.JwtService;
import io.jsonwebtoken.MalformedJwtException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthenticationFilter - autenticación por token Bearer")
class JwtAuthenticationFilterTest {

  private static final String CORREO = "ana@agrovalle.test";
  private static final String TOKEN = "token-de-prueba";

  @Mock
  private JwtService jwtService;

  @Mock
  private UsuarioRepository usuarioRepository;

  private JwtAuthenticationFilter filtro;
  private MockHttpServletRequest request;
  private MockHttpServletResponse response;
  private MockFilterChain cadena;

  @BeforeEach
  void preparar() {
    SecurityContextHolder.clearContext();
    filtro = new JwtAuthenticationFilter(jwtService, usuarioRepository);
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
    cadena = new MockFilterChain();
  }

  @AfterEach
  void limpiar() {
    SecurityContextHolder.clearContext();
  }

  private Usuario usuario() {
    Usuario usuario = new Usuario();
    usuario.setId(1);
    usuario.setCorreo(CORREO);
    usuario.setRol(TipoUsuario.AGRICULTOR);
    return usuario;
  }

  private void conTokenBearer() {
    request.addHeader("Authorization", "Bearer " + TOKEN);
  }

  @Test
  @DisplayName("Sin cabecera Authorization: continúa la cadena sin autenticar")
  void sinCabeceraNoDebeAutenticar() throws Exception {
    filtro.doFilter(request, response, cadena);

    assertNull(SecurityContextHolder.getContext().getAuthentication());
    assertNotNull(cadena.getRequest());
    verifyNoInteractions(jwtService, usuarioRepository);
  }

  @Test
  @DisplayName("Cabecera que no es Bearer: no se intenta autenticar")
  void cabeceraNoBearerNoDebeAutenticar() throws Exception {
    request.addHeader("Authorization", "Basic abc123");

    filtro.doFilter(request, response, cadena);

    assertNull(SecurityContextHolder.getContext().getAuthentication());
    assertNotNull(cadena.getRequest());
    verifyNoInteractions(jwtService, usuarioRepository);
  }

  @Test
  @DisplayName("Token válido: autentica con el correo y el rol del usuario")
  void tokenValidoDebeAutenticar() throws Exception {
    Usuario usuario = usuario();
    conTokenBearer();
    when(jwtService.extractUsername(TOKEN)).thenReturn(CORREO);
    when(usuarioRepository.findByCorreo(CORREO)).thenReturn(Optional.of(usuario));
    when(jwtService.isTokenValid(TOKEN, usuario)).thenReturn(true);

    filtro.doFilter(request, response, cadena);

    Authentication autenticacion = SecurityContextHolder.getContext().getAuthentication();
    assertNotNull(autenticacion);
    assertEquals(CORREO, autenticacion.getName());
    assertEquals("ROLE_AGRICULTOR",
        autenticacion.getAuthorities().iterator().next().getAuthority());
    assertNotNull(cadena.getRequest());
  }

  @Test
  @DisplayName("Usuario del token inexistente: no autentica")
  void usuarioInexistenteNoDebeAutenticar() throws Exception {
    conTokenBearer();
    when(jwtService.extractUsername(TOKEN)).thenReturn(CORREO);
    when(usuarioRepository.findByCorreo(CORREO)).thenReturn(Optional.empty());

    filtro.doFilter(request, response, cadena);

    assertNull(SecurityContextHolder.getContext().getAuthentication());
    assertNotNull(cadena.getRequest());
  }

  @Test
  @DisplayName("Token no válido para el usuario: no autentica")
  void tokenInvalidoNoDebeAutenticar() throws Exception {
    Usuario usuario = usuario();
    conTokenBearer();
    when(jwtService.extractUsername(TOKEN)).thenReturn(CORREO);
    when(usuarioRepository.findByCorreo(CORREO)).thenReturn(Optional.of(usuario));
    when(jwtService.isTokenValid(TOKEN, usuario)).thenReturn(false);

    filtro.doFilter(request, response, cadena);

    assertNull(SecurityContextHolder.getContext().getAuthentication());
    assertNotNull(cadena.getRequest());
  }

  @Test
  @DisplayName("Token mal formado: se limpia el contexto y la cadena continúa")
  void tokenMalFormadoNoDebeAutenticar() throws Exception {
    conTokenBearer();
    when(jwtService.extractUsername(TOKEN))
        .thenThrow(new MalformedJwtException("token roto"));

    filtro.doFilter(request, response, cadena);

    assertNull(SecurityContextHolder.getContext().getAuthentication());
    assertNotNull(cadena.getRequest());
  }

  @Test
  @DisplayName("Si ya existe una autenticación previa, no se vuelve a validar el token")
  void autenticacionPreviaDebeConservarse() throws Exception {
    SecurityContextHolder.getContext().setAuthentication(
        new UsernamePasswordAuthenticationToken("previo", null, List.of()));
    conTokenBearer();

    filtro.doFilter(request, response, cadena);

    assertEquals("previo", SecurityContextHolder.getContext().getAuthentication().getName());
    assertNotNull(cadena.getRequest());
    verifyNoInteractions(jwtService, usuarioRepository);
  }
}
