package com.agrovalle;

import com.agrovalle.exception.DocumentoDuplicadoException;
import com.agrovalle.service.UsuarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioService - registro de compradores (HU-11)")
class UsuarioServiceCompradorTest {

  @Mock
  private UsuarioRepository usuarioRepository;

  @InjectMocks
  private UsuarioService usuarioService;

  private RegistroUsuarioSolicitud solicitudComprador() {
    RegistroUsuarioSolicitud solicitud = new RegistroUsuarioSolicitud();
    solicitud.setNombre("Restaurante El Trapiche");
    solicitud.setUbicacionValle("Cali");
    solicitud.setTipoDocumento(TipoDocumento.CC);
    solicitud.setDocumento("5550001112");
    solicitud.setRol(TipoUsuario.COMPRADOR);
    return solicitud;
  }

  @Test
  @DisplayName("Documento nuevo: guarda el usuario con rol COMPRADOR y fecha de hoy")
  void documentoNuevoDebeGuardarComprador() {
    when(usuarioRepository.existsByDocumento("5550001112")).thenReturn(false);
    when(usuarioRepository.save(any(Usuario.class)))
        .thenAnswer(invocacion -> invocacion.getArgument(0));

    Usuario resultado = usuarioService.registrar(solicitudComprador());

    ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
    verify(usuarioRepository).save(captor.capture());
    Usuario guardado = captor.getValue();

    assertEquals("Restaurante El Trapiche", guardado.getNombre());
    assertEquals("Cali", guardado.getUbicacionValle());
    assertEquals(TipoDocumento.CC, guardado.getTipoDocumento());
    assertEquals("5550001112", guardado.getDocumento());
    assertEquals(TipoUsuario.COMPRADOR, guardado.getRol());
    assertEquals(LocalDate.now(), guardado.getFechaRegistro());
    assertEquals(guardado, resultado);
  }

  @Test
  @DisplayName("Documento repetido: lanza DocumentoDuplicadoException y no guarda nada")
  void documentoDuplicadoDebeLanzarExcepcionYNoGuardar() {
    when(usuarioRepository.existsByDocumento("5550001112")).thenReturn(true);

    assertThrows(DocumentoDuplicadoException.class,
        () -> usuarioService.registrar(solicitudComprador()));

    verify(usuarioRepository, never()).save(any(Usuario.class));
  }
}