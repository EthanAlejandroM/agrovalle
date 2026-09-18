# Product Backlog - AgroValle Connect

## HU-01: Registro de Agricultores

**Como Agricultor**, quiero registrarme en la plataforma para ofrecer mis productos.

**Priorización:** Must

**Estimación:** 5

### Escenario BDD

**Given:** Que el usuario ingresa a `/api/v1/auth/register`.

**When:** Envía un JSON con nombre, `ubicacion_valle` y documento_válido.

**Then:** El sistema responde con un **Status 201 Created** y el registro persiste en la base de datos PostgreSQL.

---

## HU-02: Publicación de Productos

**Como Agricultor**, quiero publicar mis cosechas para que sean visibles.

**Priorización:** Must

**Estimación:** 5

### Escenario BDD

**Given:** Un agricultor autenticado con token JWT.

**When:** Publica un producto con tipo, cantidad y `fecha_cosecha`.

**Then:** El sistema valida que la fecha no sea anterior a hoy y retorna un ID de producto único.

---

## HU-03: Visualización de Precios Regionales

**Como Comprador**, quiero ver los precios promedio del Valle para negociar mejor.

**Priorización:** Should

**Estimación:** 5

### Escenario BDD

**Given:** Que existen 50 transacciones de "Café" en las últimas 24 horas.

**When:** Solicito el precio promedio de "Café".

**Then:** El sistema calcula la media aritmética y despliega el valor exacto en pesos colombianos.

---

## HU-04: Filtro de Categoría de Producto

**Como Comprador**, quiero filtrar los productos por categoría para encontrar rápidamente los productos agrícolas que necesito.

**Priorización:** Should

**Estimación:** 3

### Escenario BDD

**Given:** Existen productos publicados de diferentes categorías en la plataforma.

**When:** El usuario selecciona una categoría específica, por ejemplo, categoría verdura.

**Then:** El sistema muestra únicamente los productos que pertenecen a la categoría seleccionada.

---

## HU-05: Contacto Directo

**Como Comprador**, quiero contactar directamente al Agricultor para consultar sobre sus productos y realizar negociaciones.

**Priorización:** Must

**Estimación:** 3

### Escenario BDD

**Given:** El comprador ha iniciado sesión y existen productos publicados por un agricultor.

**When:** El comprador selecciona la opción de contactar al agricultor y envía un mensaje.

**Then:** El sistema registra el mensaje y notifica al agricultor para que pueda responder al comprador.

---

## HU-06: Transporte

**Como Comprador**, quiero solicitar envío para recibir los productos agrícolas comprados.

**Priorización:** Must

**Estimación:** 8

### Escenario BDD

**Given:** Que el comprador haya realizado un pedido y exista disponibilidad de envío para su Municipio.

**When:** Solicita el envío y se ingresa la dirección del lugar de entrega.

**Then:** El sistema registra la solicitud y genera un tiquete que es asignado al transportista de AgroValle.

---

## HU-07: Pagos

**Como Comprador**, quiero realizar el pago de forma segura de mis productos a través de la misma aplicación.

**Priorización:** Must

**Estimación:** 8

### Escenario BDD

**Given:** El usuario tiene un pedido confirmado con el valor total de la compra.

**When:** Selecciona un método de pago y realiza la transacción al agricultor.

**Then:** El sistema valida el pago, registra la transacción y actualiza el estado del pedido como pagado.

---

## HU-08: Calificaciones

**Como Comprador**, quiero calificar al Agricultor después de una compra para compartir mi experiencia con los demás usuarios.

**Priorización:** Could

**Estimación:** 2

### Escenario BDD

**Given:** El usuario ha recibido un pedido hecho a un agricultor.

**When:** Asigna una calificación de 1 a 5, donde 1 es muy malo y 5 es muy bueno. Adicionalmente, escribe un comentario sobre su experiencia.

**Then:** El sistema registra la calificación y actualiza la valoración del agricultor.

---

## HU-09: Consultar Orden de Compra

**Como Comprador**, quiero consultar los detalles de mi orden de compra para conocer las características y el estado de mi pedido.

**Priorización:** Must

**Estimación:** 5

### Escenario BDD

**Given:** El usuario realizó una compra y tiene todos los datos de la compra completos.

**When:** El usuario selecciona la opción de ver orden de compra.

**Then:** El sistema muestra los detalles, incluyendo el estado del pedido al comprador.

---

## HU-10: Seguimiento de Envío

**Como Comprador**, quiero hacerle seguimiento a mi pedido.

**Priorización:** Must

**Estimación:** 8

### Escenario BDD

**Given:** El pedido fue confirmado y se le asignó un tiquete de transporte.

**When:** El comprador selecciona la opción de seguimiento del envío.

**Then:** El sistema muestra la ruta actualizada en la que se encuentra el pedido enviado.

---

## HU-11: Registrar Clientes

**Como Comprador**, quiero registrarme en la aplicación, con el fin de poder comprar productos agrícolas.

**Priorización:** Must

**Estimación:** 5

### Escenario BDD

**Given:** Que el usuario ingresa a `/api/v1/auth/register`.

**When:** Envía un JSON con nombre, `ubicacion_valle` y cedula_válido.

**Then:** El sistema responde con un **Status 201 Created** y el registro persiste en la base de datos PostgreSQL.

---

## HU-12: Generar PQR

**Como Comprador**, quiero realizar una queja, petición o reclamo.

**Priorización:** Could

**Estimación:** 2

### Escenario BDD

**Given:** El usuario está inconforme con el servicio prestado a través de la aplicación.

**When:** El usuario redacta una queja, reclamo o sugerencia.

**Then:** El sistema registra la PQR y confirma al usuario que fue recibida correctamente y se le da respuesta de esta.

---

## HU-13: Control de Inventario

**Como Agricultor**, quiero controlar la cantidad disponible de mis productos, con el fin de mantener actualizado el inventario y evitar ofrecer productos que ya no están disponibles.

**Priorización:** Must

**Estimación:** 5

### Escenario BDD

**Given:** El agricultor tiene productos registrados y disponibles en la aplicación.

**When:** El agricultor actualiza la cantidad disponible de un producto.

**Then:** El sistema actualiza el inventario y muestra la nueva cantidad disponible para los clientes.

---

## HU-14: Historial de Compra

**Como Comprador**, quiero consultar mi historial de compras para conocer los productos que he comprado anteriormente.

**Priorización:** Should

**Estimación:** 3

### Escenario BDD

**Given:** El usuario ha realizado una o varias compras en la aplicación.

**When:** El usuario selecciona la opción de historial de compras.

**Then:** El sistema muestra las compras realizadas anteriormente, incluyendo la información de cada pedido.

---

## HU-15: Filtro por Municipio

**Como Comprador**, quiero filtrar los productos por municipio para encontrar los productos agrícolas según el municipio que yo considere buscar.

**Priorización:** Should

**Estimación:** 3

### Escenario BDD

**Given:** Existen productos publicados en diferentes municipios en la plataforma.

**When:** El usuario selecciona un municipio específico, por ejemplo, Dagua.

**Then:** El sistema muestra únicamente los productos que pertenecen al municipio seleccionado.

---

## HU-16: Cancelar Pedido Cliente

**Como Comprador**, quiero poder cancelar un pedido que se encuentra en estado pendiente, para corregir errores o desistir de la compra.

**Priorización:** Must

**Estimación:** 5

### Escenario BDD

**Given:** El cliente realizó un pedido que se encuentra en estado pendiente.

**When:** El cliente selecciona la opción de cancelar el pedido.

**Then:** El sistema cambia el estado del pedido a "Cancelado", libera el stock y confirma la operación al cliente.

**Excepción:** Si el pedido se encuentra en estado "En preparación" o "En cosecha", el cliente no podrá cancelarlo.

---

## HU-17: Cancelar Pedido Agricultor

**Como Agricultor**, quiero cancelar un pedido reportando un imprevisto de disponibilidad para no comprometer la calidad de entrega.

**Priorización:** Should

**Estimación:** 5

### Escenario BDD

**Given:** El agricultor tiene un pedido asignado.

**When:** El agricultor selecciona la opción de cancelar el pedido e ingresa el motivo del imprevisto.

**Then:** El sistema cambia el estado del pedido a "Cancelado por agricultor", notifica al cliente con el motivo y procesa el reembolso correspondiente.

---

## HU-18: Carrito de Compras

**Como Comprador**, quiero modificar cantidades o eliminar productos del carrito fácilmente antes de proceder al pago.

**Priorización:** Must

**Estimación:** 5

### Escenario BDD

**Given:** El cliente tiene productos en el carrito de compras.

**When:** El cliente modifica la cantidad de un producto o selecciona la opción de eliminarlo.

**Then:** El sistema actualiza el contenido del carrito y recalcula el subtotal de la compra.

---

## HU-19: Realizar Pedido

**Como Comprador**, quiero confirmar mi pedido desde el carrito de compras, para seleccionar el método de entrega, la dirección y finalizar la solicitud con el agricultor.

**Priorización:** Must

**Estimación:** 5

### Escenario BDD

**Given:** El cliente tiene productos en su carrito de compras y ha iniciado sesión.

**When:** El cliente confirma la dirección de entrega, selecciona el método de entrega y presiona "Realizar pedido".

**Then:** El sistema registra el pedido en estado "Pendiente", descuenta el stock de los productos, genera la orden de compra y notifica al agricultor. Después, la HU-07 se encarga del pago.