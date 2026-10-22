# Especificación de requisitos (ISO/IEC/IEEE 29148): HU-11

**Proyecto:** AgroValle Connect
**Documento general:** [especificacion-requisitos-ieee-29148.md](./especificacion-requisitos-ieee-29148.md)
**Historia en el tablero:** [Ver tarjeta de HU-11 en GitHub Projects](https://github.com/users/EthanAlejandroM/projects/1)
## 1. Introducción

- **Propósito:** Asegurar la calidad técnica del código del Sprint 1 mediante integración continua, pruebas automatizadas, un flujo de trabajo Kanban con revisión por pares y una revisión de diseño SOLID.
- **Alcance:** Incluye la configuración de CI con GitHub Actions (Bloque A), las pruebas con JUnit 5, Mockito y TDD (Bloque B), las políticas de Kanban y revisión cruzada (Bloque C) y la refactorización SOLID de dos componentes (Bloque D). No incluye nuevas funcionalidades para el agricultor ni el comprador.
- **Definiciones y siglas:**
  - **CI:** integración continua.
  - **TDD:** desarrollo guiado por pruebas.
  - **DIP:** principio de inversión de dependencias.
  - **PR:** Pull Request.
  - **JaCoCo:** herramienta de cobertura de pruebas.
  - **Checkstyle:** herramienta de análisis de estilo de código.

## 2. Referencias

- Documento de diseño de AgroValle Connect (README de diseño).
- ISO/IEC/IEEE 29148:2018.
- Listado de tareas del Sprint 1 (Bloques A a D).

## 3. Descripción general

- **Historia de usuario:** Como miembro del equipo de desarrollo,quiero configurar canalizaciones de integración continua, pruebas automatizadas, políticas de flujo y refactorizar componentes bajo principios SOLID,para garantizar la calidad técnica y la entrega consistente de los incrementos del Sprint 1. *(Confirmar el enunciado exacto con el texto de la HU-11 en el tablero.)*
- **Rol(es):** Equipo de desarrollo y revisores de Pull Requests.
- **Precondiciones:** Proyecto Maven funcional en el repositorio de GitHub.
- **Supuestos y dependencias:** Se dispone de GitHub Actions; los componentes a refactorizar existen en el código del Sprint 1.

## 4. Requisitos específicos

### 4.1 Funcionales

| ID | Enunciado | Prioridad | Verificación |
|---|---|---|---|
| RF-11.1 | El sistema de CI deberá ejecutar un workflow de GitHub Actions en cada Pull Request. | Debe | Demostración |
| RF-11.2 | El workflow deberá configurar la versión de Java requerida por el proyecto Maven y Maven. | Debe | Inspección |
| RF-11.3 | El workflow deberá compilar el proyecto y ejecutar las pruebas automatizadas. | Debe | Demostración |
| RF-11.4 | El workflow deberá ejecutar Checkstyle e informar las violaciones encontradas. | Debe | Demostración |
| RF-11.5 | El workflow deberá generar el reporte de cobertura de JaCoCo. | Debe | Demostración |
| RF-11.6 | El proyecto deberá incluir pruebas con JUnit 5 y Mockito para al menos una regla de negocio desarrollada con TDD. | Debe | Prueba |
| RF-11.7 | El equipo deberá adjuntar evidencia de la ejecución de las pruebas y de la cobertura. | Debe | Inspección |

### 4.2 Interfaz de usuario

No aplica: esta HU no define pantallas.

### 4.3 No funcionales

| ID | Categoría | Enunciado (con medida) |
|---|---|---|
| RNF-11.1 | Calidad | La cobertura de pruebas deberá ser de al menos 60 %. |
| RNF-11.2 | Mantenibilidad | El código deberá cumplir las reglas de Checkstyle configuradas, sin violaciones nuevas tras cada cambio. |
| RNF-11.3 | Mantenibilidad | Al menos dos componentes del Sprint 1 deberán ser revisados y refactorizados según los principios SOLID, incluida la inversión de dependencias (DIP). |

### 4.4 Reglas de negocio (de proceso)

| ID | Regla |
|---|---|
| RN-11.1 | Las políticas explícitas del flujo Kanban (condiciones de entrada y salida de cada estado, bloqueos y revisión) deberán estar documentadas en `docs/kanban-policies.md`. |
| RN-11.2 | Debe existir una política de revisión cruzada, documentada en `CONTRIBUTING.md` o documentación equivalente. |
| RN-11.3 | Todo Pull Request deberá cumplir la política de aprobación definida antes de integrarse. |
| RN-11.4 | Tras refactorizar, se deberán ejecutar las pruebas y Checkstyle, y el cambio se entregará mediante un Pull Request con revisión solicitada. |

### 4.5 Datos

No aplica: esta HU no introduce entidades de datos.

## 5. Criterios de aceptación

| ID | Dado | Cuando | Entonces |
|---|---|---|---|
| CA-11.1 | Un Pull Request abierto en el repositorio | Se crea o se actualiza el PR | El workflow de CI se ejecuta y muestra su resultado en el PR. |
| CA-11.2 | El workflow en ejecución | Termina la etapa de compilación y pruebas | Falla si hay un error de compilación o una prueba fallida. |
| CA-11.3 | El workflow en ejecución | Se ejecuta Checkstyle | Se informan las violaciones de estilo encontradas. |
| CA-11.4 | El reporte de JaCoCo generado | Se revisa la cobertura | Se verifica que alcanza al menos el 60 %. |
| CA-11.5 | Una regla de negocio desarrollada con TDD | Se ejecutan las pruebas JUnit 5 con Mockito | Las pruebas pasan y quedan como evidencia adjunta. |
| CA-11.6 | Los documentos de políticas | Se revisan en el repositorio | Existen `docs/kanban-policies.md` y la política de revisión cruzada, con las condiciones descritas. |
| CA-11.7 | Dos componentes refactorizados | Se ejecutan las pruebas y Checkstyle | Ambos pasan y el cambio se entrega en un PR con revisión solicitada. |

## 6. Verificación

Los requisitos de CI (RF-11.1 a RF-11.5) se verifican con la ejecución del workflow en un Pull Request real. Las pruebas (RF-11.6) y la cobertura (RNF-11.1) se verifican con los reportes de JUnit y JaCoCo adjuntos como evidencia. Las reglas de proceso (RN-11.1 a RN-11.4) y los requisitos de refactorización (RNF-11.3) se verifican por inspección de los documentos y del Pull Request.

## 7. Trazabilidad

| Requisito | Tareas del Sprint 1 | Artefacto o evidencia | Verificación |
|---|---|---|---|
| RF-11.1 | CI-02, CI-07 | Workflow de GitHub Actions; PR real | Demostración |
| RF-11.2 | CI-01, CI-03 | Workflow; `pom.xml` | Inspección |
| RF-11.3 | CI-04 | Workflow; resultado del PR | Demostración |
| RF-11.4, RNF-11.2 | CI-05 | Reporte de Checkstyle | Demostración |
| RF-11.5, RNF-11.1 | CI-06, QA-02 | Reporte de JaCoCo | Demostración |
| RF-11.6 | QA-01 | Clases de prueba JUnit 5 y Mockito | Prueba |
| RF-11.7 | QA-03 | Evidencia adjunta | Inspección |
| RN-11.1 | KB-01 | `docs/kanban-policies.md` | Inspección |
| RN-11.2 | KB-02 | `CONTRIBUTING.md` | Inspección |
| RN-11.3 | KB-03, CI-08 | Política de aprobación de PR | Inspección |
| RNF-11.3, RN-11.4 | SOL-01 a SOL-07 | Informe de hallazgos y PR de refactorización | Inspección |
