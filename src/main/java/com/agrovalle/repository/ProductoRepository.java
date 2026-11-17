package com.agrovalle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.agrovalle.entity.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {

}