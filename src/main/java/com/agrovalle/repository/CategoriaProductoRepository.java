package com.agrovalle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.agrovalle.entity.CategoriaProducto;

public interface CategoriaProductoRepository extends JpaRepository<CategoriaProducto, Integer> {

}