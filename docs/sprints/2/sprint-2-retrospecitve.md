# Retrospectiva del Sprint 2 — AgroValle Connect

**Fecha:** [completar]

**Participantes:** Ethan Alejandro Mezu Quizaboni, Julián David Peña Chocue, Bairon Palacios Urrutia y Nicolle Mera Gomez

**Sprint Goal:** Permitir que los Agricultores publiquen sus cosechas y mantengan actualizado su inventario, evitando ofrecer productos que ya no están disponibles.



## 1. Datos del Sprint 2

El equipo planificó 10 Story Points (HU-02 y HU-13, de 5 puntos cada una). A la fecha de redacción, HU-02 cumple el Definition of Done y HU-13 está en cierre: su código y pruebas unitarias están en `develop`, y faltan su prueba BDD de integración y la verificación del DoD.

El PR #127 (`feature/HU02`) se integró en `develop` el 12 de noviembre con 2 checks aprobados y cerró los issues T-02.1 a T-02.8 y T-02.10.

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

- **Cobertura de pruebas holgada.** En la ejecución del 11 de noviembre, JaCoCo reportó 89 % de instrucciones y 85 % de ramas, frente al mínimo de 60 %, con `BUILD SUCCESS`.
- **Pruebas BDD de integración reales.** Los escenarios de HU-02 recorren registro, login con JWT y publicación, y verifican el producto, el lote y la trazabilidad en base de datos. Los 16 escenarios de la ejecución del 11 de noviembre pasaron, incluidos los de HU-01 y HU-11.
- **Los criterios BDD destaparon brechas que se corrigieron.** Al llevar los criterios del backlog a escenarios se vio que faltaba validar que la fecha de cosecha no fuera posterior a hoy y que el agricultor se tomaba del cuerpo de la petición y no del token. El PR #127 corrigió ambas.
- **Diseño pensado para lo que viene.** El bloqueo optimista del lote (`@Version`) anticipa el conflicto entre compras (HU-19) y ediciones manuales del inventario.
- **Errores consistentes y trazables.** Las nuevas reglas responden con códigos propios (`FECHA_COSECHA_INVALIDA`, `AGRICULTOR_NO_AUTORIZADO`, `CAMPO_NO_EDITABLE`, `CONFLICTO_CONCURRENCIA`) a través de `GlobalExceptionHandler`.
- **Batería amplia de pruebas unitarias en HU-13.** Cubre existencia y propiedad del producto, estados del lote, límites de cantidad y concurrencia.
- **Deuda del Sprint 1 saldada.** Las pruebas de HU-11 quedaron completas.
- **El CI funcionó como filtro.** El pipeline detectó un fallo en el PR #127, que se corrigió antes de integrar.

---

## 4. Qué se puede mejorar (Sprint 2)

- **La planeación no quedó en el repositorio a tiempo.** Es la acción 1 del Sprint 1, que se repite: el archivo seguía como marcador de posición durante el Sprint.
- **Criterios de la historia implementados tarde.** El backlog ya pedía validar la fecha y que publicara un agricultor autenticado, pero la validación de la fecha y el vínculo del producto con el agricultor del token se completaron al final del Sprint (PR #127), cuando se escribían las pruebas de integración.
- **Fallo de pipeline y reescritura de historial.** El PR #127 se abrió con 4 commits, entre ellos `fix: the pipeline error is supposed to be fixed`, cuyo mensaje no explica la causa. La rama se reescribió con force-push y el PR se integró con 3 commits. El revisor señaló que se resolvió el problema de autenticación JWT en la ejecución de las pruebas. El hook de pre-commit ejecuta `mvn test` y Checkstyle, mientras que el CI ejecuta `mvn verify` (con JaCoCo) y Checkstyle.

- **Documentación por detrás del código.**
  - Los endpoints de HU-13 y los códigos de error nuevos no están documentados.
  - `application.yml` exige la variable `JWT_SECRET`, que el README no menciona.


---

## 5. Acciones de mejora para el Sprint 3

| # | Acción | Responsable | Plazo | Indicador de cumplimiento |
|---|---|---|---|---|
| 1 | Subir `sprint-3-planning.md` a `develop` antes del primer commit funcional del Sprint. | [asignar] | Día 1 del Sprint 3 | Fecha del commit anterior al primer `feat`. |
| 2 | Ejecutar `mvn clean verify` y `mvn checkstyle:check` en local antes de cada push y de abrir un PR. Evitar el force-push sobre ramas en revisión; si es imprescindible, usar `--force-with-lease` y avisar al revisor. | Todo el equipo | Cada PR | PR sin commits posteriores de tipo `fix` por fallo de pipeline. |
| 3 | Convertir cada criterio de aceptación de la HU en una prueba al implementarla y marcarlo en la descripción del PR. | Todo el equipo | Cada HU | Cada criterio del backlog tiene prueba o escenario antes de abrir el PR. |
| 4 | Registrar dos aprobaciones en cada PR, según el DoD. | [asignar] | Cada PR | PR con 2 revisores aprobados antes de integrarse. |
| 5 | Documentar endpoints, códigos de error y variables de entorno (`JWT_SECRET`) dentro del mismo PR, y versionar el script de esquema de todas las tablas. | [asignar] | Cada PR | PR que cambia la API incluye su cambio en `docs/`; el esquema del repo coincide con las entidades. |
| 6 | Completar la prueba BDD de integración de HU-13 y cerrar su DoD. | [asignar] | Inicio del Sprint 3 | Escenarios de HU-13 integrados y DoD marcado. |
| 7 | Registrar cada Daily en la bitácora el mismo día. | Rotativo | Cada día hábil | Una entrada por día en `bitacora-daily-scrum.md`. |

---

## 6. Conclusión

El equipo cumplió en lo esencial el objetivo del Sprint 2: los agricultores ya pueden publicar productos con su primer lote, y el inventario se puede actualizar con trazabilidad y control de concurrencia, apoyados en pruebas unitarias y de integración y en un CI que filtró un fallo antes de integrar. HU-13 queda en cierre.

Los puntos de mejora se concentran en la planeación escrita a tiempo, en que cada criterio de la historia tenga su prueba desde el primer momento, en la revisión por dos integrantes y en que la documentación técnica y el esquema de base de datos acompañen al código. Estos aspectos se abordarán con las acciones definidas para el Sprint 3.