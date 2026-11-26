package com.agrovalle.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.agrovalle.entity.CategoriaProducto;
import com.agrovalle.entity.CategoriaProductoFabrica;
import com.agrovalle.entity.Producto;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("CalculoIva - IVA 5 % solo para la categoría 7")
class CalculoIvaTest {

  private Producto productoDeCategoria(Integer idCategoria) {
    CategoriaProducto categoria =
        CategoriaProductoFabrica.crear(
            idCategoria,
            "Categoría de prueba",
            "Descripción de prueba"
        );

    Producto producto = new Producto();
    producto.setCategoria(categoria);

    return producto;
  }


  private void assertDecimal(String esperado, BigDecimal real) {
    assertEquals(0, new BigDecimal(esperado).compareTo(real),
        "esperado " + esperado + " pero fue " + real);
  }

  @Test
  @DisplayName("Categoría 7: el IVA es el 5 % del total de la línea")
  void categoria7DebeCobrarCincoPorCiento() {
    BigDecimal iva = CalculoIva.ivaDe(productoDeCategoria(7), new BigDecimal("4000.00"));

    assertDecimal("200.00", iva);
    assertEquals(2, iva.scale());
  }

  @ParameterizedTest(name = "Categoría {0}: el IVA es 0")
  @ValueSource(ints = {1, 2, 6, 8, 70, 17})
  void otrasCategoriasNoCobranIva(int idCategoria) {
    BigDecimal iva = CalculoIva.ivaDe(
        productoDeCategoria(idCategoria), new BigDecimal("4000.00"));

    assertDecimal("0.00", iva);
    assertEquals(2, iva.scale());
  }

  @Test
  @DisplayName("Producto sin categoría cargada: el IVA es 0 (nunca null)")
  void sinCategoriaNoCobraIva() {
    BigDecimal iva = CalculoIva.ivaDe(new Producto(), new BigDecimal("4000.00"));

    assertDecimal("0.00", iva);
  }

  @Test
  @DisplayName("El IVA se redondea a 2 decimales con HALF_UP")
  void debeRedondearHalfUp() {
    // 10.10 * 0.05 = 0.505 -> 0.51
    assertDecimal("0.51", CalculoIva.ivaDe(productoDeCategoria(7), new BigDecimal("10.10")));
    // 10.30 * 0.05 = 0.515 -> 0.52
    assertDecimal("0.52", CalculoIva.ivaDe(productoDeCategoria(7), new BigDecimal("10.30")));
    // 10.01 * 0.05 = 0.5005 -> 0.50
    assertDecimal("0.50", CalculoIva.ivaDe(productoDeCategoria(7), new BigDecimal("10.01")));
  }

  @Test
  @DisplayName("Total de la línea en 0: el IVA es 0")
  void totalCeroDebeDarIvaCero() {
    assertDecimal("0.00", CalculoIva.ivaDe(productoDeCategoria(7), new BigDecimal("0.00")));
  }

  @Test
  @DisplayName("tieneIva solo es verdadero para la categoría 7")
  void tieneIvaSoloCategoria7() {
    assertTrue(CalculoIva.tieneIva(productoDeCategoria(7)));
    assertFalse(CalculoIva.tieneIva(productoDeCategoria(1)));
    assertFalse(CalculoIva.tieneIva(new Producto()));
    assertFalse(CalculoIva.tieneIva(null));
  }
}
