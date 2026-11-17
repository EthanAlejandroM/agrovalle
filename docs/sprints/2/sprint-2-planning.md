# Sprint Goal del Sprint 2

Este segundo Sprint tiene como objetivo **permitir que los Agricultores publiquen sus cosechas y mantengan actualizado su inventario**, evitando ofrecer productos que ya no están disponibles.

**Periodo del Sprint:** [completar fecha formal de inicio] – [completar fecha formal de cierre].

> **Nota de actualización (16 de noviembre de 2026):** la bitácora registra seguimiento del Sprint 2 entre el 16 de octubre y el 16 de noviembre. La planeación original se mantuvo en el tablero de GitHub Projects **SPRINTS**. Este documento conserva el alcance y el desglose, y añade el estado comprobable de las pruebas BDD y de los puntos que quedaron pendientes. Confirmar en el tablero las fechas formales del Sprint.

Los HU seleccionados para cumplir con el objetivo del Sprint 2 se muestran a continuación:

---

## HU-02: Publicación de Productos

**Como Agricultor**, quiero publicar mis cosechas para que sean visibles.

### Priorización

**Must**

### Estimación

**5 Story Points**

### Escenario BDD

- **Given:** Un agricultor autenticado con token JWT.
- **When:** Publica un producto con tipo, cantidad y `precio`.
- **Then:** El sistema valida que la fecha no sea futura a hoy y retorna un ID de producto único.

---

## HU-13: Control de Inventario

**Como Agricultor**, quiero controlar la cantidad disponible de mis productos, con el fin de mantener actualizado el inventario y evitar ofrecer productos que ya no están disponibles.

### Priorización

**Must**

### Estimación

**5 Story Points**

### Escenario BDD

- **Given:** El agricultor tiene productos registrados y disponibles en la aplicación.
- **When:** El agricultor actualiza la cantidad disponible de su producto (a través del lote).
- **Then:** El sistema actualiza el inventario y muestra la nueva cantidad disponible para los clientes.

> **Alcance pendiente del criterio de consulta:** la historia también exige que el cliente vea la nueva cantidad, pero no hay un endpoint `GET` para consultar productos y sus lotes. HU-04 y HU-15 definen filtros sobre un listado, pero ninguna historia del backlog crea ese listado. El equipo evaluó un `GET /api/v1/productos/{idProducto}` mínimo y decidió no agregarlo en ese momento; la bitácora deja pendiente confirmarlo con el equipo/Product Owner.

---

## Carga total del Sprint

Tanto el **HU-02** como el **HU-13** tienen una estimación de **5 Story Points** cada uno. La carga total del **Sprint 2** es de:

> **10 Story Points**  
Según la estimación de Fibonacci.

---

## Dependencias y precondiciones

- **Autenticación JWT.** HU-02 y HU-13 requieren un agricultor autenticado. La API cuenta con `POST /api/v1/auth/login`, `JwtService` y `JwtAuthenticationFilter`; las pruebas BDD cargan `BddSecurityConfig` para exigir JWT también durante la integración.
- **Usuarios registrados.** Ambas historias requieren un usuario con rol `AGRICULTOR`, entregado en HU-01 (Sprint 1).
- **Datos relacionados.** HU-02 necesita una categoría existente y crea producto, lote y trazabilidad inicial. HU-13 requiere un producto y un lote asociados al agricultor.
- **Acuerdos de flujo de trabajo.** En la Daily del 16 de octubre el equipo acordó límites WIP de tres tareas en *In Progress* y dos en *Code Review* (ver `docs/kanban-policies.md`).

---

# Desglose técnico de las tareas correspondientes para los HU seleccionados

> **Fuente de los identificadores:** T-02.1 a T-02.8 y T-02.10 corresponden a los issues #81 a #88 y #90. T-02.9 se infiere por la numeración del issue #89 y debe confirmarse. La bitácora no proporciona los IDs individuales de las tareas de HU-13.

| ID Tarea | Descripción técnica | Componente / Tecnología | Atributo ISO 25010 |
|---|---|---|---|
| **T-02.1** | Modelar entidad `Producto` y relaciones con Agricultor y Categoría | Java JPA (Jakarta Persistence), Hibernate, Spring Boot | Mantenibilidad |
| **T-02.2** | Persistencia de productos, lotes y estados de trazabilidad | PostgreSQL, SQL | Fiabilidad |
| **T-02.3** | Crear `ProductoRepository` | Spring Data JPA, `JpaRepository`, Java | Mantenibilidad |
| **T-02.4** | DTO de publicación de producto y validaciones iniciales | Java DTO, Bean Validation, Jakarta Validation | Fiabilidad |
| **T-02.5** | `ProductoService.registrar()`: crear producto, primer lote y primer registro de trazabilidad en una transacción | Spring Boot, `@Transactional`, JPA | Fiabilidad |
| **T-02.6** | Controlador `POST /api/v1/productos`, respuesta `201 Created` con ID | REST Controller, Spring Web | Adecuación funcional |
| **T-02.7** | Validar campos, autenticación, fecha y cantidades; manejar errores | Bean Validation, seguridad JWT, `GlobalExceptionHandler` | Fiabilidad |
| **T-02.8** | Pruebas unitarias de servicio y controlador | JUnit 5, Mockito, Spring Test | Mantenibilidad |
| **T-02.9** | Pruebas BDD de integración de publicación: login, JWT, POST y persistencia del producto/lote/trazabilidad | Cucumber, JUnit 5, Spring Boot Test, MockMvc, H2 | Adecuación funcional |
| **T-02.10** | Rama `feature/HU02`, revisión según DoD e integración a `develop` | Git, GitHub, Pull Request, Git Flow | Mantenibilidad |
| **HU-13** | Endpoint `PATCH /api/v1/productos/{idProducto}/lotes/{idLote}`: editar lote según `DISPONIBLE` / `AGOTADO` | REST Controller, Spring Boot, `@Transactional` | Adecuación funcional |
| **HU-13** | Registrar estado y trazabilidad en cada actualización; permitir agotar y reabastecer el lote | `EstadoTrazabilidad`, `EstadoTrazabilidadRepository` | Fiabilidad |
| **HU-13** | Control de concurrencia optimista en el lote y respuesta `409 CONFLICTO_CONCURRENCIA` | JPA, `@Version`, `OptimisticLockingFailureException` | Fiabilidad |
| **HU-13** | Endpoint `PATCH /api/v1/productos/{idProducto}` para actualizar precio e imagen con control de propiedad | REST Controller, Spring Boot | Seguridad funcional |
| **HU-13** | Pruebas BDD: estados, cantidades, fechas, autenticación, propiedad, producto/lote inexistentes y trazabilidad | Cucumber, JUnit 5, Spring Boot Test, MockMvc, H2 | Adecuación funcional |
| **HU-02 & HU-13** | Actualizar `docs/bdd/bdd-doc.md` y documentos del Sprint 2; preparar demostración | Markdown, GitHub, cliente HTTP, PgAdmin | Mantenibilidad |

## Alcance y ejecución de las pruebas BDD de HU-13

La bitácora del 16 de noviembre y `docs/bdd/bdd-doc.md` detallan el siguiente estado:

- `controlInventarioBDD.feature` incluye **22 casos ejecutables y un escenario marcado `@pendiente`**.
- Los casos ejecutables cubren reducción, agotamiento y reabastecimiento; límites de cantidad; solicitudes inválidas; fecha futura; usuario sin sesión o con JWT inválido; comprador u otro agricultor; y producto/lote inexistente o no asociado. Los pasos verifican HTTP, estado del lote y trazabilidad.
- La ejecución local reportada de `RunCucumberTest` registró **41 escenarios, 276 pasos aprobados y un escenario omitido** (`@pendiente`), con `BUILD SUCCESS`. Esta ejecución se hizo en `feature/HU-13-test-BDD`; su commit y PR a `develop` estaban pendientes de registrar.
- El escenario omitido es «El comprador ve la nueva cantidad disponible después de la actualización». No existe todavía un `GET` de productos/lotes que permita consultar ese catálogo: HU-04 y HU-15 hablan de filtrar un listado que ninguna historia implementa. Se evaluó crear `GET /api/v1/productos/{idProducto}` y se decidió no hacerlo entonces; falta confirmar la decisión con el equipo.
- `RunCucumberTest` excluye el escenario mediante el filtro `not @pendiente`, y el último paso aún no tiene definición Java. Los escenarios ejecutables comprueban el cambio leyendo `LoteProduccionRepository` y consultando con JPQL el último estado y el número de registros de trazabilidad. Esto valida la persistencia, pero no demuestra lo que ve un comprador.
- Cuando se defina e implemente la consulta, el paso pendiente debe añadirse a `ControlInventarioSteps.java`, comprobar la cantidad desde la perspectiva del comprador y quitar el tag `@pendiente`.
- La concurrencia real tampoco queda probada de extremo a extremo por BDD: la bitácora indica que `LoteProduccionConcurrenciaTest` solo comprueba el campo de versión y que el conflicto no se ejercita. Queda como una limitación de pruebas distinta del escenario de consulta pendiente.

---

## Decisiones de diseño que ajustaron el plan durante el Sprint

| # | Decisión | Motivo |
|---|---|---|
| 1 | El agricultor se identifica por el **token JWT** y el `agricultorId` de la petición debe corresponder al usuario autenticado; si no, responde `403 AGRICULTOR_NO_AUTORIZADO`. | Evitar publicaciones o cambios a nombre de otro agricultor. |
| 2 | La fecha de cosecha no puede ser posterior a hoy; responde `400 FECHA_COSECHA_INVALIDA`. | Cumplir el criterio BDD de HU-02. |
| 3 | El lote nace `DISPONIBLE`; con cantidad cero pasa a `AGOTADO`. Cada edición registra trazabilidad. | Mantener historial y disponibilidad del inventario. |
| 4 | En un lote `DISPONIBLE` solo se cambia la cantidad disponible; la producción y la fecha se actualizan al reabastecer un lote `AGOTADO` (`409 CAMPO_NO_EDITABLE` si se intentan cambiar antes). | Preservar los datos de cosecha mientras haya inventario disponible. |
| 5 | Se usa **bloqueo optimista** (`@Version`) en el lote. | Evitar que una compra futura y una edición manual sobrescriban cambios. |
| 6 | Las pruebas BDD usan `BddSecurityConfig`, que replica la cadena de seguridad de producción; el runner omite escenarios con `@pendiente`. | Probar autenticación real y excluir únicamente el escenario que no se puede ejecutar hasta disponer de una consulta de catálogo. |
| 7 | No agregar de inmediato un endpoint de consulta para HU-13; dejar etiquetado el escenario de visibilidad del comprador y confirmar el alcance con el equipo/Product Owner. | HU-04 y HU-15 presuponen un catálogo filtrable, pero el backlog no define su endpoint de consulta. |
