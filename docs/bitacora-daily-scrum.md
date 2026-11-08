# Bitácora de Daily Scrums — AgroValle Connect

**Equipo:** Ethan Alejandro Mezu Quizaboni, Julián David Peña Chocue, Bairon Palacios Urrutia, Nicolle Mera Gomez
**Periodo cubierto:** Sprint 0 (7–18 de septiembre), Sprint 1 (23–30 de septiembre) y seguimiento del Sprint de Diseño/Sprint 2 (16 de octubre–5 de noviembre de 2026)
**Formato:** cada Daily responde tres preguntas: ¿qué se hizo desde la última reunión?, ¿qué se hará hasta la próxima? y ¿qué impedimentos existen?

> **Fuentes y alcance:** las entradas de Sprint 0 y Sprint 1 se reconstruyeron a partir de la evidencia disponible en el repositorio. El seguimiento de octubre se basa en transcripciones y notas de reuniones de Teams compartidas por el equipo. Cuando una minuta no permite confirmar un avance o una asistencia, se registra como pendiente de confirmar; el avance reportado no equivale por sí solo al cierre de una tarea.

---

# Sprint 0 — Preparación del proyecto (7–18 de septiembre de 2026)

**Objetivo:** preparar el entorno, el flujo de trabajo y los artefactos iniciales antes de comenzar el desarrollo funcional.

> **Reconstrucción documental:** esta sección se contrastó con la rama `develop` y los Pull Requests públicos #1, #2, #3 y #6. El repositorio permite verificar cambios, autores, revisiones y fechas de integración; no conserva el contenido de las conversaciones de Daily Scrum. Por ello, las tablas registran únicamente evidencia verificable y no atribuyen intervenciones individuales que no constan allí.

## Daily 0.1 — Lunes 7 de septiembre de 2026

| Integrante | Ayer | Hoy | Impedimentos |
|---|---|---|---|
| Equipo | No se encontró actividad anterior registrada en la rama. | El historial del proyecto señala el commit inicial del repositorio `agrovalle`. | No consta en el repositorio. |

**Evidencia:** commit `Initial commit` (7 sep), referido en la bitácora original.

## Daily 0.2 — Martes 15 de septiembre de 2026

| Integrante | Ayer | Hoy | Impedimentos |
|---|---|---|---|
| Equipo | Sin registro público verificable para esta fecha. | Sin registro público verificable para esta fecha. | No consta en el repositorio. |

**Nota:** no se completan avances ni participantes de una Daily a partir de suposiciones.

## Daily 0.3 — Miércoles 16 de septiembre de 2026

| Integrante | Ayer | Hoy | Impedimentos |
|---|---|---|---|
| Ethan | Preparación de la base del proyecto. | PR #1 integró Maven y Spring Boot, Checkstyle, `.gitignore`, Husky/pre-commit, estructura inicial y pruebas. PR #2 actualizó el README con visión, integrantes y Git Flow. | No se reportaron impedimentos en la descripción de los PR. |
| Nicolle | Preparación de artefactos de documentación. | PR #3 integró `docs/dod.md` y el documento inicial de historias de usuario. | No se reportaron impedimentos en el PR. |
| Equipo revisor | — | PR #1 recibió comentarios de revisión de Julián y Nicolle; Ethan aprobó los cambios del PR #3. | — |

**Evidencia:** PR #1 (integrado el 16 sep; verificación declarada: `mvn test`, Checkstyle sin infracciones y hook pre-commit); PR #2 y PR #3 (integrados el 16 sep).

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
| Bairon | Sin registro verificable en la evidencia disponible. | Sin registro verificable en la evidencia disponible. | Sin registro verificable en la evidencia disponible. |

**Evidencia:** PR #29, #30, #31 y #32 integrados en `develop`.

---

## Daily 1.2 — Jueves 24 de septiembre de 2026

| Integrante | Ayer | Hoy | Impedimentos |
|---|---|---|---|
| Nicolle | Esquema, repositorio, controlador y DTO base. | Agregar getters/setters a las clases de usuario, ajustar el endpoint de registro, documentar las variables de entorno de la base de datos en el README y sincronizar su rama con `develop`. | Ninguno. |
| Ethan, Julián, Bairon | Sin registro verificable en la evidencia disponible. | Sin registro verificable en la evidencia disponible. | Sin registro verificable en la evidencia disponible. |

**Evidencia:** commits `refactor: update registration endpoint`, `docs: add database environment variables`. PR #33 pendiente de revisión.

---

## Daily 1.3 — Lunes 28 de septiembre de 2026

| Integrante | Ayer | Hoy | Impedimentos |
|---|---|---|---|
| Ethan | Configuración de herramientas de calidad. | Revisar y fusionar el PR #33; alinear el esquema con el nombre de columnas de Hibernate; implementar el servicio de registro (T-01.6); crear pruebas del servicio e incorporar H2 como base de datos de pruebas. | Las columnas del esquema no coincidían con el mapeo de Hibernate, lo que impedía persistir usuarios. Se corrigió con `fix(db)`. |
| Nicolle, Julián, Bairon | Sin registro verificable en la evidencia disponible. | Sin registro verificable en la evidencia disponible. | Sin registro verificable en la evidencia disponible. |

**Evidencia:** commits `fix(db)`, `feat(hu-01)`, `test(hu-01)`, `build(test)`.

---

## Daily 1.4 — Martes 29 de septiembre de 2026

| Integrante | Ayer | Hoy | Impedimentos |
|---|---|---|---|
| Julián | Sincronización de su rama de HU-11. | Implementar el registro de compradores (T-11.1 y T-11.2): rol `COMPRADOR` sobre el endpoint `/api/v1/auth/register`. Fusionar el PR #75 (HU-01). | Conflictos al integrar con `develop`, porque HU-01 y HU-11 modifican el mismo servicio y controlador. |
| Ethan | Servicio de registro de HU-01 y pruebas. | Revisar y fusionar el PR #76 (HU-11) en `develop`. | Ninguno. |
| Nicolle, Bairon | Sin registro verificable en la evidencia disponible. | Sin registro verificable en la evidencia disponible. | Sin registro verificable en la evidencia disponible. |

**Evidencia:** PR #75 (HU-01) y PR #76 (HU-11) integrados en `develop`.

---

## Daily 1.5 — Miércoles 30 de septiembre de 2026

| Integrante | Ayer | Hoy | Impedimentos |
|---|---|---|---|
| Nicolle | Revisión de integración. | Documentar el *Sprint Goal* y el desglose técnico de las tareas de HU-01 y HU-11 (`docs/sprint1-planning.md`, PR #77). Fusionar el PR #78. | La planeación escrita quedó documentada al final del Sprint, después del desarrollo. |
| Ethan | Revisión del PR #76. | Mejorar validación y manejo de errores (T-01.8), ampliar la cobertura de pruebas, eliminar la prueba por defecto y mover las credenciales de la base de datos a variables de entorno (PR #78). | Ninguno. |
| Julián, Bairon | Sin registro verificable en la evidencia disponible. | Sin registro verificable en la evidencia disponible. | Sin registro verificable en la evidencia disponible. |

**Evidencia:** PR #77 y #78 integrados en `develop`.

---

## Resumen de impedimentos del Sprint 1

| # | Impedimento | Cómo se resolvió |
|---|---|---|
| 1 | Entidad y esquema SQL desalineados. | Correcciones sucesivas de `Usuario` y `schema.sql`. |
| 2 | Nombres de columnas distintos entre el esquema y Hibernate. | Commit `fix(db): align schema columns with hibernate naming`. |
| 3 | Conflictos entre ramas que modifican `UsuarioService` y `UsuarioController`. | Se integró `develop` en cada rama antes del PR. |
| 4 | Credenciales de PostgreSQL escritas en `application.yml`. | Se leen de `DB_USERNAME` y `DB_PASSWORD` (variables de entorno). |


---

# Sprint de Diseño y seguimiento del Sprint 2 (16 de octubre–5 de noviembre de 2026)

**Contexto:** desde el inicio del Sprint de Diseño y del Sprint 2, el equipo ha trabajado en cerrar tareas pendientes del Sprint 1 y completar los artefactos de diseño, en paralelo con la preparación del siguiente incremento. Según el equipo, el documento de planificación del Sprint 2 y el tablero GitHub Projects **SPRINTS** ya contienen el Sprint Goal y las tareas con prioridad **Must/Should**.

## Daily 2.1 — 16 de octubre de 2026

| Pregunta | Resumen |
|---|---|
| ¿Qué se reportó? | Se revisaron avances de HU-01 y HU-11, las pruebas existentes y el tablero Kanban. El equipo indicó límites WIP de hasta tres tareas en **In Progress** y dos en **Code Review**. |
| ¿Qué sigue? | Preparar el checklist y los artefactos de diseño; completar wireframes para agricultor y comprador; aclarar permisos por rol; documentar CI, JaCoCo y TDD; elaborar el diagrama de despliegue y la comparación IEEE 29148 frente a BDD. |
| ¿Qué impedimentos hubo? | HU-11 tenía dificultades de integración en el Pull Request. También había dudas sobre cómo evidenciar TDD y documentar las pruebas, y faltaba información de planificación para cerrar los mockups. |

**Evidencia:** transcripción de la reunión de Teams del 16 de octubre.

## Daily 2.2 — 19 de octubre de 2026

| Pregunta | Resumen |
|---|---|
| ¿Qué se reportó? | El equipo informó avances importantes en tareas del Sprint 1 y del Sprint de Diseño. Se indicó que las tareas de planificación del Sprint 2 estaban listas para iniciar. |
| ¿Qué sigue? | Compartir el estado de las tareas, resolver la carga de cambios y continuar la planificación y el trabajo de diseño. |
| ¿Qué impedimentos hubo? | Se mencionó un problema al subir cambios para corregir y disponibilidad desigual para asistir o reportar el avance de algunas tareas. |

**Evidencia:** notas de la reunión del 19 de octubre.

## Daily 2.3 — 20 de octubre de 2026

| Pregunta | Resumen |
|---|---|
| ¿Qué se reportó? | Se reorganizaron ramas y commits para corregir problemas de integración. Se informó la creación de espacios de documentación para BDD, diseño, sprints y la bitácora. También se reportaron pruebas, controlador y servicio de HU-11 listos para subir a un Pull Request. |
| ¿Qué sigue? | Integrar el Pull Request de refactorización, completar documentación de diseño y de pruebas TDD, y publicar los cambios pendientes en `develop`. |
| ¿Qué impedimentos hubo? | Persistían tareas documentales y de diseño sin cerrar; la documentación TDD aún no estaba preparada según el reporte de la reunión. |

**Evidencia:** transcripción y notas de la reunión del 20 de octubre.

## Daily 2.4 — 22 de octubre de 2026

| Pregunta | Resumen |
|---|---|
| ¿Qué se reportó? | Se actualizaron responsables y estado: CI y su documentación; políticas Kanban; corrección de diagramas de secuencia y UML; JaCoCo; comparación IEEE para HU-01/HU-11 y matriz de trazabilidad. |
| ¿Qué sigue? | Completar correcciones de diagramas y documentación, y subir a `develop` los Pull Requests que aún estaban pendientes. |
| ¿Qué impedimentos hubo? | Faltaban correcciones de diagramas y completar algunos documentos; la integración de cambios todavía dependía de Pull Requests. |

**Evidencia:** notas de la reunión del 22 de octubre.

## Actualización técnica posterior — 4 y 5 de noviembre de 2026

El `pom.xml` y el workflow de CI ya incluyen el gate global de JaCoCo para instrucciones y ramas, con mínimo de 60 %, además de la ejecución de Maven, pruebas y Checkstyle. En la ejecución local compartida, Maven terminó con `BUILD SUCCESS` y el informe mostró 95 % de instrucciones y 100 % de ramas. La prueba intencional de fallo de CI quedó como siguiente actividad y no se registra aquí como ejecutada.
