package com.agrovalle.repository;

import com.agrovalle.entity.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CarritoRepository extends JpaRepository<Carrito, Integer> {
  Optional<Carrito> findByCompradorId(Integer idComprador);
}