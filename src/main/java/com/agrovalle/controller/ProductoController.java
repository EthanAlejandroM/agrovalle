
package com.agrovalle.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agrovalle.RegistroProductoSolicitud;
import com.agrovalle.service.ProductoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/productos")
public class ProductoController {

  private final ProductoService productoService;

  public ProductoController(ProductoService productoService) {
    this.productoService = productoService;
  }

  @PostMapping
  public ResponseEntity<Map<String, Object>> registrarProducto(
      @Valid @RequestBody RegistroProductoSolicitud solicitud) {

    Integer idProducto = productoService.registrar(solicitud);

    Map<String, Object> respuesta = Map.of(
        "mensaje", "Producto y lote registrados correctamente.",
        "idProducto", idProducto);

    return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
  }
}
