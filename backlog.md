# Product Backlog - AgroValle Connect

## HU-01: Registro de Agricultores

**Como agricultor**, quiero registrarme en la plataforma para ofrecer mis productos.

**Priorización:** Must

**Estimación:** 5

### Escenario BDD

**Given:** El usuario ingresa a `/api/v1/auth/register`.

**When:** Envía un JSON con nombre, `ubicacion_valle` y cédula válida.

**Then:** El sistema responde con un **Status 201 Created** y el registro persiste en la base de datos PostgreSQL.

---

## HU-02: Publicación de Productos

**Como agricultor**, quiero publicar mis cosechas para que sean visibles a los usaurios.

**Priorización:** Must

**Estimación:** 5

### Escenario BDD

**Given:** Un agricultor autenticado con token JWT.

**When:** Publica un producto con tipo, cantidad y `fecha_cosecha`.

**Then:** El sistema valida que la fecha no sea anterior a hoy y retorna un ID de producto único.

---

## HU-03: Visualización de Precios Regionales

**Como usuario**, quiero ver los precios promedio del Valle para negociar mejor.

**Priorización:** Should

**Estimación:** 5

### Escenario BDD

**Given:** Existen 50 transacciones de "Café" en las últimas 24 horas.

**When:** Solicito el precio promedio de "Café".

**Then:** El sistema calcula la media aritmética y despliega el valor exacto en pesos colombianos.

---

## HU-04: Filtro de Categoría de Producto

**Como usuario**, quiero filtrar los productos por categoría para encontrar rápidamente los productos agrícolas que necesito.

**Priorización:** Should

**Estimación:** 3

### Escenario BDD

**Given:** Existen productos publicados de diferentes categorías en la plataforma.

**When:** El usuario selecciona una categoría específica, por ejemplo, categoría verduras.

**Then:** El sistema muestra únicamente los productos que pertenecen a la categoría seleccionada.

---

## HU-05: Contacto Directo

**Como usuario**, quiero contactar directamente al agricultor para consultar sobre sus productos y realizar negociaciones.

**Priorización:** Must

**Estimación:** 3

### Escenario BDD

**Given:** El comprador ha iniciado sesión y existen productos publicados por un agricultor.

**When:** El comprador selecciona la opción de contactar al agricultor y envía un mensaje.

**Then:** El sistema registra y entrega el mensaje al agricultor, quien debe darle una respuesta al usuario.

---

## HU-06: Transporte

**Como usuario**, quiero solicitar envío para recibir los productos agrícolas comprados.

**Priorización:** Must

**Estimación:** 8

### Escenario BDD

**Given:** El comprador ha realizado un pedido y existe disponibilidad de envío para su municipio.

**When:** Solicita el envío e ingresa la dirección del lugar de entrega.

**Then:** El sistema registra la solicitud y genera un tiquete que es asignado al transportista de AgroValle.

---

## HU-07: Pagos

**Como usuario**, quiero realizar el pago de forma segura de mis productos a través de la misma aplicación.

**Priorización:** Must

**Estimación:** 8

### Escenario BDD

**Given:** El usuario tiene un pedido confirmado con el valor total de la compra.

**When:** Selecciona un método de pago y realiza la transacción al agricultor.

**Then:** El sistema valida el pago, registra la transacción y actualiza el estado del pedido como pagado.

---

## HU-08: Calificaciones

**Como usuario**, quiero calificar al agricultor después de una compra para compartir mi experiencia con los demás usuarios.

**Priorización:** Could

**Estimación:** 2

### Escenario BDD

**Given:** El usuario ha recibido un pedido hecho a un agricultor.

**When:** Asigna una calificación de 1 a 5 y escribe un comentario sobre su experiencia.

**Then:** El sistema registra la calificación y actualiza la valoración del agricultor.

---

## HU-09: Orden de Compra

**Como usuario**, quiero ver los detalles de los pedidos para conocer las características de mi orden.

**Priorización:** Must

**Estimación:** 5

### Escenario BDD

**Given:** El usuario realizó una compra y tiene todos los datos de la compra completos.

**When:** El usuario selecciona la opción de ver orden de compra.

**Then:** El sistema muestra los detalles de la compra al usuario.

---

## HU-10: Seguimiento de Envío

**Como usuario**, quiero hacerle seguimiento a mi pedido.

**Priorización:** Must

**Estimación:** 8

### Escenario BDD

**Given:** El pedido fue confirmado y se le asignó un tiquete de transporte.

**When:** El usuario selecciona la opción de seguimiento del envío.

**Then:** El sistema muestra la ruta actualizada en la que se encuentra el pedido enviado.

---

## HU-11: Registrar Clientes

**Como usuario**, quiero registrarme en la aplicación, con el fin de poder comprar productos agrícolas.

**Priorización:** Must

**Estimación:** 5

### Escenario BDD

**Given:** El usuario ingresa a `/api/v1/auth/register`.

**When:** Envía un JSON con nombre, `ubicacion_valle` y cédula válida.

**Then:** El sistema responde con un **Status 201 Created** y el registro persiste en la base de datos PostgreSQL.

---

## HU-12: Generar PQR

**Como usuario**, quiero realizar una queja, petición o reclamo.

**Priorización:** Could

**Estimación:** 2

### Escenario BDD

**Given:** El usuario está inconforme con el servicio prestado a través de la aplicación.

**When:** El usuario redacta y envía una queja, reclamo o sugerencia.

**Then:** El sistema registra la PQR, confirma al usuario que fue recibida correctamente y se le da respuesta.

---

## HU-13: Control de Inventario

**Como agricultor**, quiero controlar la cantidad disponible de mis productos, con el fin de mantener actualizado el inventario y evitar ofrecer productos que ya no están disponibles.

**Priorización:** Must

**Estimación:** 5

### Escenario BDD

**Given:** El agricultor tiene productos registrados y disponibles en la aplicación.

**When:** El agricultor actualiza la cantidad disponible de un producto.

**Then:** El sistema actualiza el inventario y muestra la nueva cantidad disponible para los clientes.

---

## HU-14: Historial de Compra

**Como usuario**, quiero consultar mi historial de compras para conocer los productos que he comprado anteriormente.

**Priorización:** Should

**Estimación:** 3

### Escenario BDD

**Given:** El usuario ha realizado una o varias compras en la aplicación.

**When:** El usuario selecciona la opción de historial de compras.

**Then:** El sistema muestra las compras realizadas anteriormente, incluyendo la información de cada pedido.

---

## HU-15: Filtro por Municipio

**Como usuario**, quiero filtrar los productos por municipio para encontrar los productos agrícolas según el municipio que considere buscar.

**Priorización:** Should

**Estimación:** 3

### Escenario BDD

**Given:** Existen productos publicados en diferentes municipios en la plataforma.

**When:** El usuario selecciona un municipio específico, por ejemplo, Dagua.

**Then:** El sistema muestra únicamente los productos que pertenecen al municipio seleccionado.