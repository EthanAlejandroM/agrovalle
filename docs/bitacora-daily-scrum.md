# Bitácora de Daily Scrums — AgroValle Connect

**Equipo:** Ethan Alejandro Mezu Quizaboni, Julián David Peña Chocue, Bairon Palacios Urrutia, Nicolle Mera Gomez

**Periodo cubierto:** Sprint 0 (7–18 de septiembre de 2026) y Sprint 1 (23–30 de septiembre de 2026)

**Formato:** cada Daily responde tres preguntas: 
- ¿qué se hizo desde la última reunión?
- ¿qué se hará hasta la próxima? 
- ¿qué impedimentos existen?


> **Modalidad asíncrona:** el equipo realizó las Daily Scrums por el chat grupal y no por videollamada. Cada integrante informa su avance por escrito y el resto responde en el mismo hilo. Las entradas de esta bitácora resumen esos mensajes y se contrastan con el historial de commits y Pull Requests del repositorio.

---

# Sprint 0 — Preparación del proyecto (7–18 de septiembre de 2026)

**Objetivo:** preparar el entorno, el flujo de trabajo y los artefactos iniciales antes de comenzar el desarrollo funcional.


## Daily 0.1 — Lunes 7 de septiembre de 2026

| Integrante | Ayer | Hoy | Impedimentos |
|---|---|---|---|
| Equipo | Sin actividad. | El historial del proyecto señala el commit inicial del repositorio `agrovalle`. | Sin impedimentos. |

**Evidencia:** commit `Initial commit` (7 sep), referido en la bitácora original.

## Daily 0.2 — Martes 15 de septiembre de 2026

| Integrante | Ayer | Hoy | Impedimentos |
|---|---|---|---|
| Equipo | Sin registro público verificable para esta fecha. | Sin registro público verificable para esta fecha. | No consta en el repositorio. |


## Daily 0.3 — Miércoles 16 de septiembre de 2026

| Integrante | Ayer | Hoy | Impedimentos |
|---|---|---|---|
| Ethan | Preparación de la base del proyecto. | PR #1 integró Maven y Spring Boot, Checkstyle, `.gitignore`, Husky/pre-commit, estructura inicial y pruebas. PR #2 actualizó el README con visión, integrantes y Git Flow. | No se reportaron impedimentos en la descripción de los PR. |
| Nicolle | Preparación de artefactos de documentación. | PR #3 integró `docs/dod.md` y el documento inicial de historias de usuario. | No se reportaron impedimentos en el PR. |
| Equipo revisor | — | PR #1 recibió comentarios de revisión de Julián y Nicolle; Ethan aprobó los cambios del PR #3. | — |

**Evidencia:** PR #1 (integrado el 16 sep; verificación declarada: `mvn test`, Checkstyle sin infracciones y hook pre-commit); PR #2 y PR #3.

## Daily 0.4 — Jueves 17 de septiembre de 2026

| Integrante | Ayer | Hoy | Impedimentos |
|---|---|---|---|
| Nicolle | Historias de usuario y Definition of Done publicados en el repositorio. | El commit `ac90680` ajustó la condición *When* de HU-01 y HU-11. | El historial no documenta el motivo más allá del ajuste de precisión de los escenarios. |

**Evidencia:** commit `ac906806eac5be0ca5ab42e3043cb281c70b28b7`, incluido después en el PR #6.

## Daily 0.5 — Viernes 18 de septiembre de 2026

| Integrante | Ayer | Hoy | Impedimentos |
|---|---|---|---|
| Nicolle | Correcciones a historias y al DoD. | PR #6 actualizó el DoD, incorporó cuatro historias adicionales al backlog y reunió las correcciones BDD de HU-01 y HU-11. | No se reportaron impedimentos en la descripción del PR. |

**Evidencia:** PR #6 integrado en `develop` el 18 sep. El backlog quedó con 19 historias.

**Resultado del Sprint 0:** quedó publicada la base Maven/Spring Boot con Java 17, la estrategia Git Flow, las comprobaciones de calidad y el pre-commit, además del backlog y el Definition of Done. Commitlint se incorporó el 23 de septiembre, ya en Sprint 1, por lo que no se presenta como entregable del Sprint 0.

---

# Sprint 1 — Registro de usuarios (23–30 de septiembre de 2026)

**Sprint Goal:** habilitar el registro de Agricultores y Compradores, garantizando la integridad de los datos en PostgreSQL.

---

## Daily 1.1 — Miércoles 23 de septiembre de 2026

| Integrante | Ayer (Sprint 0 / trabajo previo) | Hoy | Impedimentos |
|---|---|---|---|
| Nicolle | Backlog, DoD y criterios BDD documentados. | T-01.1 a T-01.4: dependencias y conexión a PostgreSQL, entidad `Usuario`, `schema.sql`, `UsuarioRepository` con su prueba, clase principal de Spring Boot. | La entidad y el esquema necesitaron varias correcciones hasta quedar alineados. |
| Ethan | Configuración de Husky, Checkstyle y Maven. | Configurar Commitlint y depurar los hooks de Husky (PR #31). | Ninguno. |
| Julián | Revisión del repositorio y de la HU-11. | Sincronizar su rama de HU-11 con `develop` y actualizar `package-lock.json`. | Ninguno. |
| Bairon | *[completar]* | *[completar]* | *[completar]* |

**Evidencia:** PR #29, #30, #31 y #32 integrados en `develop`.

---

## Daily 1.2 — Jueves 24 de septiembre de 2026

| Integrante | Ayer | Hoy | Impedimentos |
|---|---|---|---|
| Nicolle | Esquema, repositorio, controlador y DTO base. | Agregar getters/setters a las clases de usuario, ajustar el endpoint de registro, documentar las variables de entorno de la base de datos en el README y sincronizar su rama con `develop`. | Ninguno. |
| Ethan, Julián, Bairon | *[completar]* | *[completar]* | *[completar]* |

**Evidencia:** commits `refactor: update registration endpoint`, `docs: add database environment variables`. PR #33 pendiente de revisión.

---

## Daily 1.3 — Lunes 28 de septiembre de 2026

| Integrante | Ayer | Hoy | Impedimentos |
|---|---|---|---|
| Ethan | Configuración de herramientas de calidad. | Revisar y fusionar el PR #33; alinear el esquema con el nombre de columnas de Hibernate; implementar el servicio de registro (T-01.6); crear pruebas del servicio e incorporar H2 como base de datos de pruebas. | Las columnas del esquema no coincidían con el mapeo de Hibernate, lo que impedía persistir usuarios. Se corrigió con `fix(db)`. |
| Nicolle, Julián, Bairon | *[completar]* | *[completar]* | *[completar]* |

**Evidencia:** commits `fix(db)`, `feat(hu-01)`, `test(hu-01)`, `build(test)`.

---

## Daily 1.4 — Martes 29 de septiembre de 2026

| Integrante | Ayer | Hoy | Impedimentos |
|---|---|---|---|
| Julián | Sincronización de su rama de HU-11. | Implementar el registro de compradores (T-11.1 y T-11.2): rol `COMPRADOR` sobre el endpoint `/api/v1/auth/register`. Fusionar el PR #75 (HU-01). | Conflictos al integrar con `develop`, porque HU-01 y HU-11 modifican el mismo servicio y controlador. |
| Ethan | Servicio de registro de HU-01 y pruebas. | Revisar y fusionar el PR #76 (HU-11) en `develop`. | Ninguno. |
| Nicolle, Bairon | *[completar]* | *[completar]* | *[completar]* |

**Evidencia:** PR #75 (HU-01) y PR #76 (HU-11) integrados en `develop`.

---

## Daily 1.5 — Miércoles 30 de septiembre de 2026

| Integrante | Ayer | Hoy | Impedimentos |
|---|---|---|---|
| Nicolle | Revisión de integración. | Documentar el *Sprint Goal* y el desglose técnico de las tareas de HU-01 y HU-11 (`docs/sprint1-planning.md`, PR #77). Fusionar el PR #78. | La planeación escrita quedó documentada al final del Sprint, después del desarrollo. |
| Ethan | Revisión del PR #76. | Mejorar validación y manejo de errores (T-01.8), ampliar la cobertura de pruebas, eliminar la prueba por defecto y mover las credenciales de la base de datos a variables de entorno (PR #78). | Ninguno. |
| Julián, Bairon | *[completar]* | *[completar]* | *[completar]* |

**Evidencia:** PR #77 y #78 integrados en `develop`.

---

## Resumen de impedimentos del Sprint 1

| # | Impedimento | Cómo se resolvió |
|---|---|---|
| 1 | Entidad y esquema SQL desalineados. | Correcciones sucesivas de `Usuario` y `schema.sql`. |
| 2 | Nombres de columnas distintos entre el esquema y Hibernate. | Commit `fix(db): align schema columns with hibernate naming`. |
| 3 | Conflictos entre ramas que modifican `UsuarioService` y `UsuarioController`. | Se integró `develop` en cada rama antes del PR. |
| 4 | Credenciales de PostgreSQL escritas en `application.yml`. | Se leen de `DB_USERNAME` y `DB_PASSWORD` (variables de entorno). |
