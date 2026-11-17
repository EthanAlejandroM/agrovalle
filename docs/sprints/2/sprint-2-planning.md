# Sprint Goal del Sprint 2

Este segundo Sprint tiene como objetivo **permitir que los Agricultores publiquen sus cosechas y mantengan actualizado su inventario**, evitando ofrecer productos que ya no están disponibles.

**Periodo del Sprint:** [completar fecha de inicio] – [completar fecha de cierre]

> **Nota de actualización (12 de noviembre de 2026):** según la bitácora de Daily Scrums, la planeación original del Sprint 2 se mantuvo en el tablero de GitHub Projects **SPRINTS**. Este documento recoge el alcance, el desglose técnico y las decisiones de diseño tomadas durante el Sprint, para que el repositorio refleje lo realmente planeado y construido.

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

---

## Carga total del Sprint

Tanto el **HU-02** como el **HU-13** tienen una estimación de **5 Story Points** cada uno. La carga total del **Sprint 2** es de:

> **10 Story Points**
Según la estimación de Fibonacci.

---

## Dependencias y precondiciones

- **Autenticación JWT.** El Sprint 1 dejó pendiente la autenticación de la que depende HU-02. El repositorio ya cuenta con `POST /api/v1/auth/login`, `JwtService` y `JwtAuthenticationFilter`, por lo que ambas historias parten de un usuario autenticado.
- **Usuarios registrados.** HU-02 y HU-13 requieren un usuario con rol `AGRICULTOR`, entregado en HU-01 (Sprint 1).
- **Acuerdos de flujo de trabajo.** En la Daily del 16 de octubre el equipo acordó límites WIP de tres tareas en *In Progress* y dos en *Code Review* (ver `docs/kanban-policies.md`).

---

# Desglose técnico de las tareas correspondientes para los HU seleccionados

> **Fuente de los identificadores:** T-02.1 a T-02.8 y T-02.10 corresponden a los issues #81 a #88 y #90, vinculados al PR #127. T-02.9 se infiere por la numeración del issue #89. Las tareas de HU-13 se listan **sin ID** hasta confirmar su numeración en el tablero.

| ID Tarea | Descripción técnica | Componente / Tecnología | Atributo ISO 25010 |
|---|---|---|---|
| **T-02.1** | Modelar entidad `Producto` + relaciones con el Agricultor | Java JPA (Jakarta Persistence), Hibernate, Spring Boot | Mantenibilidad |
| **T-02.2** | Migración de base de datos (tabla de productos; el modelo incluye también `lote_produccion` y `estado_trazabilidad`) | PostgreSQL, SQL | Fiabilidad |
| **T-02.3** | Crear `ProductoRepository` (Spring Data JPA) | Spring Data JPA, `JpaRepository`, Java | Mantenibilidad |
| **T-02.4** | DTO base de publicación de producto + validaciones iniciales | Java DTO, Bean Validation, Jakarta Validation | Fiabilidad |
| **T-02.5** | Implementar `ProductoService.registrar()`: crea el producto, su primer lote y el primer registro de trazabilidad | Spring Boot, `@Transactional`, JPA | Fiabilidad |
| **T-02.6** | Controlador `POST /api/v1/productos` que devuelve el ID del producto con `201 Created` | REST Controller, Spring Web, HTTP POST API | Usabilidad |
| **T-02.7** | Validación de campos obligatorios y manejo de errores | Bean Validation, `GlobalExceptionHandler` | Fiabilidad |
| **T-02.8** | Pruebas unitarias (service + controller) | JUnit 5, Mockito, Spring Test | Mantenibilidad |
| **T-02.9** | Prueba BDD de integración de la publicación de productos | Cucumber, JUnit 5, Spring Boot Test, MockMvc, H2 | Adecuación funcional |
| **T-02.10** | Rama `feature/HU02`, PR revisado según el DoD, merge a `develop` | Git, GitHub, Pull Request, Git Flow | Mantenibilidad |
| **HU-13** | Endpoint `PATCH /api/v1/productos/{idProducto}/lotes/{idLote}` y `ProductoService.actualizarLote()` según el estado del lote (`DISPONIBLE` / `AGOTADO`) | REST Controller, Spring Boot, `@Transactional` | Adecuación funcional |
| **HU-13** | Registro de trazabilidad en cada edición de cantidad | `EstadoTrazabilidad`, `EstadoTrazabilidadRepository` | Fiabilidad |
| **HU-13** | Control de concurrencia optimista sobre el lote (`@Version`) y respuesta `409 CONFLICTO_CONCURRENCIA` | JPA, `OptimisticLockingFailureException` | Fiabilidad |
| **HU-13** | Endpoint `PATCH /api/v1/productos/{idProducto}` para actualizar precio e imagen | REST Controller, Spring Boot | Usabilidad |
| **HU-13** | Pruebas unitarias (servicio, controlador y concurrencia) | JUnit 5, Mockito, Spring Test | Mantenibilidad |
| **HU-13** | Prueba BDD de integración del control de inventario | Cucumber, JUnit 5, Spring Boot Test | Adecuación funcional |
| **HU-13** | Code review + PR a `develop` | Git, GitHub, Pull Request, Code Review | Mantenibilidad |
| **HU-02 & HU-13** | Actualizar documentación (`docs/bdd/bdd-doc.md` y documentos del Sprint 2) | Markdown, GitHub | Mantenibilidad |
| **HU-02 & HU-13** | Preparar demo de publicación y actualización de inventario | PgAdmin, cliente HTTP | Usabilidad |

---

## Decisiones de diseño que ajustaron el plan durante el Sprint

| # | Decisión | Motivo |
|---|---|---|
| 1 | El agricultor se identifica por el **token JWT** y el `agricultorId` del cuerpo debe coincidir con él; si no, responde `403 AGRICULTOR_NO_AUTORIZADO`. | Evitar que un usuario autenticado publique a nombre de otro agricultor. |
| 2 | La fecha de cosecha **no puede ser posterior a hoy**: responde `400 FECHA_COSECHA_INVALIDA`. | Es un criterio explícito del escenario BDD de HU-02. |
| 3 | El lote nace en estado `DISPONIBLE` y cada edición de cantidad registra trazabilidad; con cantidad `0` pasa a `AGOTADO`. | Mantener el historial del inventario y marcar lo que ya no se puede ofrecer. |
| 4 | Un lote `DISPONIBLE` solo permite cambiar la cantidad disponible; producida y fecha solo se modifican en un lote `AGOTADO` (`409 CAMPO_NO_EDITABLE`). | Proteger los datos de la cosecha original mientras haya stock. |
| 5 | Se usa **bloqueo optimista** (`@Version`) en el lote. | Evitar que una compra futura (HU-19) y una edición manual del inventario se pisen el stock. |
| 6 | Las pruebas BDD de integración usan una cadena de seguridad que replica la de producción (`BddSecurityConfig`). | Que el JWT se exija de verdad en las pruebas (sin token o con token inválido responde `401`). |
