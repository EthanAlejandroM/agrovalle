package com.agrovalle.controller;

import com.agrovalle.dto.request.AgregarItemSolicitud;
import com.agrovalle.dto.request.ModificarCantidadSolicitud;
import com.agrovalle.dto.response.CarritoRespuesta;
import com.agrovalle.service.CarritoService;
import jakarta.validation.Valid;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/carrito")
public class CarritoController {

  private final CarritoService carritoService;

  public CarritoController(CarritoService carritoService) {
    this.carritoService = carritoService;
  }

  @GetMapping
  public ResponseEntity<CarritoRespuesta> obtener(Authentication auth) {
    return respuesta(carritoService.obtener(auth.getName()));
  }

  @PostMapping("/items")
  public ResponseEntity<CarritoRespuesta> agregar(
      Authentication auth,
      @Valid @RequestBody AgregarItemSolicitud req) {
    return respuesta(carritoService.agregar(auth.getName(), req));
  }

  @PutMapping("/items/{idProducto}")
  public ResponseEntity<CarritoRespuesta> modificar(
      Authentication auth,
      @PathVariable Integer idProducto,
      @Valid @RequestBody ModificarCantidadSolicitud req) {
    return respuesta(
        carritoService.modificarCantidad(auth.getName(), idProducto, req.cantidad()));
  }

  @DeleteMapping("/items/{idProducto}")
  public ResponseEntity<CarritoRespuesta> eliminar(
      Authentication auth,
      @PathVariable Integer idProducto) {
    return respuesta(carritoService.eliminarItem(auth.getName(), idProducto));
  }

  // auth.getName() es el correo del token JWT validado: nunca viene del cuerpo ni de la URL.

  /** Cache-Control: no-store para no mostrar valores viejos. */
  private ResponseEntity<CarritoRespuesta> respuesta(CarritoRespuesta cuerpo) {
    return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(cuerpo);
  }
}