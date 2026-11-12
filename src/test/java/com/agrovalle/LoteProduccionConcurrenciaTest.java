package com.agrovalle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import jakarta.persistence.Version;
import java.lang.reflect.Field;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("LoteProduccion - control de concurrencia optimista")
class LoteProduccionConcurrenciaTest {

  @Test
  @DisplayName("El lote tiene un campo @Version para detectar modificaciones simultáneas del stock")
  void loteDebeTenerCampoVersion() throws Exception {
    Field version = LoteProduccion.class.getDeclaredField("version");

    assertNotNull(version.getAnnotation(Version.class));
    assertEquals(Long.class, version.getType());
  }
}
