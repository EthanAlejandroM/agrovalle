
package com.agrovalle.controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agrovalle.ActualizarLoteSolicitud;
import com.agrovalle.ActualizarProductoSolicitud;
import com.agrovalle.EstadoLote;
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
      @Valid @RequestBody RegistroProductoSolicitud solicitud,
      Principal principal) {

    // principal.getName() es el correo del token validado por JwtAuthenticationFilter
    Integer idProducto = productoService.registrar(solicitud, principal.getName());

    Map<String, Object> respuesta = Map.of(
        "mensaje", "Producto y lote registrados correctamente.",
        "idProducto", idProducto);

    return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
  }

  @PatchMapping("/{idProducto}")
  public ResponseEntity<Map<String, Object>> actualizarProducto(
      @PathVariable Integer idProducto,
      @Valid @RequestBody ActualizarProductoSolicitud solicitud,
      Principal principal) {

    productoService.actualizarProducto(idProducto, solicitud, principal.getName());

    return ResponseEntity.ok(Map.of(
        "mensaje", "Producto actualizado correctamente.",
        "idProducto", idProducto));
  }

  @PatchMapping("/{idProducto}/lotes/{idLote}")
  public ResponseEntity<Map<String, Object>> actualizarLote(
      @PathVariable Integer idProducto,
      @PathVariable Integer idLote,
      @Valid @RequestBody ActualizarLoteSolicitud solicitud,
      Principal principal) {

    EstadoLote estado = productoService.actualizarLote(
        idProducto, idLote, solicitud, principal.getName());

    return ResponseEntity.ok(Map.of(
        "mensaje", "Lote actualizado correctamente.",
        "idLote", idLote,
        "estadoLote", estado.name()));
  }
}
