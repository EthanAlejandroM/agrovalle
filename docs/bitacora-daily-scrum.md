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

---

# Daily 2.5 — 9 de noviembre de 2026

**Fecha:** lunes 9 de noviembre de 2026  
**Hora:** 8:32 p. m. (hora registrada en la grabación)  
**Duración:** 47 minutos y 33 segundos  
**Participantes con intervenciones identificadas:** Ethan Alejandro Mezu Quizaboni y Nicolle Mera Gomez.  
**Personas mencionadas como ausentes al inicio:** Peña y Urrutia.  
**Fuente:** transcripción de la reunión de Teams del 9 de noviembre de 2026.

## ¿Qué se hizo desde la última reunión?

Ethan explicó que, durante esa semana y la anterior, se habían realizado ajustes por cuenta propia en los mockups de AgroValle Connect. Entre los cambios mencionó modificaciones en los campos de correo electrónico y contraseña, el color de los botones y el tamaño de algunas casillas. También incorporó los apartados de términos y condiciones y política de privacidad, considerando la seguridad y el tratamiento de datos.

En la vista de inicio del agricultor revisó la distribución de la barra de búsqueda, las notificaciones para facilitar la comunicación con compradores, el acceso a soporte y las categorías para agilizar la búsqueda de productos. También explicó el uso de filtros por fechas de cosecha, de modo que un comprador pueda consultar productos cosechados dentro de un rango determinado.

Durante la reunión se examinó el formulario de publicación de productos y la información relacionada con los lotes. Se discutió la necesidad de diferenciar la cantidad producida de la cantidad disponible y se revisó cómo debía comportarse el estado del lote de acuerdo con el inventario.

## ¿Qué se hará hasta la próxima reunión?

A partir de los acuerdos y propuestas discutidos, quedan como actividades por continuar:

- Ajustar los mockups del formulario de publicación y edición de productos para representar correctamente los campos del lote y sus restricciones.
- Revisar la pantalla de administración de productos para permitir consultar los lotes asociados a cada producto.
- Evaluar una tabla o listado de lotes que muestre código, fecha de cosecha y estado; contemplar búsqueda, ordenamiento por fecha y una visualización inicial limitada (se propusieron cinco registros).
- Definir e implementar la edición de la cantidad disponible cuando el lote siga en estado disponible.
- Definir el comportamiento de la edición cuando el lote esté agotado y el agricultor registre una nueva cosecha.
- Revisar los filtros por rango de fechas en las vistas del agricultor y del comprador.
- Verificar que el inventario agregado que se muestra al comprador tenga en cuenta las cantidades disponibles de los lotes asociados al producto.
- Contrastar los acuerdos con la lógica de backend y las validaciones existentes para mantener la trazabilidad.

Estas tareas se registran como próximos pasos derivados de la conversación; la transcripción no demuestra que ya se hayan implementado.

## ¿Qué impedimentos existen?

- Se presentaron dudas funcionales sobre la diferencia entre ajustar el inventario de un lote existente por ventas externas y registrar una cosecha nueva. La conversación permitió precisar la distinción, pero todavía requiere trasladarse y verificarse en la implementación.
- La regla de edición depende del estado del lote, por lo que deben coordinarse validaciones del backend y controles de interfaz.
- Se debe evitar mezclar existencias de fechas de cosecha distintas, ya que esto afectaría la trazabilidad y la precisión de los filtros por fecha.
- Se anticipa que la consulta de múltiples lotes para un mismo producto requiere resolver la forma de búsqueda, ordenamiento y visualización de los registros.
- En la transcripción no se reportó un bloqueo técnico concreto que impidiera continuar; los puntos anteriores corresponden a dudas y trabajo pendiente de definición o implementación.

## Acuerdos funcionales de la reunión

### Publicación inicial del producto y creación del lote

Se revisó que al publicar un producto se registren los datos de producción y se genere automáticamente el código de lote. Entre la información discutida están la fecha de cosecha, la cantidad producida y la cantidad disponible.

Se aclaró que la cantidad disponible puede ser menor que la cantidad producida. Por ejemplo, si se cosecharon 600 unidades y 100 se vendieron por fuera de la plataforma, el agricultor debe poder registrar que solo quedan 500 disponibles. La cantidad disponible nunca debe superar la cantidad producida.

### Edición de un lote que todavía está disponible

Cuando el lote continúa disponible, la edición debe limitarse a la cantidad disponible. Esto permite descontar ventas realizadas por fuera de AgroValle Connect sin alterar la fecha de cosecha ni la cantidad originalmente producida.

La cantidad disponible debe validarse para que no exceda la cantidad producida. El estado del lote depende de esa disponibilidad: si llega a cero, el lote pasa a agotado; si quedan unidades, permanece disponible.

### Lote agotado y nueva cosecha

Se explicó que no se deben cambiar los datos de cosecha de un lote que todavía conserva existencias, porque se mezclaría inventario de fechas distintas. Cuando el lote se agota y se realiza una cosecha nueva, debe registrarse el nuevo ciclo sin combinarlo con las existencias anteriores.

Durante la conversación se distinguió entre reutilizar el mismo producto y registrar un lote distinto para una cosecha posterior. El producto puede conservarse, mientras que una nueva cosecha con fecha y producción diferentes debe tener su propio registro de lote y código para preservar la trazabilidad. La transcripción contiene dudas iniciales sobre cuándo se conserva o se genera el código; el criterio que quedó más claro al final fue crear un lote nuevo cuando hay una nueva cosecha mientras aún existen unidades de un lote anterior. La regla definitiva de actualización cuando un lote se agota debe validarse contra el modelo de datos y la lógica existente antes de implementarse.

### Consulta de lotes y trazabilidad

Se propuso que la edición del producto permita consultar los lotes asociados en una tabla. Como información resumida se mencionaron el código de lote, la fecha de cosecha y el estado. Al seleccionar un lote, el agricultor podría consultar el detalle.

También se discutió ordenar los registros por fecha, buscarlos y mostrar inicialmente cinco lotes, con una opción para consultar más. La finalidad es facilitar la administración cuando un mismo producto tenga varios lotes de distintas cosechas.

### Información y filtros para el comprador

Se conversó sobre mostrar al comprador la disponibilidad del producto considerando los lotes vinculados. Asimismo, se revisó la búsqueda por un rango de fechas de cosecha, para que el comprador pueda encontrar productos cosechados dentro del periodo que le interesa. La vista del agricultor, por su parte, debe permitir consultar sus productos y lotes por fecha.

## Seguimiento de los acuerdos

| Tema | Acuerdo o criterio conversado | Estado al cierre de la reunión |
|---|---|---|
| Código de lote | Generación automática al registrar el lote. | Criterio conversado; validar implementación. |
| Cantidad producida | Representa la producción asociada al lote. | Criterio conversado. |
| Cantidad disponible | Puede ser inferior a la producida y debe reflejar ventas externas. | Criterio conversado; requiere validación. |
| Validación de cantidades | La cantidad disponible no puede ser mayor que la producida. | Criterio conversado; verificar regla en backend. |
| Estado del lote | Se agota cuando la disponibilidad llega a cero. | Criterio conversado; verificar automatización. |
| Edición de lote disponible | Solo permitir modificar cantidad disponible. | Acuerdo funcional. |
| Nueva cosecha | Mantener separados los registros de cosechas diferentes para no mezclar inventario. | Acuerdo funcional; precisar el ciclo de vida del código. |
| Tabla de lotes | Mostrar código, fecha y estado; valorar búsqueda y ordenamiento. | Propuesta pendiente de diseño/implementación. |
| Filtro por fechas | Consultar productos o cosechas dentro de un rango. | Requisito revisado; pendiente de validación. |

## Registro de Pull Requests y estado del repositorio (consulta del 12 de noviembre de 2026)

Esta sección complementa las notas de la reunión con una consulta posterior del repositorio `EthanAlejandroM/agrovalle`, rama `develop`. Los estados siguientes son los que mostraba GitHub al momento de la consulta y no deben interpretarse como acuerdos expresados en la reunión del 9 de noviembre.

### Pull Requests recientes relevantes

| PR | Descripción | Estado consultado | Integración |
|---|---|---|---|
| [#128](https://github.com/EthanAlejandroM/agrovalle/pull/128) | Pruebas BDD de HU-02: publicación de productos. Rama `feature/HU-02-test-BDD` hacia `develop`. | **Abierto** al 12 nov 2026. | Pendiente de merge. |
| [#124](https://github.com/EthanAlejandroM/agrovalle/pull/124) | Release `v1.0.0` con Sprint 0, Sprint 1 y Sprint de Diseño. | Cerrado. | **Fusionado en `main`** el 8 nov 2026. |
| [#123](https://github.com/EthanAlejandroM/agrovalle/pull/123) | Intento de release desde `develop` hacia `main`. | Cerrado. | No fue fusionado. |
| [#121](https://github.com/EthanAlejandroM/agrovalle/pull/121) | Marcar checklist de documentación y eliminar carpeta duplicada de imágenes. | Cerrado. | Fusionado en `develop` el 6 nov 2026. |
| [#120](https://github.com/EthanAlejandroM/agrovalle/pull/120) | Actualización del backlog. | Cerrado. | Fusionado en `develop` el 6 nov 2026. |
| [#119](https://github.com/EthanAlejandroM/agrovalle/pull/119) | Corrección de diagramas. | Cerrado. | Fusionado en `develop` el 6 nov 2026. |
| [#118](https://github.com/EthanAlejandroM/agrovalle/pull/118) | Corrección de diagramas. | Cerrado. | Fusionado en `develop` el 6 nov 2026. |
| [#117](https://github.com/EthanAlejandroM/agrovalle/pull/117) | Prueba de fallo intencional de CI. | Cerrado. | Fusionado en `develop` el 5 nov 2026. |
| [#116](https://github.com/EthanAlejandroM/agrovalle/pull/116) | Matriz de trazabilidad y documentación. | Cerrado. | Fusionado en `develop` el 5 nov 2026. |
| [#115](https://github.com/EthanAlejandroM/agrovalle/pull/115) | Configuración de CI. | Cerrado. | Fusionado en `develop` el 22 oct 2026. |
| [#114](https://github.com/EthanAlejandroM/agrovalle/pull/114) | Configuración de JaCoCo con umbral de cobertura del 60 %. | Cerrado. | Fusionado en `develop` el 22 oct 2026. |
| [#113](https://github.com/EthanAlejandroM/agrovalle/pull/113) | Refactorización y eliminación de código no utilizado en HU-01. | Cerrado. | Fusionado en `develop` el 22 oct 2026. |
| [#112](https://github.com/EthanAlejandroM/agrovalle/pull/112) | Checklist de diseño de diagramas UML y mockups. | Cerrado. | Fusionado en `develop` el 22 oct 2026. |
| [#111](https://github.com/EthanAlejandroM/agrovalle/pull/111) | Pruebas BDD con Cucumber para HU-01 y HU-11. | Cerrado. | Fusionado en `develop` el 22 oct 2026. |
| [#110](https://github.com/EthanAlejandroM/agrovalle/pull/110) | Refactorización de archivos de pruebas de HU-01 y HU-11. | Cerrado. | Fusionado en `develop` el 22 oct 2026. |
| [#109](https://github.com/EthanAlejandroM/agrovalle/pull/109) | Cambio de nombre de archivos de prueba. | Cerrado. | Fusionado en `develop` el 22 oct 2026. |
| [#108](https://github.com/EthanAlejandroM/agrovalle/pull/108) | Cambio de nombre del archivo de pruebas. | Cerrado. | Cerrado sin merge. |
| [#107](https://github.com/EthanAlejandroM/agrovalle/pull/107) | Creación de carpetas de documentación. | Cerrado. | Fusionado en `develop` el 22 oct 2026. |
| [#106](https://github.com/EthanAlejandroM/agrovalle/pull/106) | Pruebas Maven de HU-11. | Cerrado. | Fusionado en `develop` el 21 oct 2026. |
| [#105](https://github.com/EthanAlejandroM/agrovalle/pull/105) | Kanban, métricas y revisión por pares. | Cerrado. | Fusionado en `develop` el 21 oct 2026. |
| [#104](https://github.com/EthanAlejandroM/agrovalle/pull/104) | Refactorización SOLID de HU-01. | Cerrado. | Fusionado en `develop` el 20 oct 2026. |
| [#103](https://github.com/EthanAlejandroM/agrovalle/pull/103) | Refactorización SOLID dirigida a una rama de respaldo. | Cerrado. | Fusionado en `backup/develop-antes-de-limpiar-2026-10-19`, no en `develop`. |
| [#102](https://github.com/EthanAlejandroM/agrovalle/pull/102) | Refactorización SOLID de HU-11. | Cerrado. | Cerrado sin merge. |


### Estado del release

El PR #124 integró en `main` el release `v1.0.0` el 8 de noviembre de 2026. Su descripción presenta como alcance consolidado HU-01 y HU-11, persistencia en PostgreSQL, pruebas automatizadas, CI y documentación. También aclara que HU-02 y otras historias permanecían fuera del alcance de esa entrega. Por ello, el PR #128 debe entenderse como trabajo posterior para avanzar en HU-02, no como parte ya liberada de `v1.0.0`.

## Pendientes consolidados después de la reunión y de la consulta del repositorio

1. Dar seguimiento a la revisión y eventual integración del PR #128 de BDD HU-02.
2. Mantener el escenario de fecha futura identificado como pendiente hasta que la validación funcional correspondiente esté implementada y pueda probarse.
3. Contrastar las reglas de disponibilidad, agotamiento y creación de lotes nuevas definidas en la reunión con el modelo y los servicios actuales.
4. Completar o ajustar los mockups de publicación, edición y consulta de lotes.
5. Resolver la presentación de múltiples lotes por producto, con búsqueda y ordenamiento.
6. Validar filtros por rango de fecha de cosecha para agricultores y compradores.
7. Verificar que la disponibilidad mostrada al comprador corresponda a las existencias reales de los lotes.

> **Nota de trazabilidad:** la sección de PR refleja el estado de GitHub consultado el 12 de noviembre de 2026. Los acuerdos funcionales proceden de la transcripción del 9 de noviembre. Las propuestas de interfaz y tareas futuras se mantienen como pendientes mientras no exista evidencia de su implementación o cierre.