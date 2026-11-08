# Sprint 1 Review — AgroValle Connect

**Fecha de la revisión:** 30 de septiembre de 2026

**Equipo:** Ethan Alejandro Mezu Quizaboni, Julián David Peña Chocue, Bairon Palacios Urrutia, Nicolle Mera Gomez

**Sprint Goal:** Habilitar el registro de Agricultores y Compradores, garantizando la integridad de los datos en PostgreSQL.

---

## 0. Antecedente: Sprint 0 (7–18 de septiembre de 2026)

El Sprint 0 preparó el proyecto y sus reglas de trabajo antes de iniciar las historias funcionales. La rama `develop` y los PR públicos verifican los siguientes entregables:

| Entregable | Evidencia en el repositorio | Responsable / contribución visible |
|---|---|---|
| Proyecto base Maven, Spring Boot y Java 17; estructura inicial y pruebas | PR #1, integrado el 16 sep; `pom.xml` y `src/` | Ethan |
| Checkstyle, `.gitignore`, Husky y hook pre-commit | PR #1; se reportó `mvn test`, Checkstyle sin infracciones y ejecución correcta del hook | Ethan; PR revisado por Julián y Nicolle |
| Visión del producto, integrantes y estrategia Git Flow | README, PR #2 integrado el 16 sep | Ethan |
| Definition of Done e historias de usuario iniciales | `docs/dod.md` y backlog, PR #3 integrado el 16 sep | Nicolle; Ethan aprobó el PR |
| Correcciones de HU-01/HU-11 y actualización del DoD; backlog ampliado a 19 historias | commit `ac90680` y PR #6 integrado el 18 sep | Nicolle |

**Demostración verificable:** el PR #1 declara `mvn test` con `BUILD SUCCESS`, `mvn checkstyle:check` con cero infracciones y funcionamiento del pre-commit. No se encontró evidencia pública de una captura de demostración, así que no se afirma que exista.

**Precisión de alcance:** Commitlint aparece en el historial desde el 23 de septiembre, durante el Sprint 1; no forma parte de los entregables verificados del Sprint 0.

---

## 1. Resultado frente al Sprint Goal

| HU | Descripción | Puntos | Estado | Observación |
|---|---|---|---|---|
| HU-01 | Registro de Agricultores | 5 | **Terminada** | Cumple los criterios BDD y el DoD: pruebas unitarias, de controlador y de integración. |
| HU-11 | Registro de Compradores | 5 | **Terminada** | El rol `COMPRADOR` se registra por el mismo endpoint (PR #76). |

**Carga planificada:** 10 Story Points (Fibonacci). **Completados según DoD:** 10. 

---

## 2. Incremento entregado

- Endpoint `POST /api/v1/auth/register` que registra usuarios con rol `AGRICULTOR` o `COMPRADOR` y responde `201 Created`.
- Persistencia en PostgreSQL (tabla `usuario`) con restricción `UNIQUE` sobre `documento`.
- Validaciones con Bean Validation: nombre, ubicación, tipo de documento (`CC`, `CE`, `PASAPORTE`), documento alfanumérico y rol obligatorios.
- Manejo centralizado de errores (`GlobalExceptionHandler`) con respuestas `400` (validación) y `409` (documento duplicado), incluyendo una red de seguridad ante registros simultáneos.
- Credenciales de base de datos fuera del repositorio (`DB_USERNAME`, `DB_PASSWORD`).
- Prácticas de equipo: Git Flow, Conventional Commits con Commitlint, Husky (`mvn test` y Checkstyle antes de cada commit) y Pull Requests revisados.

---

## 3. Evidencias de la demo

> **Requisitos:** PostgreSQL con la base `agrovalle`, variables `DB_USERNAME` y `DB_PASSWORD` definidas y la aplicación en ejecución (`mvn spring-boot:run`, puerto 8080 por defecto).

### Demo 1 — Registro exitoso de un Agricultor (HU-01)

```bash
POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Carlos Ramírez","ubicacionValle":"Dagua","tipoDocumento":"CC","documento":"1234567890","rol":"AGRICULTOR"}'
```

**Resultado esperado:** `201 Created` con el JSON del usuario y su `id`. 

**Evidencia:** revisar PR.

### Demo 2 — Registro exitoso de un Comprador (HU-11)

```bash
POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Laura Gómez","ubicacionValle":"Cali","tipoDocumento":"CC","documento":"9876543210","rol":"COMPRADOR"}'
```

**Resultado esperado:** `201 Created`.
**Evidencia:** Revisar PR.

### Demo 3 — Persistencia en PostgreSQL

```sql
SELECT id, nombre, ubicacion_valle, tipo_documento, documento, rol, fecha_registro FROM usuario;
```

**Resultado esperado:** dos filas, una `AGRICULTOR` y otra `COMPRADOR`.
**Evidencia:** Revisar PR.

### Demo 4 — Documento duplicado

Repetir el comando de la Demo 1. **Resultado esperado:** `409 Conflict` con código `DOCUMENTO_DUPLICADO`, sin crear un segundo registro.
**Evidencia:** *[captura]*

### Demo 5 — Documento con formato inválido

```bash
POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Ana Torres","ubicacionValle":"Yumbo","tipoDocumento":"CC","documento":"123-456","rol":"COMPRADOR"}'
```

**Resultado esperado:** `400 Bad Request` con código `VALIDACION_FALLIDA` que señala el campo `documento`.
**Evidencia:** Revisar PR.

### Demo 6 — Pruebas automatizadas

```bash
mvn test
```

El proyecto define 14 pruebas: 3 de integración BDD (HU-01), 2 del servicio, 8 del controlador y 1 del repositorio.
**Resultado:** Revisar PR

---

## 4. Verificación del Definition of Done (HU-01)

| Criterio | Cumple | Evidencia |
|---|---|---|
| Implementada según la HU y sus criterios BDD | Sí | Demo 1 |
| Validaciones implementadas | Sí | Demo 5 |
| Probada, con errores corregidos | Sí | Demo 6 |
| Código revisado por al menos 2 integrantes | Sí | PR #75 y #78 |
| Operaciones correctas sobre la base de datos | Sí | Demos 3 y 4 |
| Documentación actualizada | Sí | `docs/sprint1-planning.md`, README |
| Integrada a `develop` sin afectar lo existente | Sí | PR #75 |

---

## 5. Estado del Product Backlog

Con HU-01 y HU-11 resueltas, quedan 17 historias en el backlog. Las siguientes en prioridad *Must* son HU-02 (Publicación de Productos), HU-05, HU-06, HU-07, HU-09, HU-10, HU-13, HU-16, HU-18 y HU-19. HU-02 depende de la autenticación de usuarios (JWT), pendiente de abordar.

---

## 6. Retroalimentación recibida

| Fuente | Comentario | Acción |
|---|---|---|
| *[Profesora / Product Owner]* | *[completar durante la revisión]* | *[completar]* |
