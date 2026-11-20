package com.agrovalle.repository;

import com.agrovalle.entity.PedidoAgricultor;
import com.agrovalle.enums.EstadoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PedidoAgricultorRepository extends JpaRepository<PedidoAgricultor, Integer> {

  /**
   * Cambia de estado todos los pedidos de la orden que estén en el estado
   * esperado. Si pago y vencimiento coinciden, solo uno encuentra los pedidos
   * en PENDIENTE; el otro recibe 0 filas afectadas.
   *
   * @return número de pedidos actualizados
   */
  @Modifying(flushAutomatically = true, clearAutomatically = true)
  @Query("""
      update PedidoAgricultor p
      set p.estadoPedido = :nuevo
      where p.orden.id = :idOrden and p.estadoPedido = :esperado
      """)
  int cambiarEstado(
      @Param("idOrden") Integer idOrden,
      @Param("esperado") EstadoPedido esperado,
      @Param("nuevo") EstadoPedido nuevo);
}