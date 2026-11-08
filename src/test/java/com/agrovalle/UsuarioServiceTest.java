package com.agrovalle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.agrovalle.exception.DocumentoDuplicadoException;
import com.agrovalle.service.UsuarioService;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioService - registro de agricultores (HU-01)")
class UsuarioServiceTest {

  @Mock
  private UsuarioRepository usuarioRepository;

  @InjectMocks
  private UsuarioService usuarioService;

  private RegistroUsuarioSolicitud solicitudAgricultor() {
    RegistroUsuarioSolicitud solicitud = new RegistroUsuarioSolicitud();
    solicitud.setNombre("Ethan");
    solicitud.setUbicacionValle("Dagua");
    solicitud.setTipoDocumento(TipoDocumento.CC);
    solicitud.setDocumento("1234567890");
    solicitud.setRol(TipoUsuario.AGRICULTOR);
    return solicitud;
  }

  @Test
  @DisplayName("Documento nuevo: guarda el usuario con rol AGRICULTOR y fecha de hoy")
  void documentoNuevoDebeGuardarAgricultor() {
    when(usuarioRepository.existsByDocumento("1234567890")).thenReturn(false);
    when(usuarioRepository.save(any(Usuario.class)))
        .thenAnswer(invocacion -> invocacion.getArgument(0));

    Usuario resultado = usuarioService.registrar(solicitudAgricultor());

    ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
    verify(usuarioRepository).save(captor.capture());
    Usuario guardado = captor.getValue();

    assertEquals("Ethan", guardado.getNombre());
    assertEquals("Dagua", guardado.getUbicacionValle());
    assertEquals(TipoDocumento.CC, guardado.getTipoDocumento());
    assertEquals("1234567890", guardado.getDocumento());
    assertEquals(TipoUsuario.AGRICULTOR, guardado.getRol());
    assertEquals(LocalDate.now(), guardado.getFechaRegistro());
    assertEquals(guardado, resultado);
  }

  @Test
  @DisplayName("Documento repetido: lanza DocumentoDuplicadoException y no guarda nada")
  void documentoDuplicadoDebeLanzarExcepcionYNoGuardar() {
    when(usuarioRepository.existsByDocumento("1234567890")).thenReturn(true);

    assertThrows(DocumentoDuplicadoException.class,
        () -> usuarioService.registrar(solicitudAgricultor()));

    verify(usuarioRepository, never()).save(any(Usuario.class));
  }
}
