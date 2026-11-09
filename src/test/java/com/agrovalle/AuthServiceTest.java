package com.agrovalle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.agrovalle.service.AuthService;
import com.agrovalle.service.JwtService;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService - inicio de sesión")
class AuthServiceTest {

  private static final String CORREO = "ana@agrovalle.test";
  private static final String CONTRASENA = "Clave12345";
  private static final String HASH = "hash-bcrypt";

  @Mock
  private UsuarioRepository usuarioRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private JwtService jwtService;

  @InjectMocks
  private AuthService authService;

  private InicioSesionSolicitud solicitud() {
    InicioSesionSolicitud solicitud = new InicioSesionSolicitud();
    solicitud.setCorreo(CORREO);
    solicitud.setContrasena(CONTRASENA);
    return solicitud;
  }

  private Usuario usuario() {
    Usuario usuario = new Usuario();
    usuario.setId(1);
    usuario.setCorreo(CORREO);
    usuario.setContrasenaHash(HASH);
    return usuario;
  }

  @Test
  @DisplayName("Credenciales correctas: devuelve el token Bearer y su expiración")
  void credencialesCorrectasDebenDevolverToken() {
    Usuario usuario = usuario();
    when(usuarioRepository.findByCorreo(CORREO)).thenReturn(Optional.of(usuario));
    when(passwordEncoder.matches(CONTRASENA, HASH)).thenReturn(true);
    when(jwtService.generateToken(usuario)).thenReturn("token-jwt");
    when(jwtService.getExpirationMs()).thenReturn(3600000L);

    AuthResponseSolicitud respuesta = authService.iniciarSesion(solicitud());

    assertEquals("token-jwt", respuesta.getAccessToken());
    assertEquals("Bearer", respuesta.getTokenType());
    assertEquals(3600000L, respuesta.getExpiresIn());
  }

  @Test
  @DisplayName("Correo inexistente: lanza BadCredentialsException")
  void correoInexistenteDebeLanzarExcepcion() {
    when(usuarioRepository.findByCorreo(CORREO)).thenReturn(Optional.empty());

    assertThrows(BadCredentialsException.class,
        () -> authService.iniciarSesion(solicitud()));

    verifyNoInteractions(passwordEncoder, jwtService);
  }

  @Test
  @DisplayName("Contraseña incorrecta: lanza BadCredentialsException y no genera token")
  void contrasenaIncorrectaDebeLanzarExcepcion() {
    when(usuarioRepository.findByCorreo(CORREO)).thenReturn(Optional.of(usuario()));
    when(passwordEncoder.matches(CONTRASENA, HASH)).thenReturn(false);

    assertThrows(BadCredentialsException.class,
        () -> authService.iniciarSesion(solicitud()));

    verifyNoInteractions(jwtService);
  }
}
