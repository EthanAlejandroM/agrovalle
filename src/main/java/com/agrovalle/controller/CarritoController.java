package com.agrovalle.controller;

import com.agrovalle.dto.request.AgregarItemSolicitud;
import com.agrovalle.dto.request.ModificarCantidadSolicitud;
import com.agrovalle.dto.response.CarritoRespuesta;
import com.agrovalle.repository.UsuarioRepository;
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
  private final UsuarioRepository usuarioRepo;



  public CarritoController(CarritoService carritoService, UsuarioRepository usuarioRepo) {
    this.carritoService = carritoService;
    this.usuarioRepo = usuarioRepo;
  }

  @GetMapping
  public ResponseEntity<CarritoRespuesta> obtener(Authentication auth) {
    return respuesta(carritoService.obtener(idUsuario(auth)));
  }

  @PostMapping("/items")
  public ResponseEntity<CarritoRespuesta> agregar(
      Authentication auth,
      @Valid @RequestBody AgregarItemSolicitud req) {
    return respuesta(carritoService.agregar(idUsuario(auth), req));
  }

  @PutMapping("/items/{idProducto}")
  public ResponseEntity<CarritoRespuesta> modificar(
      Authentication auth,
      @PathVariable Integer idProducto,
      @Valid @RequestBody ModificarCantidadSolicitud req) {
    return respuesta(
        carritoService.modificarCantidad(idUsuario(auth), idProducto, req.cantidad()));
  }

  @DeleteMapping("/items/{idProducto}")
  public ResponseEntity<CarritoRespuesta> eliminar(
      Authentication auth,
      @PathVariable Integer idProducto) {
    return respuesta(carritoService.eliminarItem(idUsuario(auth), idProducto));
  }

   /** El id sale del token JWT, nunca del cuerpo ni de la URL. */
  private Integer idUsuario(Authentication auth) {
    return usuarioRepo.findByCorreo(auth.getName())
        .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"))
        .getId();
  }

  /** Cache-Control: no-store para no mostrar valores viejos. */
  private ResponseEntity<CarritoRespuesta> respuesta(CarritoRespuesta cuerpo) {
    return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(cuerpo);
  }
}