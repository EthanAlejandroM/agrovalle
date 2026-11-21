package com.agrovalle.repository;

import com.agrovalle.entity.Carrito;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CarritoRepository extends JpaRepository<Carrito, Integer> {
  Optional<Carrito> findByCompradorId(Integer idComprador);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select c from Carrito c where c.comprador.id = :idComprador")
  Optional<Carrito> bloquearPorComprador(@Param("idComprador") Integer idComprador);
}