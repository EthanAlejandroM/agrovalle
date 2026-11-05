# Acta de revisión de contradicciones entre artefactos (Bloque F: TRA-06)

**Proyecto:** AgroValle Connect
**Fecha de la revisión:** 22/10/2026
**Responsable:** <completar>
**Participantes:** <completar>

## 1. Artefactos revisados

- Especificaciones: `docs/requisitos/especificacion-requisitos-ieee-29148.md`, `ieee-hu-01.md`, `ieee-hu-11.md`.
- Matriz: `docs/design/matriz-trazabilidad.md`.
- Diagramas: `docs/design/img/` (MER, ER en PNG, componentes, despliegue).
- Pruebas: `src/test/java/com/agrovalle/`.
- Documentos de proceso: `docs/kanban-policies.md`, `docs/contributing.md`, `docs/pr-review.md`, `docs/dod.md`.
- Método: comparación del contenido de las especificaciones con el listado real del repositorio (`ls -R docs` y `ls -R src/test`).

## 2. Hallazgos

Severidad: Alta (impide cerrar una tarea del bloque), Media (inconsistencia que conviene resolver), Baja (orden o limpieza).

| ID | Artefactos en conflicto | Descripción | Severidad | Acción | Estado |
|---|---|---|---|---|---|
| H-01 | `ieee-hu-01.md` vs `src/test` | La especificación citaba `RegistroAgricultorBddTest.java`, que no existe en el repositorio. | Alta | Sustituir por las clases existentes (`RegistroAgricultorTDDTest`, `UsuarioServiceAgricultorTest`, `UsuarioControllerAgricultorTest`). Decidir si se crea una prueba BDD. | Corregido en la especificación |
| H-02 | `ieee-hu-01.md` vs `docs/design/img` | La especificación marcaba los diagramas de componentes y de despliegue como "por implementar", pero ambos existen. | Media | Referenciar `DiagramaComponentes.drawio.svg` y `DiagramaDespliegue.drawio.svg`. | Corregido en la especificación |
| H-03 | `ieee-hu-01.md` y matriz vs repositorio | No existen el diagrama de casos de uso (UC-01), el de secuencia, el de clases, el de actividades ni los mockups (`registro-agricultor`, selector de municipio). TRA-02 y la parte de comportamiento de TRA-03 no se pueden cerrar. | Alta | Crear los artefactos o acordar con el equipo cuáles son obligatorios para este sprint. | Abierto |
| H-04 | `docs/design/README.md` y `docs/design/matriz-trazabilidad.md` | Ambos eran archivos casi vacíos (77 y 16 bytes) y se citan como referencia en las especificaciones. | Media | La matriz queda reemplazada por la versión completa. Falta escribir el README de diseño (la nota del Bloque G indica que este documento se anexará a él). | Parcial |
| H-05 | `docs/` vs `docs/sprints/1/` | `sprint-1-retrospective.md`, `sprint-1-review.md` y `sprint1-planning.md` aparecen en ambas carpetas con el mismo tamaño. | Baja | Dejar una sola copia y actualizar los enlaces. Coincide con la nota del equipo sobre reorganizar `docs/`. | Abierto |
| H-06 | `UsuarioServiceTest` vs `UsuarioServiceAgricultorTest`; `UsuarioControllerTest` vs `UsuarioControllerAgricultorTest` | Existen pares de pruebas con tamaños casi idénticos (2817 y 2827 bytes; 7305 y 7315 bytes) y fechas distintas. Posible duplicado tras un renombrado (inferido por el tamaño, falta comparar el contenido). | Media | Comparar el contenido; eliminar el duplicado o diferenciar su propósito. | Abierto |
| H-07 | `docs/tdd/*.md` y `docs/bdd/*.md` | Los archivos `hu-01-tdd.md`, `hu-11-tdd.md`, `tdd-doc.md`, `bdd-doc.md`, `hu-01-bdd.md` y `hu-11-bdd.md` tienen 0 bytes. Afecta a RF-11.7 (evidencia de pruebas y cobertura). | Alta | Documentar el proceso TDD/BDD y adjuntar la evidencia. | Abierto |
| H-08 | `ieee-hu-11.md` vs `docs/` | La trazabilidad citaba `CONTRIBUTING.md` y una política de aprobación sin ruta. En el repositorio son `docs/contributing.md` y `docs/pr-review.md`. | Baja | Actualizar las rutas. | Corregido en la especificación |
| H-09 | `ieee-hu-01.md` y matriz vs `src/main` | No estaban en el listado revisado: `src/main/resources/schema.sql`, la entidad JPA `Usuario`, la ruta `/agricultor/inicio` y `.github/workflows/`. Las filas de TRA-04 y varias de TRA-05 quedan "por verificar". | Media | Revisar que existan con esos nombres y que `Usuario` use `contrasena`, `rol` (VARCHAR) y `municipio` (VARCHAR) como dice la especificación. | Abierto |
| H-10 | `ModeloER_Agrovalle.drawio.svg` vs `DiagramaER.png` | Hay dos artefactos de modelo de datos y no se indica cuál es el MER y cuál el DER. | Media | Nombrar cada uno en el README de diseño y citar el correcto en TRA-04. | Abierto |

## 3. Resumen

- **Corregidos en esta revisión:** H-01, H-02, H-08.
- **Abiertos que bloquean el cierre del Bloque F:** H-03 y H-07.
- **Abiertos de verificación:** H-09 y H-10.
- **Limpieza:** H-04 (parcial), H-05 y H-06.

## 4. Seguimiento

Cuando se resuelva un hallazgo, se actualiza su estado en esta tabla y la fila correspondiente de la matriz.
