# Matriz de trazabilidad (Bloque F: TRA-02 a TRA-05)

**Proyecto:** AgroValle Connect
**Alcance:** requisitos de HU-01 y HU-11 (ver [ieee-hu-01.md](../requisitos/ieee-hu-01.md) y [ieee-hu-11.md](../requisitos/ieee-hu-11.md))
**Base de la revisión:** listado real del repositorio (`ls -R docs` y `ls -R src/test`, 22/10/2026)
**Acta de revisión:** [acta-revision-contradicciones.md](./acta-revision-contradicciones.md)

**Estados usados:** *Existe* (el archivo está en el repositorio), *Por crear* (no existe todavía), *Por verificar* (no estaba en el listado revisado o su contenido no se ha comprobado).

## 0. Inventario de artefactos

| Artefacto | Estado | Ruta |
|---|---|---|
| Modelo entidad-relación (MER) | Existe | `docs/design/img/ModeloER_Agrovalle.drawio.svg` |
| Diagrama ER en imagen (confirmar si es el DER) | Existe | `docs/design/img/DiagramaER.png` |
| Diagrama de componentes | Existe | `docs/design/img/DiagramaComponentes.drawio.svg` |
| Diagrama de despliegue | Existe | `docs/design/img/DiagramaDespliegue.drawio.svg` |
| DDL | Por verificar | `src/main/resources/schema.sql` |
| Entidades JPA | Por verificar | `src/main/java/...` |
| Diagrama de casos de uso (UC-01) | Por crear | no hay archivo en el repositorio |
| Diagrama de secuencia | Por crear | no hay archivo en el repositorio |
| Diagrama de clases | Por crear | no hay archivo en el repositorio |
| Diagrama de actividades | Por crear | no hay archivo en el repositorio |
| Mockups (`registro-agricultor`, selector de municipio) | Por crear | no hay archivo en el repositorio |
| Pruebas | Existen | `src/test/java/com/agrovalle/` (ver sección 4) |

## 1. TRA-02: requisitos, casos de uso y mockups

Cada requisito de interfaz tiene una pantalla o flujo asociado cuando corresponde.

| Requisito | Resumen | Caso de uso | Pantalla o flujo | Estado |
|---|---|---|---|---|
| RF-01.1 | Formulario de registro | UC-01 Registro Agricultor | Formulario `registro-agricultor` | Por crear |
| RF-01.2 | Validar contraseña | UC-01 (flujo alterno) | Formulario `registro-agricultor` | Por crear |
| RF-01.3 | Validar formato de correo | UC-01 (flujo alterno) | Formulario `registro-agricultor` | Por crear |
| RF-01.4 | Correo no repetido | UC-01 (flujo alterno) | Formulario `registro-agricultor` | Por crear |
| RF-01.5 | Cifrar contraseña | N/A (proceso interno) | N/A | N/A |
| RF-01.6 | Asignar rol agricultor | UC-01 (postcondición) | Redirección a `/agricultor/inicio` | Por crear |
| RF-01.7 | Municipio del Valle del Cauca | UC-01 | Selector de municipio | Por crear |
| RI-01.1 | Mensajes de error junto al campo | UC-01 | Formulario `registro-agricultor` | Por crear |
| RNF-01.1, RNF-01.2, RN-01.1 | Seguridad, rendimiento, regla de municipio | N/A (no son de interfaz) | N/A | N/A |
| RF-11.x, RNF-11.x, RN-11.x | CI, pruebas, Kanban, SOLID | N/A: la HU-11 no define pantallas ni casos de uso de usuario final | N/A | N/A |

## 2. TRA-03: requisitos, diagramas de comportamiento y arquitectura

Se identifica qué diagramas representan cada flujo y componente.

| Requisito | Diagrama de comportamiento | Diagrama de arquitectura | Estado |
|---|---|---|---|
| RF-01.1, RF-01.2, RF-01.3, RF-01.4, RF-01.6, RF-01.7 | Secuencia "Registro de agricultor" (por crear); actividades (por crear) | `DiagramaComponentes.drawio.svg` (verificar que muestre controlador, servicio y repositorio de usuario) | Parcial |
| RF-01.5, RNF-01.1 | Secuencia "Registro de agricultor" (por crear) | `DiagramaComponentes.drawio.svg` (verificar que muestre dónde se cifra la contraseña) | Parcial |
| RNF-01.2 | N/A | `DiagramaDespliegue.drawio.svg` | Existe (verificar) |
| RI-01.1 | N/A | N/A | N/A |
| RF-11.1 a RF-11.5 | N/A | `DiagramaDespliegue.drawio.svg` (verificar si incluye GitHub Actions) | Por verificar |
| RNF-11.3 | Diagrama de clases (por crear) | `DiagramaComponentes.drawio.svg` | Parcial |
| RF-11.6, RF-11.7, RNF-11.1, RNF-11.2, RN-11.1 a RN-11.4 | N/A (procesos y documentos) | N/A | N/A |

## 3. TRA-04: requisitos, MER, DER, DDL y entidades JPA

Las necesidades de información tienen representación coherente en el modelo de datos.

| Requisito | MER | DER | DDL | Entidad JPA | Estado |
|---|---|---|---|---|---|
| RF-01.1 | Entidad `Usuario` en `ModeloER_Agrovalle.drawio.svg` | `DiagramaER.png` | `schema.sql`, tabla de usuarios | `Usuario` | Por verificar |
| RF-01.4 | `Usuario.correo` | `DiagramaER.png` | Restricción de unicidad en `correo` | `Usuario.correo` | Por verificar |
| RF-01.5, RNF-01.1 | `Usuario.contrasena` | `DiagramaER.png` | Columna `contrasena` | `Usuario.contrasena` | Por verificar |
| RF-01.6 | `Usuario.rol` | `DiagramaER.png` | Columna `rol` (VARCHAR) | `Usuario.rol` | Por verificar |
| RF-01.7, RN-01.1 | `Usuario.municipio` | `DiagramaER.png` | Columna `municipio` (VARCHAR) | `Usuario.municipio` | Por verificar |
| RF-01.2, RF-01.3, RI-01.1, RNF-01.2 | N/A (reglas de validación o rendimiento, no de datos) | N/A | N/A | N/A | N/A |
| RF-11.x, RNF-11.x, RN-11.x | N/A: la HU-11 no introduce entidades de datos | N/A | N/A | N/A | N/A |

## 4. TRA-05: requisitos, pruebas y criterios de aceptación

Se puede identificar cómo comprobar que cada requisito se cumple. La columna de prueba muestra la clase candidata según su nombre; la cobertura exacta por método debe confirmarse.

Pruebas existentes en `src/test/java/com/agrovalle/`: `RegistroAgricultorTDDTest`, `RegistroCompradorTDDTest`, `UsuarioRepositoryTest`, `UsuarioServiceAgricultorTest`, `UsuarioServiceCompradorTest`, `UsuarioServiceTest`, y en `controller/`: `UsuarioControllerAgricultorTest`, `UsuarioControllerCompradorTest`, `UsuarioControllerTest`.

### HU-01

| Requisito | Criterio de aceptación | Prueba candidata | Estado |
|---|---|---|---|
| RF-01.1 | CA-01.1 | `UsuarioControllerAgricultorTest` | Clase existe; confirmar cobertura |
| RF-01.2 | CA-01.4 | `RegistroAgricultorTDDTest`, `UsuarioServiceAgricultorTest` | Clase existe; confirmar cobertura |
| RF-01.3 | CA-01.3 | `UsuarioControllerAgricultorTest` | Clase existe; confirmar cobertura |
| RF-01.4 | CA-01.2 | `UsuarioServiceAgricultorTest` | Clase existe; confirmar cobertura |
| RF-01.5 | CA-01.1 | `UsuarioServiceAgricultorTest` | Clase existe; confirmar cobertura |
| RF-01.6 | CA-01.1 | `UsuarioServiceAgricultorTest` | Clase existe; confirmar cobertura |
| RF-01.7, RN-01.1 | CA-01.5 | `RegistroAgricultorTDDTest`, `UsuarioControllerAgricultorTest` | Clase existe; confirmar cobertura |
| RI-01.1 | CA-01.3, CA-01.4 | Inspección de la interfaz | Por crear |
| RNF-01.1 | N/A | Inspección del DDL y de la entidad | Por verificar |
| RNF-01.2 | CA-01.6 | Prueba de carga | Por crear |

### HU-11

| Requisito | Criterio de aceptación | Evidencia o prueba | Estado |
|---|---|---|---|
| RF-11.1, RF-11.2, RF-11.3 | CA-11.1, CA-11.2 | Workflow en `.github/workflows/` y resultado de un PR | Por verificar |
| RF-11.4, RNF-11.2 | CA-11.3 | Checkstyle configurado (PR #1 del repositorio); falta confirmarlo dentro del workflow de CI | Por verificar |
| RF-11.5, RNF-11.1 | CA-11.4 | Reporte de JaCoCo y umbral de 60 % en el `pom.xml` | Por verificar |
| RF-11.6 | CA-11.5 | Clases JUnit existentes (por ejemplo `RegistroAgricultorTDDTest`) | Existe |
| RF-11.7 | N/A | `docs/tdd/hu-11-tdd.md` y `docs/bdd/hu-11-bdd.md` (ambos con 0 bytes) | Pendiente: vacíos |
| RN-11.1 | CA-11.6 | `docs/kanban-policies.md` | Existe |
| RN-11.2 | CA-11.6 | `docs/contributing.md` | Existe (revisar contenido) |
| RN-11.3 | CA-11.6 | `docs/pr-review.md` | Existe (revisar contenido) |
| RNF-11.3, RN-11.4 | CA-11.7 | PR de refactorización SOLID y su informe de hallazgos | Por verificar |

## 5. Resumen

- **Completo y verificable hoy:** RN-11.1, RN-11.2, RN-11.3 (documentos existen) y RF-11.6.
- **Parcial:** los requisitos de HU-01 con pruebas candidatas (falta confirmar métodos) y los de arquitectura (falta confirmar contenido de los diagramas).
- **No se puede cerrar hasta crear los artefactos:** TRA-02 completo (no existen casos de uso ni mockups) y la parte de comportamiento de TRA-03 (no existen secuencia ni actividades).
- **Pendiente de contenido:** `docs/tdd/` y `docs/bdd/` están vacíos.