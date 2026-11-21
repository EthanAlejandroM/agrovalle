package com.agrovalle.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Los ítems no viajan aquí: salen del carrito persistido del comprador. */
public record RealizarPedidoSolicitud(
    @NotBlank(message = "La dirección de entrega es obligatoria.")
    @Size(max = 255, message = "La dirección de entrega no puede superar 255 caracteres.")
    String direccionEntrega) {

}

