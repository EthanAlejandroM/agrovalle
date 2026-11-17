# Retrospectiva del Sprint 2 — AgroValle Connect

**Fecha:** [completar]

**Participantes:** Ethan Alejandro Mezu Quizaboni, Julián David Peña Chocue, Bairon Palacios Urrutia y Nicolle Mera Gomez

**Sprint Goal:** Permitir que los Agricultores publiquen sus cosechas y mantengan actualizado su inventario, evitando ofrecer productos que ya no están disponibles.



## 1. Datos del Sprint 2

El equipo planificó 10 Story Points: HU-02 y HU-13, de 5 puntos cada una. PR #127 integró la implementación de HU-02 el 12 de noviembre; PR #128 integró sus pruebas BDD. PR #133 integró la implementación de HU-13. El 16 de noviembre se reportó una ejecución BDD de HU-13 en la rama `feature/HU-13-test-BDD`, pero el commit y el PR de esas pruebas seguían pendientes de registrar. Por ello, al corte de la bitácora HU-13 continuaba en cierre y no debía darse por terminada según el DoD.

El feature `controlInventarioBDD.feature` contiene 22 casos ejecutables y un escenario etiquetado `@pendiente`. La ejecución local de `RunCucumberTest` reportó 41 escenarios, 276 pasos aprobados, un escenario omitido (`@pendiente`) y `BUILD SUCCESS`.

El escenario omitido requiere que un comprador vea la cantidad disponible después de una actualización. El proyecto no tiene un endpoint `GET` para consultar productos y sus lotes. HU-04 y HU-15 describen filtros sobre un listado, pero ninguna historia define la creación de ese listado. Se evaluó un `GET /api/v1/productos/{idProducto}` mínimo y se decidió no implementarlo entonces; la bitácora deja pendiente confirmar el alcance con el equipo.

---

## 2. Seguimiento de las acciones de mejora del Sprint 1

| # | Acción comprometida | Estado | Evidencia |
|---|---|---|---|
| 1 | Subir `sprintN-planning.md` a `develop` antes del primer commit funcional. | **No cumplida** | Hasta el 12 de noviembre, `docs/sprints/2/sprint-2-planning.md` solo contenía un marcador de posición. La planeación vivía en el tablero SPRINTS. |
| 2 | Completar pruebas unitarias y BDD del rol `COMPRADOR` (HU-11). | **Cumplida** | La ejecución del 11 de noviembre incluye pruebas de controlador y servicio de HU-11 y 3 escenarios BDD de comprador. |
| 3 | Registrar cada Daily Scrum el mismo día. | **Cumplida** | La bitácora tiene entradas del 16, 19, 20 y 22 de octubre, una actualización técnica del 4 y 5 de noviembre y una reunión el 9 de Noviembre, |
| 4 | Definir la estructura compartida antes de dividir historias. | **Por confirmar** | El repositorio no permite evaluar si hubo conflictos de fusión. |
| 5 | Actualizar la documentación técnica dentro del mismo PR cuando cambie el diseño. | **No cumplida** | Los endpoints `PATCH` de HU-13 y los códigos de error nuevos no están documentados en `docs/`; solo se documentaron las pruebas BDD de HU-02. |
| 6 | Al menos una tarea y una revisión de PR por integrante. | **Por confirmar** | Requiere revisar el tablero. En el PR #127 consta una aprobación (Ethan). |

---

## 3. Qué salió bien (Sprint 2)

- **HU-02 quedó integrada con pruebas BDD.** PR #127 llevó la publicación con autenticación JWT y validaciones a `develop`; PR #128 integró `PublicacionProductoSteps.java`, los escenarios y su documentación.
- **Las pruebas de HU-02 verifican el flujo integrado.** Registran un agricultor, obtienen JWT desde `/api/v1/auth/login`, publican en `POST /api/v1/productos` con `Authorization: Bearer` y verifican en `ProductoRepository`, `LoteProduccionRepository` y `EstadoTrazabilidadRepository` la respuesta/persistencia del producto, lote y trazabilidad.
- **HU-13 cubre el ciclo de vida del lote.** Los escenarios ejecutables incluyen reducción, agotamiento y reabastecimiento, límites de cantidad, errores de solicitud, fechas, permisos y casos de producto/lote inexistente o no asociado.
- **La verificación de HU-13 comprueba estado e historial, no solo el código HTTP.** `ControlInventarioSteps.java` consulta cantidades mediante `LoteProduccionRepository` y usa JPQL para comprobar el último estado y el número de registros de trazabilidad.
- **El escenario pendiente quedó identificado explícitamente.** El tag `@pendiente` representa el caso desde la perspectiva del comprador; el runner lo excluye con `not @pendiente`, y el último paso todavía no tiene definición Java. Así se evita contabilizar como aprobado un comportamiento que no se puede consultar por API.
- **La ejecución BDD fue documentada con su alcance.** El 16 de noviembre se reportaron 22 casos ejecutables más el pendiente en el feature de HU-13 y una corrida local de `RunCucumberTest` con 41 escenarios, 276 pasos aprobados, un escenario omitido y `BUILD SUCCESS`.
- **La trazabilidad acompaña los cambios de inventario.** La implementación crea eventos para `DISPONIBLE` y `AGOTADO`, y usa control optimista para evitar que una actualización sobreescriba otra.
- **El CI detectó una falla antes de integrar HU-02.** Según la retrospectiva previa, el pipeline se corrigió antes de completar la integración del PR #127.

---

## 4. Qué se puede mejorar (Sprint 2)

- **La planeación se publicó tarde.** Se repitió la acción del Sprint 1: el documento detallado quedó en el repositorio después de iniciado el trabajo; el periodo formal aún requiere confirmación en el tablero.
- **El criterio de consulta de HU-13 no tenía una dependencia de API definida.** El backlog pide que el comprador vea la cantidad nueva, pero no existe `GET` de catálogo/producto/lotes. HU-04 y HU-15 solo hablan de filtrar un listado que el backlog no encarga a ninguna historia. Se evaluó un `GET /api/v1/productos/{idProducto}` mínimo y se decidió posponerlo; la decisión sigue pendiente de ratificación.
- **La evidencia BDD debe distinguir ejecución de integración.** Los 22 casos ejecutables y el escenario `@pendiente` se probaron localmente en `feature/HU-13-test-BDD`. Al corte del 16 de noviembre, el commit y PR no estaban registrados; por tanto, esa ejecución no prueba el estado de `develop` ni completa el DoD.
- **El resultado de persistencia no demuestra visibilidad para el comprador.** Hoy los pasos Java verifican cantidades y trazabilidad en base de datos. El escenario de catálogo se omite y carece de paso Java; no debe describirse como cubierto por esas consultas.
- **La concurrencia real sigue sin prueba de extremo a extremo.** La bitácora reporta que `LoteProduccionConcurrenciaTest` comprueba la existencia del campo de versión, pero no fuerza dos actualizaciones simultáneas ni comprueba el rechazo `CONFLICTO_CONCURRENCIA`.
- **La documentación técnica general debe acompañar las APIs.** La bitácora identifica como pendientes la documentación de endpoints/códigos de error y la variable `JWT_SECRET`; además, pide versionar el esquema de todas las tablas.
- **Falta una ejecución completa de CI sobre el conjunto actual.** La ejecución completa citada en la documentación es del 11 de noviembre, anterior a PR #127. La corrida del 16 de noviembre es de Cucumber local y no sustituye `mvn clean verify` sobre `develop`.


---

## 5. Acciones de mejora para el Sprint 3

| # | Acción | Responsable | Plazo | Indicador de cumplimiento |
|---|---|---|---|---|
| 1 | Publicar `sprint-3-planning.md` antes del primer cambio funcional y confirmar fechas, HU y responsables en el tablero. | [asignar] | Día 1 del Sprint 3 | Documento disponible en `develop` antes del primer commit funcional; tablero y documento coinciden. |
| 2 | Integrar por PR las pruebas BDD de HU-13 y ejecutar la suite completa sobre `develop`. | [asignar] | Antes de cerrar HU-13 | PR revisado, `mvn clean verify` exitoso y reporte adjunto; ningún escenario se contabiliza si está `@pendiente`. |
| 3 | Resolver con el Product Owner si la consulta del catálogo pertenece a HU-13, HU-04, HU-15 o una HU nueva; si se aprueba, especificar el endpoint y los datos expuestos. | [asignar] | Planning del Sprint 3 | Decisión registrada en backlog/acta. Si se implementa la consulta, paso Java de comprador agregado y `@pendiente` retirado con prueba pasando. |
| 4 | Diseñar una prueba reproducible de concurrencia sobre la misma versión del lote. | [asignar] | Durante Sprint 3 | Prueba con dos actualizaciones concurrentes verifica una operación aceptada y el error `409 CONFLICTO_CONCURRENCIA` esperado para la otra. |
| 5 | Documentar endpoints, códigos de error, `JWT_SECRET` y esquema en el mismo PR que introduce cada cambio. | [asignar] | Cada PR | Checklist del PR enlaza documentación y esquema actualizados. |
| 6 | Registrar cada Daily y la retrospectiva con fecha, acuerdos, responsables y fuentes, separando notas directas de reconstrucciones. | Rotativo | El mismo día | Bitácora actualizada y acuerdos verificables. |
| 7 | Comprobar revisiones requeridas y CI antes de marcar cada historia como terminada. | Todo el equipo | Cada cierre de HU | Tabla DoD con PR, aprobaciones y ejecución de CI enlazados. |

---

## 6. Conclusión

El equipo cumplió en lo esencial el objetivo del Sprint 2: los agricultores ya pueden publicar productos con su primer lote, y el inventario se puede actualizar con trazabilidad y control de concurrencia, apoyados en pruebas unitarias y de integración y en un CI que filtró un fallo antes de integrar. HU-13 queda en cierre.

Los puntos de mejora se concentran en la planeación escrita a tiempo, en que cada criterio de la historia tenga su prueba desde el primer momento, en la revisión por dos integrantes y en que la documentación técnica y el esquema de base de datos acompañen al código. Estos aspectos se abordarán con las acciones definidas para el Sprint 3.