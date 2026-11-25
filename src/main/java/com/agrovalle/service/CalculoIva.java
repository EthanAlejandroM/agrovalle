package com.agrovalle.service;

import com.agrovalle.entity.Producto;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Regla de negocio del IVA, compartida por el carrito (resumen de orden) y por
 * el pedido (detalle_compra / orden_compra) para que ambos calculen lo mismo.
 *
 * <ul>
 * <li>El total de una línea es precio unitario x cantidad, sin IVA.</li>
 * <li>El IVA es 5 % del total de la línea, solo para productos de la categoría
 * con ID 7; para las demás categorías es 0.</li>
 * <li>El IVA de cada línea se redondea a 2 decimales (HALF_UP) y el IVA de la
 * orden es la suma de esos valores ya redondeados.</li>
 * </ul>
 */
public final class CalculoIva {

  public static final int ID_CATEGORIA_CON_IVA = 7;
  public static final BigDecimal TARIFA_IVA = new BigDecimal("0.05");

  private static final BigDecimal CERO = new BigDecimal("0.00");

  private CalculoIva() {
  }

  /** ¿El producto pertenece a la categoría que paga IVA? */
  public static boolean tieneIva(Producto producto) {
    return producto != null
        && producto.getCategoria() != null
        && Integer.valueOf(ID_CATEGORIA_CON_IVA)
            .equals(producto.getCategoria().getIdCategoria());
  }

  /**
   * IVA de una línea. Siempre devuelve un valor con escala 2 (0.00 si el
   * producto no paga IVA), nunca null.
   *
   * @param totalLinea precio unitario x cantidad, sin IVA
   */
  public static BigDecimal ivaDe(Producto producto, BigDecimal totalLinea) {
    if (!tieneIva(producto)) {
      return CERO;
    }
    return totalLinea.multiply(TARIFA_IVA).setScale(2, RoundingMode.HALF_UP);
  }
}
