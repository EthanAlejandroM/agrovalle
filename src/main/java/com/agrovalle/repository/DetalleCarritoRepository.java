package com.agrovalle.repository;

import com.agrovalle.entity.DetalleCarrito;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleCarritoRepository extends JpaRepository<DetalleCarrito, Integer> {
}