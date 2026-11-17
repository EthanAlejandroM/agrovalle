# Sprint 2 Review — AgroValle Connect

**Fecha de la revisión:** [completar]

**Equipo:** Ethan Alejandro Mezu Quizaboni, Julián David Peña Chocue, Bairon Palacios Urrutia, Nicolle Mera Gomez

**Sprint Goal:** Permitir que los Agricultores publiquen sus cosechas y mantengan actualizado su inventario, evitando ofrecer productos que ya no están disponibles.

---

## 1. Resultado frente al Sprint Goal

| HU | Descripción | Puntos | Estado | Observación |
|---|---|---|---|---|
| HU-02 | Publicación de Productos | 5 | **Terminada** | Cumple los criterios BDD: JWT, fecha no posterior a hoy e ID de producto único. PR #127 integrado en `develop` el 12 de noviembre. |
| HU-13 | Control de Inventario | 5 | **Terminada parcialmente** | La edición de la cantidad del lote, la trazabilidad y el control de concurrencia están en `develop` con pruebas unitarias.  |

**Carga planificada:** 10 Story Points (Fibonacci). **Completados según DoD a la fecha de redacción:** 5 (HU-02). 

**Nota sobre HU-02:** el cierre queda sujeto a integrar el PR con las pruebas BDD de integración (T-02.9) y a una ejecución final de `mvn clean verify`.

---

## 2. Incremento entregado

**HU-02: publicación de productos**
- Endpoint `POST /api/v1/productos`, protegido con JWT, que registra un producto con su primer lote y responde `201 Created` con el `idProducto`.
- El agricultor se identifica por el token. Si el `agricultorId` del cuerpo no coincide, responde `403 AGRICULTOR_NO_AUTORIZADO`.
- Validaciones: categoría existente (`404`), rol `AGRICULTOR` (`403`), cantidades del lote (`400 CANTIDAD_LOTE_INVALIDA`), campos obligatorios (`400 VALIDACION_FALLIDA`) y fecha de cosecha no posterior a hoy (`400 FECHA_COSECHA_INVALIDA`).
- Cada publicación crea el producto, el lote y un registro de trazabilidad en estado `DISPONIBLE`.

**HU-13: control de inventario**
- Endpoint `PATCH /api/v1/productos/{idProducto}/lotes/{idLote}`: un lote `DISPONIBLE` solo permite cambiar la cantidad disponible (si llega a `0`, pasa a `AGOTADO`). Un lote `AGOTADO` se reabastece y vuelve a `DISPONIBLE`.
- Cada edición de cantidad deja un registro de trazabilidad.
- Control de concurrencia optimista (`@Version`) en el lote: una modificación simultánea responde `409 CONFLICTO_CONCURRENCIA`.
- Endpoint adicional `PATCH /api/v1/productos/{idProducto}` para actualizar precio e imagen. Solo el dueño del producto puede usarlo.

**Calidad y pruebas**
- Pruebas unitarias de servicio y controlador para HU-02 y HU-13, y una prueba de concurrencia del lote.
- Pruebas BDD de integración de HU-02 (Cucumber + MockMvc + H2). Recorren el flujo real: registro, login para obtener el JWT y publicación, y verifican producto, lote y trazabilidad en base de datos.

---

**Alcance BDD de HU-13 y criterio pendiente**

- El archivo `controlInventarioBDD.feature` tiene **22 casos ejecutables y un escenario `@pendiente`**. La ejecución local de `RunCucumberTest` del 16 de noviembre reportó **41 escenarios, 276 pasos aprobados y un escenario omitido** (`BUILD SUCCESS`). La bitácora atribuye esa ejecución a `feature/HU-13-test-BDD`; el commit y el PR a `develop` quedaron pendientes.
- El escenario omitido pide que un comprador vea la nueva cantidad disponible. El catálogo no tiene endpoint `GET` para consultar productos y lotes: HU-04 y HU-15 describen filtros de un listado que ninguna historia crea. El equipo evaluó `GET /api/v1/productos/{idProducto}` y decidió no implementarlo por ahora; falta confirmar esa decisión con el equipo/Product Owner.
- El escenario lleva `@pendiente`; `RunCucumberTest` aplica `not @pendiente` y lo excluye. El último paso aún no está definido en Java. Hoy se valida el inventario consultando la base de datos, no desde la vista del comprador.
- Cuando se acuerde e implemente la consulta, añadir a `ControlInventarioSteps.java` el paso que consulta el catálogo como comprador, comprobar la nueva cantidad y retirar `@pendiente`.

---

## 3. Evidencias de la demo

> **Requisitos:** PostgreSQL con la base `agrovalle` y las tablas del modelo creadas, variables `DB_PASSWORD` y `JWT_SECRET` definidas, y la aplicación en ejecución (`mvn spring-boot:run`, puerto 8080). Se necesita un agricultor registrado y una categoría de producto existente.

### Demo 1 — Iniciar sesión como Agricultor

```bash
POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"correo":"agricultor@ejemplo.com","contrasena":"[contraseña]"}'
```

**Resultado esperado:** `200 OK` con `accessToken`. **Evidencia:** [PR #122](https://github.com/EthanAlejandroM/agrovalle/pull/122), captura 2.

### Demo 2 — Publicar un producto (HU-02)

```bash
POST http://localhost:8080/api/v1/productos \
  -H "Authorization: Bearer <accessToken>" \
  -H "Content-Type: application/json" \
  -d '{"categoriaId":1,"agricultorId":<id>,"nombre":"Tomate chonto","tipoProducto":"Hortaliza","unidadMedida":"KILOGRAMO","precio":2500,"imagen":"https://ejemplo.com/tomate.jpg","fechaCosecha":"<fecha de hoy o anterior>","cantidadProducida":500,"cantidadDisponible":500,"ubicacionLote":"Finca El Paraíso, Dagua"}'
```

**Resultado esperado:** `201 Created` con `idProducto`. **Evidencia:** [PR #122](https://github.com/EthanAlejandroM/agrovalle/pull/122), captura 4.

### Demo 3 — Persistencia en PostgreSQL

```sql
SELECT * FROM producto;
SELECT * FROM lote_produccion;
SELECT * FROM estado_trazabilidad;
```

**Resultado esperado:** un producto, su lote con 500 unidades disponibles y un registro de trazabilidad `DISPONIBLE`. **Evidencia:** Revisar ultimos PR

### Demo 4 — Publicación sin token

Repetir la Demo 2 sin la cabecera `Authorization`. **Resultado esperado:** `401 Unauthorized`, sin crear registros. **Evidencia:** Revisar ultimos PR

### Demo 5 — Fecha de cosecha posterior a hoy

Repetir la Demo 2 con una `fechaCosecha` de mañana. **Resultado esperado:** `400 Bad Request` con código `FECHA_COSECHA_INVALIDA`. **Evidencia:** Revisar ultimos PR

### Demo 6 — Publicar a nombre de otro agricultor

Repetir la Demo 2 con el `agricultorId` de otro agricultor. **Resultado esperado:** `403 Forbidden` con código `AGRICULTOR_NO_AUTORIZADO`. **Evidencia:** *Revisar ultimos PR

### Demo 7 — Actualizar el inventario de un lote (HU-13)

```bash
PATCH http://localhost:8080/api/v1/productos/<idProducto>/lotes/<idLote> \
  -H "Authorization: Bearer <accessToken>" \
  -H "Content-Type: application/json" \
  -d '{"cantidadDisponible":120}'
```

**Resultado esperado:** `200 OK` con `estadoLote: DISPONIBLE` y un nuevo registro de trazabilidad. Con `"cantidadDisponible":0` el lote pasa a `AGOTADO`. **Evidencia:** Revisar ultimos PR

### Demo 8 — Campo no editable en un lote disponible

Repetir la Demo 7 enviando `cantidadProducida`. **Resultado esperado:** `409 Conflict` con código `CAMPO_NO_EDITABLE`. **Evidencia:** Revisar ultimos PR

### Demo 9 — Pruebas automatizadas

```bash
mvn clean verify
```

Última ejecución completa registrada (11 de noviembre de 2026, antes del PR #127): 95 pruebas sin fallos ni errores, con 1 omitida; Cucumber con 16 escenarios y 83 pasos aprobados; JaCoCo con 89 % de instrucciones y 85 % de ramas (mínimo exigido: 60 %), con `BUILD SUCCESS`. 

> **Escenario no demostrable todavía:** el comprador ve la nueva cantidad del catálogo. No hay `GET` de productos/lotes; el escenario está etiquetado `@pendiente`, excluido por el runner y sin último paso Java. La verificación actual consulta `LoteProduccionRepository` y JPQL para revisar cantidad, estado más reciente y número de trazas. Eso verifica persistencia, no la respuesta visible al comprador. El endpoint mínimo `GET /api/v1/productos/{idProducto}` se evaluó y se decidió posponer; falta ratificar el alcance. Al implementarlo, completar el paso en `ControlInventarioSteps.java`, verificar la respuesta como comprador y quitar el tag.

---

## 4. Verificación del Definition of Done (HU-02)

| Criterio | Cumple | Evidencia |
|---|---|---|
| Implementada según la HU y sus criterios BDD | Sí, según los PR integrados | PR #127 y escenarios BDD de PR #128. |
| Validaciones implementadas | Sí, según los escenarios | Demos 4–6; autenticación, fecha, identidad, rol, categoría y cantidades. |
| Probada, con errores corregidos | Sí, segun los PR integrados | La ejecución citada de 11 nov es anterior a PR #127. |
| Código revisado por al menos 1 integrante | Sí | Verificar aprobaciones requeridas en PR #127 y #128. |
| Operaciones correctas sobre la base de datos | Sí, según pruebas documentadas | Demo 3 y consultas a repositorios en `PublicacionProductoSteps.java`. |
| Documentación actualizada | Sí, segun los PR integrados | `bdd-doc.md` explica escenarios. |
| Integrada a `develop` sin afectar lo existente | Sí, según bitácora | PR #127 y #128 integrados. |

## Verificación del Definition of Done (HU-13)

| Criterio | Cumple | Evidencia |
|---|---|---|
| Implementación de actualización, estados y trazabilidad | Integrada | PR #133, según bitácora del 12 de noviembre. |
| Pruebas unitarias | Registradas en `develop` | Adjuntar ejecución del pipeline en la versión actual. |
| Pruebas BDD de integración | Parcial | 22 casos ejecutables y uno `@pendiente`; ejecución local reportada con 41 escenarios aprobados y uno omitido. El commit/PR de HU-13 BDD estaba pendiente el 16 de noviembre. |
| El comprador ve la cantidad actualizada | Pendiente de decisión y endpoint | No existe consulta GET de catálogo; HU-04/HU-15 presuponen un listado sin definir quién lo implementa. Escenario excluido por `not @pendiente`. |

---

## 5. Estado del Product Backlog

Con HU-01 y HU-11 (Sprint 1) y HU-02 (Sprint 2) registradas como resueltas, quedan 16 historias. HU-13 no debe contarse como completada según DoD mientras estén pendientes la integración/revisión de la BDD y la decisión sobre el criterio de consulta; al cerrar HU-13 quedarían 15 historias.

Las siguientes historias en prioridad *Must* son HU-05, HU-06, HU-07, HU-09, HU-10, HU-16, HU-18 y HU-19. HU-19 (Realizar pedido) y HU-16 (Cancelar pedido) dependen de la disponibilidad y liberación correcta del inventario.

---

