# Retrospectiva del Sprint 1 — AgroValle Connect

**Fecha:** 30 de septiembre de 2026

**Participantes:** Ethan Alejandro Mezu Quizaboni, Julián David Peña Chocue, Bairon Palacios Urrutia y Nicolle Mera Gomez

**Sprint Goal:** Habilitar el registro de Agricultores y Compradores, garantizando la integridad de los datos en PostgreSQL.

---

## 0. Retrospectiva del Sprint 0 (7–18 de septiembre de 2026)

El Sprint 0 preparó el repositorio, el flujo de trabajo, el entorno Java y los artefactos iniciales del producto. Los PR #1, #2 y #3 se integraron el 16 de septiembre; el PR #6, con correcciones al DoD y a HU-01/HU-11 y cuatro historias añadidas al backlog, se integró el 18 de septiembre.

**Qué salió bien**
- El repositorio quedó con una base ejecutable de Maven y Spring Boot, pruebas iniciales, Checkstyle y un hook pre-commit antes de desarrollar las historias funcionales.
- El README hizo explícitos la visión del producto, la composición del equipo y el flujo Git Flow.
- El backlog y el DoD se incorporaron temprano. El PR #3 agregó el DoD y las historias iniciales; el PR #6 dejó el backlog en 19 historias.
- Las verificaciones descritas en el PR #1 reportaron `BUILD SUCCESS`, cero infracciones de Checkstyle y ejecución correcta del pre-commit.

**Qué se puede mejorar**
- Las fechas y entregas de los PR son trazables, pero el historial público no registra el contenido de las Daily Scrums ni permite reconstruir qué dijo cada integrante. Conviene guardar el resumen de cada Daily en el repositorio el mismo día.
- El DoD y los escenarios BDD tuvieron correcciones posteriores: el PR #6 actualizó el DoD y los cambios de HU-01/HU-11 ajustaron la condición *When*. Una revisión conjunta previa a publicar habría reducido ese retrabajo.
- Las descripciones de PR permiten atribuir cambios a quienes los publicaron y revisaron, pero no muestran todo el trabajo de coordinación del equipo. Se recomienda registrar responsables y revisores en la planeación y en cada PR.


**Aprendizaje que se llevó al Sprint 1:** mantener evidencia contemporánea de acuerdos, decisiones y responsabilidades, y actualizar la documentación cuando cambien los criterios o el diseño.

## 1. Datos del Sprint 1

El equipo planificó 10 Story Points (HU-01 y HU-11, de 5 puntos cada una). Al cierre del Sprint, HU-01 cumplió el Definition of Done. HU-11 quedó funcional y fusionada en `develop`, pero sin pruebas dedicadas al rol comprador, por lo que su cierre completo se trasladó al siguiente ciclo.

Durante el Sprint se integraron los Pull Requests #29 a #33 y #75 a #78.

---

## 2. Qué salió bien (Sprint 1)

- **Flujo de trabajo disciplinado.** El equipo trabajó siempre con ramas `feature/*` y Pull Requests hacia `develop`, tal como lo establece el README. No se registraron commits directos sobre `main` ni `develop`.
- **Calidad automatizada.** Los hooks de Husky ejecutan `mvn test` y Checkstyle antes de cada commit, y Commitlint valida el formato de los mensajes. Esto evitó que código defectuoso o mal documentado llegara al repositorio.
- **Cobertura de pruebas de HU-01.** El equipo construyó 14 pruebas automatizadas entre servicio, controlador, repositorio e integración, incluyendo los escenarios de documento duplicado y documento inválido.
- **Manejo de errores robusto.** El equipo centralizó los errores en `GlobalExceptionHandler`, con respuestas claras (`400`, `409`) y protección ante registros simultáneos mediante la restricción `UNIQUE` de PostgreSQL.
- **Seguridad de credenciales.** El equipo identificó que la contraseña de la base de datos estaba en el código y la movió a variables de entorno antes de cerrar el Sprint.
- **Base técnica sólida desde el Sprint 0.** El Backlog, el DoD y el README estaban listos al iniciar el desarrollo, lo que permitió empezar a programar sin bloqueos de definición.

---

## 3. Qué se puede mejorar (Sprint 1)

- **Planeación documentada tarde.** El archivo `sprint1-planning.md` se subió al repositorio el último día del Sprint, cuando el desarrollo ya estaba avanzado. El equipo reconoció que la planeación debe quedar escrita antes de comenzar a programar.
- **Cambios de diseño sin reflejo en la planeación.** El desglose técnico mencionaba `AuthService` y pruebas con Cucumber, pero la implementación usó `UsuarioService` y pruebas BDD escritas con JUnit 5 y MockMvc. El equipo identificó que las decisiones que cambian el plan deben actualizarse en el documento.
- **Trabajo paralelo sobre los mismos archivos.** HU-01 y HU-11 modificaron el mismo servicio y controlador, lo que generó varias integraciones de `develop` en las ramas y conflictos de fusión. El equipo observó que repartir las historias sin definir antes la estructura compartida aumentó el retrabajo.
- **Desalineación entre esquema y entidad.** La tabla `usuario` y la entidad `Usuario` requirieron correcciones repetidas, entre ellas el ajuste de nombres de columnas para Hibernate.
- **HU-11 sin pruebas propias.** La funcionalidad de comprador existe, pero ninguna prueba verifica el rol `COMPRADOR`, de modo que la historia no cumple por completo el DoD.
- **Evidencia de las Daily Scrums.** Las reuniones diarias no dejaron un registro escrito en el momento, por lo que la bitácora tuvo que reconstruirse a partir del historial de commits.
- **Distribución visible del trabajo.** El historial del repositorio muestra la actividad concentrada en tres de los cuatro integrantes. El equipo consideró necesario hacer visibles las tareas de cada persona (por ejemplo, revisiones de código o documentación).

---

## 4. Acciones de mejora para el Sprint 2

| # | Acción | Responsable | Plazo | Indicador de cumplimiento |
|---|---|---|---|---|
| 1 | Subir `sprintN-planning.md` a `develop` antes del primer commit funcional del Sprint. | Nicolle | Día 1 del Sprint 2 | Fecha del commit anterior al primer `feat`. |
| 2 | Completar las pruebas unitarias y BDD del rol `COMPRADOR` para cerrar HU-11. | Julián | Inicio del Sprint 2 | Pruebas de HU-11 fusionadas y DoD marcado. |
| 3 | Registrar cada Daily Scrum en la bitácora el mismo día. | Rotativo | Cada día hábil | Una entrada por día en `bitacora-daily-scrum.md`. |
| 4 | Definir la estructura compartida (servicio, DTO, rutas) antes de dividir historias que tocan los mismos archivos. | Ethan | Planning del Sprint 2 | Cero conflictos de fusión por archivos compartidos. |
| 5 | Actualizar la documentación técnica cuando el diseño cambie respecto a lo planeado. | Todo el equipo | Dentro del mismo PR | PR que modifica el diseño incluye el cambio en `docs/`. |
| 6 | Asignar al menos una tarea y una revisión de PR por integrante en cada Sprint. | Bairon | Planning del Sprint 2 | Tablero con tareas asignadas a los cuatro integrantes. |

---

## 5. Conclusión

El equipo valoró que el Sprint 1 cumplió en lo esencial su objetivo: el sistema ya registra Agricultores y Compradores con datos íntegros en PostgreSQL, apoyado en un flujo de trabajo y herramientas de calidad que funcionaron de manera consistente. Los puntos de mejora se concentran en la planeación anticipada, la coordinación del trabajo sobre archivos compartidos y el registro oportuno de las ceremonias, aspectos que se abordarán con las acciones definidas para el Sprint 2.
