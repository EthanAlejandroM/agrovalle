# Sprint 2 Review — AgroValle Connect

**Fecha de la revisión:** [completar]

**Equipo:** Ethan Alejandro Mezu Quizaboni, Julián David Peña Chocue, Bairon Palacios Urrutia, Nicolle Mera Gomez

**Sprint Goal:** Permitir que los Agricultores publiquen sus cosechas y mantengan actualizado su inventario, evitando ofrecer productos que ya no están disponibles.

---

## 1. Resultado frente al Sprint Goal

| HU | Descripción | Puntos | Estado | Observación |
|---|---|---|---|---|
| HU-02 | Publicación de Productos | 5 | **Terminada** | Cumple los criterios BDD: JWT, fecha no posterior a hoy e ID de producto único. PR #127 integrado en `develop` el 12 de noviembre. |
| HU-13 | Control de Inventario | 5 | **Terminada** | La edición de la cantidad del lote, la trazabilidad y el control de concurrencia están en `develop` con pruebas unitarias.  |

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

---

## 4. Verificación del Definition of Done (HU-02)

| Criterio | Cumple | Evidencia |
|---|---|---|
| Implementada según la HU y sus criterios BDD | Sí | Demos 2, 4 y 5 |
| Validaciones implementadas | Sí | Demos 5 y 6; escenarios BDD de precio, cantidades, categoría y rol |
| Probada, con errores corregidos | Sí | Demo 9; el fallo de pipeline del PR #127 se corrigió antes de integrar |
| Código revisado por al menos 2 integrantes | Sí | El PR #127 muestra la aprobación de Ethan; falta confirmar la segunda revisión |
| Operaciones correctas sobre la base de datos | Sí | Demo 3; los escenarios BDD verifican producto, lote y trazabilidad |
| Documentación actualizada | **Parcial** | `docs/bdd/bdd-doc.md` y documentos del Sprint 2; sin documentación de los endpoints y códigos de error nuevos |
| Integrada a `develop` sin afectar lo existente | Sí | PR #127 integrado el 12 de noviembre con 2 checks aprobados; las pruebas de HU-01 y HU-11 siguen pasando |

---

## 5. Estado del Product Backlog

Con HU-01 y HU-11 (Sprint 1) y HU-02 (Sprint 2) resueltas, quedan 16 historias en el backlog, y 15 si HU-13 cumple el DoD. Las siguientes en prioridad *Must* son HU-05, HU-06, HU-07, HU-09, HU-10, HU-16, HU-18 y HU-19.

HU-19 (Realizar pedido) y HU-16 (Cancelar pedido) descuentan o liberan el stock de los lotes.

---

