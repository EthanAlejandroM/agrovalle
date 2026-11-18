# Planning Sprint 3

**Fecha de creación:** 17 de noviembre de 2026

## 1. Objetivo y alcance del Sprint 3

El objetivo del Sprint 3 es habilitar el flujo de compra sin cambiar el esquema actual más allá de lo ya aplicado: el catálogo debe ser visible para compradores y agricultores, mientras que el carrito y el pedido solo estarán habilitados para compradores, con el stock protegido ante concurrencia. Se construye sobre el backend actual, donde el lote ya tiene `@Version` y existe el handler `CONFLICTO_CONCURRENCIA`.

### Supuestos confirmados por el equipo:
* El producto **no** guarda stock: la cantidad disponible es la suma de `cantidad_disponible` de sus lotes en estado `DISPONIBLE`.
* El municipio del catálogo es `usuario.ubicacion_valle` del agricultor.
* El carrito **no** reserva stock; el descuento ocurre al confirmar el pedido (HU-19), es decir, que solo se descuenta del stock (inventario del agricultor) cuando el pedido se confirma.
* Un pedido con productos de varios agricultores genera una sola `orden_compra` y un `pedido_agricultor` por agricultor.
* El pago queda fuera (HU-07). El pedido nace en estado `PENDIENTE`. El pago cubre solo los productos (el total de la orden); el envío se cobra aparte al entregar al comprador, por lo que `costo_envio` no entra al total.
* `detalle_compra.cantidad` y `detalle_lote.cantidad_asignada` ya se cambiaron a `numeric`, así que las cantidades son decimales de punta a punta.
* El agricultor ve sus pedidos en el apartado “Mis pedidos” de su panel; esa es la “notificación” de HU-19, por lo que **no** se crea tabla notificación.
* El carrito se persiste en el servidor, con dos tablas nuevas (`carrito` y `detalle_carrito`) que se crean en la base de datos junto con el código (ya fueron creadas como parte de la Tarea 1).
* Agregar al carrito no tiene HU propia en el sprint; así que se debe cubrir dentro de HU-18.
* El desarrollo solo será del backend, así que las tareas son de API; el consumo desde la interfaz queda fuera.
* Los lotes los asigna la aplicación, no el agricultor: al crear el pedido se toman automáticamente de los lotes `DISPONIBLE` del producto, el más antiguo primero (`fecha_cosecha` ascendente, desempate por `Id_lote`). El agricultor ve los lotes asignados en “Mis pedidos”.

---

## 2. Historias de Usuario (HU) a realizar

### HU-18: Carrito de compras (Must · 5)

| Historia de Usuario | Detalles |
| :--- | :--- |
| **Descripción** | Como comprador, quiero modificar cantidades o eliminar productos del carrito fácilmente antes de proceder al pago. |
| **Priorización** | Must |
| **Estimación** | 5 |
| **Escenario BDD** | **Given:** El cliente tiene productos en el carrito.<br>**When:** El cliente modifica la cantidad de un producto o selecciona la opción de eliminarlo.<br>**Then:** El sistema actualiza el contenido del carrito y recalcula el subtotal de la compra. |

El carrito vive en el servidor. Endpoints propuestos para que el comprador pueda agregar, modificar y eliminar productos, y el servidor recalcula el subtotal en cada cambio:
* `POST /api/v1/carrito/items`
* `GET /api/v1/carrito`
* `PATCH /api/v1/carrito/items/{idProducto}`
* `DELETE /api/v1/carrito/items/{idProducto}`

#### Tareas (HU-18)

| ID Tarea | Descripción técnica | Componente/Tecnología | Atributo ISO 25010 |
| :--- | :--- | :--- | :--- |
| **T-18.1** | Crear tablas `carrito` (`Id_usuario_comprador` UNIQUE, `fecha_actualizacion`) y `detalle_carrito` (`UNIQUE (id_carrito, id_producto)`, FK, `cantidad numeric(10,2)` con `CHECK (cantidad > 0)`) en la base de datos y en `schema.sql` | PostgreSQL, `schema.sql` | Fiabilidad |
| **T-18.2** | Crear entidades JPA `Carrito` y `DetalleCarrito` (con `@Version`) y sus repositorios | Spring Data JPA, Hibernate | Mantenibilidad |
| **T-18.3** | Crear DTOs `AgregarItemSolicitud`, `ModificarCantidadSolicitud` y `CarritoRespuesta` (ítems, subtotal por ítem, subtotal total, cantidad disponible actual, `stockSuficiente`) con Bean Validation | Jakarta Validation, Java records | Adecuación funcional |
| **T-18.4** | Consulta compartida de stock: suma de lotes `DISPONIBLE` para varios productos en una sola consulta (`IN`), sin N+1; se reutiliza en HU-19 y HU-20 | Spring Data JPA, JPQL | Eficiencia de desempeño |
| **T-18.5** | Implementar `CarritoService.agregar`: crea el carrito si no existe, suma si el producto ya está y valida contra el stock disponible | Spring `@Service`, `@Transactional` | Adecuación funcional |
| **T-18.6** | Implementar `modificarCantidad` y `eliminarItem` sobre el carrito del usuario autenticado | Spring `@Service`, `@Transactional` | Adecuación funcional |
| **T-18.7** | Calcular subtotales con `BigDecimal` (precio vigente × cantidad, escala 2, `HALF_UP`) y recalcular en cada respuesta | Java `BigDecimal` | Fiabilidad |
| **T-18.8** | Crear `CarritoController` y validar rol `COMPRADOR` en el servicio (mismo patrón de `RolAgricultorInvalidoException`); el carrito se obtiene del token, nunca del cuerpo | Spring Web, Spring Security, JWT | Seguridad |
| **T-18.9** | Crear excepciones `ItemCarritoNoEncontrado`, `StockInsuficiente`, `CantidadCarritoInvalida`, `RolCompradorInvalido` y mapearlas en `GlobalExceptionHandler` | `@RestControllerAdvice`, `ApiError` | Usabilidad |
| **T-18.10** | Evitar duplicados por doble clic: `UNIQUE` en BD y manejo de `DataIntegrityViolation` / `OptimisticLockingFailure` como `CONFLICTO_CONCURRENCIA` reintentable | PostgreSQL, Spring Data | Fiabilidad |
| **T-18.11** | Pruebas unitarias de `CarritoService` (TDD): agregar, sumar, modificar, eliminar, subtotal | JUnit 5, Mockito | Mantenibilidad |
| **T-18.12** | Pruebas de controlador: 401, 403 con agricultor, 400 y 404 | MockMvc, `TestSecurityConfig` | Seguridad |
| **T-18.13** | Escenarios BDD `carritoBDD.feature` (modificar cantidad, eliminar, recalcular subtotal) con sus steps | Cucumber, Gherkin en español | Adecuación funcional |

#### Validaciones (HU-18)

| ID | Validación | Regla | Respuesta |
| :--- | :--- | :--- | :--- |
| **V-18.1** | Autenticación | Sin token o token inválido no accede | `401` |
| **V-18.2** | Rol | Solo `COMPRADOR` usa el carrito; el agricultor solo ve el catálogo | `403 ROL_COMPRADOR_INVALIDO` |
| **V-18.3** | Producto existe | `idProducto` debe existir | `404 PRODUCTO_NO_ENCONTRADO` |
| **V-18.4** | Producto con stock | Al agregar, la suma de lotes `DISPONIBLE` debe ser mayor que 0 | `409 PRODUCTO_SIN_STOCK` |
| **V-18.5** | Cantidad válida | Obligatoria, mayor que 0 y máximo 2 decimales | `400 VALIDACION_FALLIDA` |
| **V-18.6** | Unidad discreta | Si la unidad es `UNIDAD` o `BULTO`, la cantidad debe ser entera | `400 CANTIDAD_CARRITO_INVALIDA` |
| **V-18.7** | No superar el stock | Cantidad acumulada en el carrito (existente + nueva) $\le$ stock disponible; el mensaje informa cuánto hay | `409 STOCK_INSUFICIENTE` |
| **V-18.8** | Ítem existe en mi carrito | Modificar o eliminar solo ítems del carrito del usuario del token | `404 ITEM_CARRITO_NO_ENCONTRADO` |
| **V-18.9** | Cantidad cero al modificar | No se acepta 0; para quitar el producto se usa `DELETE` | `400 CANTIDAD_CARRITO_INVALIDA` |
| **V-18.10** | Subtotal confiable | El servidor ignora precios o subtotales enviados por el cliente y usa el precio vigente del producto | `200` con subtotal recalculado |
| **V-18.11** | Carrito vacío | `GET` devuelve lista vacía y subtotal 0, no error | `200` |
| **V-18.12** | Stock que bajó después de agregar | El `GET` no borra el ítem; marca alerta con la cantidad disponible actual (se bloquea en HU-19) | `200` con alerta |
| **V-18.13** | Concurrencia | Dos solicitudes simultáneas sobre el mismo producto no duplican la fila ni pierden una cantidad | `200` o `409 CONFLICTO_CONCURRENCIA` |

---

### HU-19: Realizar pedido (Must · 5)

| Historia de Usuario | Detalles |
| :--- | :--- |
| **Descripción** | Como comprador, quiero confirmar mi pedido desde el carrito de compras, para seleccionar la dirección y finalizar la solicitud con el agricultor. |
| **Priorización** | Must |
| **Estimación** | 5 |
| **Escenario BDD** | **Given:** El cliente tiene productos en su carrito de compras y ha iniciado sesión<br>**When:** El cliente confirma la dirección de entrega y presiona "Realizar pedido"<br>**Then:** El sistema registra el pedido en estado "Pendiente", descuenta el stock de los productos, genera la orden de compra y notifica al agricultor. Después, la HU-07 se encarga del pago. |

#### De PENDIENTE a pagado sin perder el stock
**Recomendación:** Reservar el stock al crear el pedido y volverlo definitivo al pagar. Si el stock se descontara solo después del pago, dos compradores podrían pagar la misma existencia y uno pagaría por algo que ya no hay. Por eso el descuento del *Then* de esta HU es una reserva, y `detalle_lote` (qué lote aportó cuánto a cada detalle) permite devolverla con exactitud.

| Momento | Qué pasa con el stock | Estado de `pedido_agricultor` | Responsable |
| :--- | :--- | :--- | :--- |
| **Realizar pedido** | Se descuenta de los lotes (reserva) y se registra en `detalle_lote` | `PENDIENTE` (pendiente de pago) | HU-19 |
| **Pago aprobado** | El descuento pasa a ser definitivo; no se vuelve a tocar el stock, solo cambia el estado de todos los pedidos de la orden | `PAGADO` | HU-07 llama a `confirmarPago(idOrden)`, que entrega HU-19 |
| **Pago rechazado, cancelación o plazo de pago vencido** | Se devuelve a cada lote lo registrado en `detalle_lote`; un lote `AGOTADO` vuelve a `DISPONIBLE` con trazabilidad | `CANCELADO` | HU-19 entrega `liberarReserva(idOrden)`; HU-07 la llama ante un pago rechazado y un job programado ante el vencimiento |

**Reglas de la transición:** 
Solo se pasa de `PENDIENTE` a `PAGADO` o `CANCELADO`, nunca entre esos dos ni de vuelta. Como el pago puede llegar a la vez que el vencimiento, cada cambio se hace con un `UPDATE ... WHERE estado = 'PENDIENTE'` (o `@Version` en `PedidoAgricultor`) y gana el primero. El pago es uno por orden (`pago.Id_orden` es `UNIQUE`), así que la confirmación actualiza todos los pedidos de esa orden en una sola transacción. El pago cubre solo los productos: el envío se cobra aparte al entregar, por lo que `orden_compra.total` nunca incluye `costo_envio`. El agricultor ve el pedido en “Mis pedidos” desde `PENDIENTE`, pero solo debería prepararlo cuando esté `PAGADO`. El plazo de pago (cuánto puede estar reservado un pedido sin pagarse) debe definirlo el equipo; se configura como propiedad.

#### ¿Quién elige los lotes?
La aplicación, automáticamente y en el momento de crear el pedido. Como el stock se reserva en ese instante (antes de que el agricultor tome el pedido), el sistema toma de los lotes `DISPONIBLE` del producto empezando por el más antiguo (`fecha_cosecha` ascendente, desempate por `Id_lote`), lo que favorece la salida de lo más fresco y evita que se pierda producto. Si un solo lote no alcanza, la cantidad se reparte entre varios y cada parte queda en `detalle_lote`. El agricultor no elige: ve en “Mis pedidos” de qué lotes sale su mercancía (código de lote, ubicación y cantidad) para alistarla. Si más adelante quieren que el agricultor pueda cambiar la asignación, habría que mover la reserva entre lotes revalidando el stock; queda fuera de este sprint.

#### Tareas (carrito persistente)
El carrito vive en el servidor (`carrito` y `detalle_carrito`, tablas nuevas de HU-18). `POST /api/v1/pedidos` recibe solo `direccionEntrega`: los ítems salen del carrito del comprador autenticado.

| ID Tarea | Descripción técnica | Componente/Tecnología | Atributo ISO 25010 |
| :--- | :--- | :--- | :--- |
| **T-19.1** | Prerrequisito: tablas, entidades y servicio del carrito persistente de HU-18 | PostgreSQL, Spring Data JPA | Fiabilidad |
| **T-19.2** | Crear enum `EstadoPedido` (`PENDIENTE`, `PAGADO`, `CANCELADO`) y entidades `OrdenCompra`, `PedidoAgricultor` (con `@Version`), `DetalleCompra`, `DetalleLote` y `Envio`, con sus repositorios; `fecha_orden` y `fecha_asignacion` como `OffsetTime`, cantidades como `BigDecimal` | Spring Data JPA, Hibernate | Mantenibilidad |
| **T-19.3** | Crear DTOs `RealizarPedidoSolicitud` (solo dirección) y `PedidoRespuesta` (id de orden, total de productos, pedidos por agricultor con estado y subtotal) | Jakarta Validation, Java records | Adecuación funcional |
| **T-19.4** | Bloquear el carrito del comprador al iniciar (`@Lock(PESSIMISTIC_WRITE)`) para serializar el doble clic | Spring Data `@Lock`, PostgreSQL | Fiabilidad |
| **T-19.5** | Consulta de lotes con bloqueo: lotes `DISPONIBLE` por producto, ordenados por `fecha_cosecha` y `Id_lote`, bloqueados siempre en orden ascendente de `Id_lote` para evitar deadlocks | Spring Data `@Lock`, JPQL | Fiabilidad |
| **T-19.6** | Implementar `PedidoService.realizar` en una sola `@Transactional`: leer carrito, revalidar stock bajo bloqueo, asignar por lote automáticamente (FIFO), descontar (reserva), crear orden, pedidos por agricultor, detalles con precio fijado y `detalle_lote`, y vaciar el carrito; todo o nada | Spring `@Transactional (READ_COMMITTED)` | Fiabilidad |
| **T-19.7** | Crear un `envio` por pedido con la dirección, `costo_envio = 0.00` y `fecha_envio` provisional; el envío se cobra aparte al entregar y no entra al total | JPA, PostgreSQL | Adecuación funcional |
| **T-19.8** | Registrar trazabilidad `AGOTADO` cuando un lote llega a 0; extraer `registrarTrazabilidad` de `ProductoService` a un componente compartido | `EstadoTrazabilidad`, refactor | Mantenibilidad |
| **T-19.9** | Implementar `confirmarPago(idOrden)`: pasa los pedidos `PENDIENTE` de la orden a `PAGADO` con `UPDATE ... WHERE estado = 'PENDIENTE'`; lo invoca HU-07 | Spring `@Transactional`, JPA | Fiabilidad |
| **T-19.10** | Implementar `liberarReserva(idOrden)`: pasa a `CANCELADO`, devuelve a cada lote lo de `detalle_lote` sin superar `cantidad_producida`, vuelve a `DISPONIBLE` un lote `AGOTADO` y registra trazabilidad | Spring `@Transactional`, JPA | Fiabilidad |
| **T-19.11** | Job programado que cancela pedidos `PENDIENTE` cuya `fecha_creacion` superó el plazo de pago (propiedad configurable), procesando por tandas | Spring `@Scheduled`, `application.yml` | Fiabilidad |
| **T-19.12** | Manejar `OptimisticLockingFailure`, `PessimisticLockingFailure` y timeout de bloqueo como `409 CONFLICTO_CONCURRENCIA`; definir `lock.timeout` | `GlobalExceptionHandler`, JPA hints | Fiabilidad |
| **T-19.13** | Crear `PedidoController` (`POST /api/v1/pedidos`) con control de rol `COMPRADOR` | Spring Web, Spring Security | Seguridad |
| **T-19.14** | Crear `GET /api/v1/pedidos/recibidos` para “Mis pedidos” del agricultor (propios, recientes primero, paginado), con los lotes asignados de cada pedido (código de lote, ubicación y cantidad, desde `detalle_lote`). Solo entra si otra HU no lo cubre | Spring Data `Pageable`, Spring Security | Adecuación funcional |
| **T-19.15** | Crear excepciones `CarritoVacio`, `StockInsuficiente`, `ProductoNoDisponible`, `EstadoPedidoInvalido` y mapearlas en `GlobalExceptionHandler` | `@RestControllerAdvice` | Usabilidad |
| **T-19.16** | Pruebas unitarias de `PedidoService`: orden y pedidos por agricultor, totales, FIFO entre lotes, carrito vaciado, rollback ante error; y de `confirmarPago` y `liberarReserva` | JUnit 5, Mockito | Mantenibilidad |
| **T-19.17** | Pruebas de concurrencia: N hilos sobre el último stock (1 éxito, resto 409); doble clic sobre el mismo carrito; pago frente a vencimiento; edición del lote por el agricultor durante la compra | `ExecutorService`, `CountDownLatch`, PostgreSQL de pruebas (idealmente Testcontainers) | Fiabilidad |
| **T-19.18** | Escenarios BDD `pedidoBDD.feature`: pedido exitoso, stock insuficiente, carrito vacío, agricultor intenta comprar, pago confirma, pago rechazado devuelve stock | Cucumber | Adecuación funcional |

#### Validaciones (HU-19)

| ID | Validación | Regla | Respuesta |
| :--- | :--- | :--- | :--- |
| **V-19.1** | Autenticación | Sin sesión o con token inválido no se puede pedir | `401` |
| **V-19.2** | Rol | Solo `COMPRADOR` confirma pedidos; el agricultor no puede comprar | `403 ROL_COMPRADOR_INVALIDO` |
| **V-19.3** | Carrito con ítems | El carrito del comprador existe y tiene al menos un ítem | `400 CARRITO_VACIO` |
| **V-19.4** | Dirección de entrega | Obligatoria, no en blanco, máximo 255 caracteres | `400 VALIDACION_FALLIDA` |
| **V-19.5** | Productos vigentes | Cada producto del carrito existe y tiene al menos un lote `DISPONIBLE` | `409 PRODUCTO_NO_DISPONIBLE` |
| **V-19.6** | Stock revalidado bajo bloqueo | Para cada ítem, `cantidad` $\le$ suma de lotes ya bloqueados; el error lista los ítems afectados y el carrito queda intacto | `409 STOCK_INSUFICIENTE` |
| **V-19.7** | Todo o nada | Si un ítem falla no se descuenta nada, no se crea pedido y el carrito no se vacía | Rollback total |
| **V-19.8** | Asignación de lotes | Se asigna del lote más antiguo al más nuevo; la suma de `detalle_lote.cantidad_asignada` por ítem es igual a `detalle_compra.cantidad` y el stock nunca queda negativo | Integridad en BD |
| **V-19.9** | Lote en 0 | Al llegar a 0 el lote pasa a `AGOTADO` y queda trazabilidad | Registro en `estado_trazabilidad` |
| **V-19.10** | Totales solo de productos | `detalle.total = cantidad × precio`, `subtotal_pedido = suma de detalles`, `orden.total = suma de subtotales`; escala 2; no incluye `costo_envio` | Calculado en servidor |
| **V-19.11** | Precio fijado | `precio_unitario` se copia del precio vigente al confirmar | `201` |
| **V-19.12** | Estado inicial | Cada `pedido_agricultor` nace en `PENDIENTE` | `201` |
| **V-19.13** | Un pedido por agricultor | Los ítems se agrupan por el dueño del producto; cada pedido aparece en su “Mis pedidos” | `201` |
| **V-19.14** | Dirección guardada | Cada pedido tiene su `envio` con la dirección confirmada | `201` |
| **V-19.15** | Doble clic | Dos confirmaciones simultáneas generan un solo pedido; la segunda ve el carrito vacío | `201` y luego `400 CARRITO_VACIO` |
| **V-19.16** | Carrera por el último stock | Dos compradores sobre la misma existencia: uno confirma y el otro recibe error con el stock restante | `201` / `409 STOCK_INSUFICIENTE` |
| **V-19.17** | Edición del agricultor en paralelo | Si el agricultor modifica el lote mientras se confirma, gana la primera transacción y la segunda recibe conflicto | `409 CONFLICTO_CONCURRENCIA` |
| **V-19.18** | Confirmar pago | Solo desde `PENDIENTE`; la orden existe; el monto pagado coincide con `orden.total`; actualiza todos los pedidos de la orden a la vez | `200` / `409 ESTADO_PEDIDO_INVALIDO` |
| **V-19.19** | Liberar reserva | Solo desde `PENDIENTE`; devuelve exactamente lo registrado en `detalle_lote` y nunca deja el lote por encima de `cantidad_producida` | `200` / `409 ESTADO_PEDIDO_INVALIDO` |
| **V-19.20** | Pago frente a vencimiento | Si ambos ocurren a la vez, solo uno cambia el estado; el otro recibe conflicto y no toca el stock | `409 ESTADO_PEDIDO_INVALIDO` |
| **V-19.21** | Vencimiento | El job solo cancela pedidos `PENDIENTE` con el plazo cumplido, y cada uno en su propia transacción | Estado `CANCELADO` |
| **V-19.22** | “Mis pedidos” (si entra al sprint) | Solo `AGRICULTOR`; ve únicamente sus pedidos (filtro por el usuario del token), con los lotes asignados; paginado y por fecha descendente | `403` / `200` |

---

### HU-20: Visualizar catálogo de productos (Must · 5)

| Historia de Usuario | Detalles |
| :--- | :--- |
| **Descripción** | Como comprador o agricultor, quiero visualizar el catálogo general de productos agrícolas disponibles para conocer la oferta activa en la plataforma. |
| **Priorización** | Must |
| **Estimación** | 5 |
| **Escenario BDD** | **Given:** Que existen productos publicados con stock disponible mayor a cero en la base de datos.<br>**When:** El usuario accede a la vista principal o catálogo de productos.<br>**Then:** El sistema despliega el listado de productos mostrando nombre, categoría, precio, municipio, cantidad disponible y nombre del agricultor. |

Compradores y agricultores consultan el mismo catálogo de solo lectura; el comprador siempre ve la cantidad vigente, incluida la que el agricultor acaba de actualizar (HU-13). Endpoint propuesto: `GET /api/v1/catalogo` con filtros y paginación. Es la HU más simple y destraba el escenario `@pendiente` de `controlInventarioBDD.feature`.

#### Tareas (HU-20)

| ID Tarea | Descripción técnica | Componente/Tecnología | Atributo ISO 25010 |
| :--- | :--- | :--- | :--- |
| **T-20.1** | Opcional: crear índices en `lote_produccion("Id_producto")` (parcial donde `cantidad_disponible > 0`) y `producto("Id_categoria")`; no modifica tablas | PostgreSQL | Eficiencia de desempeño |
| **T-20.2** | Consulta del catálogo: producto + categoría + agricultor con la suma de lotes con stock (reutiliza la consulta compartida de stock de HU-18), `GROUP BY` y `HAVING SUM > 0`, con `JOIN` para evitar N+1 | Spring Data JPA, JPQL con proyección | Eficiencia de desempeño |
| **T-20.3** | Crear `ProductoCatalogoRespuesta`: id, nombre, categoría, precio, unidad de medida, municipio (`ubicacion_valle`), cantidad disponible, nombre del agricultor e imagen; sin correo, documento ni teléfono | Java records | Seguridad |
| **T-20.4** | Implementar `CatalogoService` con paginación (`Pageable`, tamaño máximo 50), filtros opcionales (categoría, municipio, texto) y orden permitido | Spring Data `Page`, `@Transactional(readOnly = true)` | Adecuación funcional |
| **T-20.5** | Crear `CatalogoController` (`GET`) accesible para `COMPRADOR` y `AGRICULTOR`, sin endpoints de escritura; incluir `puedeComprar` según el rol para que la interfaz oculte el botón | Spring Web, Spring Security | Seguridad |
| **T-20.6** | Garantizar frescura: leer siempre de BD (sin caché de aplicación) y responder `Cache-Control: no-store` | Spring `ResponseEntity`, cabeceras HTTP | Fiabilidad |
| **T-20.7** | Activar el escenario `@pendiente` (“comprador ve la nueva cantidad”) quitando la etiqueta e implementando el step que consulta el catálogo tras el `PATCH` del lote | Cucumber, `RunCucumberTest` | Adecuación funcional |
| **T-20.8** | Pruebas unitarias de `CatalogoService`: solo productos con stock, suma de lotes, filtros y paginación | JUnit 5, Mockito | Mantenibilidad |
| **T-20.9** | Pruebas de controlador: 401, 200 para ambos roles, 400 por parámetros inválidos, ausencia de datos sensibles | MockMvc | Seguridad |
| **T-20.10** | Prueba con volumen de datos y revisión del plan de consulta (`EXPLAIN`) para confirmar una sola consulta por página | PostgreSQL, `EXPLAIN ANALYZE` | Eficiencia de desempeño |
| **T-20.11** | Escenarios BDD `catalogoBDD.feature` (listado, sin stock, agotado, reabastecido, agricultor ve pero no compra) | Cucumber | Adecuación funcional |

#### Validaciones (HU-20)

| ID | Validación | Regla | Respuesta |
| :--- | :--- | :--- | :--- |
| **V-20.1** | Autenticación | Sin token o token inválido no ve el catálogo | `401` |
| **V-20.2** | Acceso por rol | `COMPRADOR` y `AGRICULTOR` pueden consultar; ambos ven los productos de todos los agricultores | `200` |
| **V-20.3** | Solo productos con stock | Aparece el producto si la suma de sus lotes con `cantidad_disponible > 0` es mayor que 0; sin lotes o agotado no se muestra | `200` |
| **V-20.4** | Cantidad correcta | `cantidadDisponible` = suma de lotes disponibles, con la unidad de medida del producto | `200` |
| **V-20.5** | Campos obligatorios | Cada ítem trae nombre, categoría, precio, municipio, cantidad disponible y nombre del agricultor | `200` |
| **V-20.6** | Datos sensibles | No se exponen correo, documento, teléfono ni hash de contraseña | `200` |
| **V-20.7** | Frescura | Después de un `PATCH` de lote, el siguiente `GET` muestra el valor nuevo; si queda en 0 el producto desaparece y si se reabastece reaparece | `200` |
| **V-20.8** | Paginación | `page` $\ge 0$ y `size` entre 1 y 50 (por defecto 20) | `400 VALIDACION_FALLIDA` |
| **V-20.9** | Orden | Solo campos permitidos (por ejemplo nombre y precio); cualquier otro se rechaza | `400 VALIDACION_FALLIDA` |
| **V-20.10** | Filtro de categoría | Si el id enviado no existe se rechaza | `404 CATEGORIA_NO_ENCONTRADA` |
| **V-20.11** | Filtro de texto | Máximo 100 caracteres, consulta parametrizada y escape de `%` y `_` | `400 VALIDACION_FALLIDA` |
| **V-20.12** | Catálogo vacío | Si nada coincide se devuelve lista vacía, no error | `200` |
| **V-20.13** | Agricultor no compra | Un agricultor que llama al carrito o al pedido recibe error aunque vea el catálogo (se prueba junto con HU-18 y HU-19) | `403 ROL_COMPRADOR_INVALIDO` |

---

## 3. Concurrencia y orden de ejecución

La estrategia combina bloqueo pesimista en el pedido (donde la contención es alta y no se puede vender de más) con el `@Version` ya existente en `LoteProduccion` (para ediciones del agricultor). Una alternativa equivalente es un `UPDATE ... WHERE cantidad_disponible >= :cantidad` atómico por lote.

| Escenario | Riesgo | Mecanismo | Resultado esperado |
| :--- | :--- | :--- | :--- |
| **Dos compradores piden el último stock** | Sobreventa | Bloqueo `PESSIMISTIC_WRITE` de lotes + revalidación | Uno `201`, el otro `409 STOCK_INSUFICIENTE` |
| **Agricultor edita el lote mientras se compra** | Pisar el stock | `@Version` + handler `CONFLICTO_CONCURRENCIA` | Gana la primera transacción; la segunda reintenta con el stock nuevo |
| **Pedidos que tocan los mismos lotes en distinto orden** | Deadlock | Bloqueo siempre en orden ascendente de `Id_lote` | Sin deadlock |
| **Doble clic en “Realizar pedido”** | Pedido duplicado | Bloqueo de la fila del carrito | Un solo pedido; el segundo ve el carrito vacío |
| **Doble “agregar al carrito”** | Ítem duplicado | `UNIQUE (carrito, producto)` + `@Version` | Una fila con la cantidad correcta |
| **Comprador consulta el catálogo o el carrito durante una compra** | Ver un valor viejo | Lectura `READ_COMMITTED`, sin caché, `no-store`; HU-19 revalida | Ve el último valor confirmado |
| **Pago aprobado y vencimiento del plazo al mismo tiempo** | Confirmar un pedido que ya devolvió su stock, o devolverlo dos veces | `UPDATE ... WHERE estado = 'PENDIENTE'` (o `@Version` en `PedidoAgricultor`) | Gana el primero; el otro recibe `409 ESTADO_PEDIDO_INVALIDO` y no toca el stock |