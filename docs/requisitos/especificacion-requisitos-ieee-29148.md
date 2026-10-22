# Especificación de requisitos: formato ISO/IEC/IEEE 29148

**Proyecto:** AgroValle Connect  


## 1. Propósito

Este documento explica cómo se estructura y se redacta una especificación de requisitos de software (SRS) según la norma ISO/IEC/IEEE 29148:2018, y enlaza cada historia de usuario (HU) del backlog con el requisito que la respalda. La norma reemplaza a IEEE 830 y es la referencia actual para documentar requisitos.

## 2. Estructura de una SRS según 29148

| Sección | Qué contiene | Dónde se cubre en AgroValle Connect |
|---|---|---|
| 1. Introducción | Propósito, alcance, visión general del producto (perspectiva, funciones, características de los usuarios, limitaciones) y definiciones | Secciones 1 a 4 del documento de diseño |
| 2. Referencias | Documentos citados | README de diseño, modelos de datos, arquitectura |
| 3. Requisitos específicos | Interfaces externas, funciones, usabilidad, rendimiento, requisitos lógicos de datos, restricciones de diseño y atributos del sistema (seguridad, disponibilidad, mantenibilidad) | Historias de usuario y sección 5 del documento de diseño |
| 4. Verificación | Cómo se comprueba cada requisito: prueba, inspección, demostración o análisis | Estrategia de pruebas y criterios de aceptación |
| 5. Apéndices | Glosario, matriz de trazabilidad | Matriz del Bloque F |

## 3. Cómo se redacta un requisito

Cada requisito individual debe ser:

- **Necesario:** si se quita, falta algo que el usuario necesita.
- **Singular:** expresa una sola cosa.
- **No ambiguo:** una sola lectura posible; sin palabras como "rápido", "fácil" o "adecuado" sin una cifra.
- **Completo:** no depende de información que falta.
- **Factible:** se puede construir con el alcance del MVP.
- **Verificable:** existe una forma objetiva de comprobarlo.
- **Correcto y conforme:** refleja la necesidad real y sigue la plantilla acordada.

El conjunto de requisitos además debe ser consistente (sin contradicciones), completo y comprensible.

**Plantilla de enunciado:** `El sistema deberá <acción> <objeto> <condición o medida>.`

**Ficha de cada requisito:**

| Campo | Contenido |
|---|---|
| ID | RF-xx (funcional), RI-xx (interfaz), RNF-xx (no funcional), RN-xx (regla de negocio) |
| Enunciado | Con "El sistema deberá…" |
| Prioridad | MoSCoW: Debe, Debería, Podría, No por ahora |
| Fuente | HU de origen (con enlace) |
| Criterio de aceptación | Dado / Cuando / Entonces |
| Verificación | Prueba, inspección, demostración o análisis |

## 4. De historia de usuario a requisito

Una HU tiene la forma "Como `<rol>` quiero `<acción>` para `<beneficio>`". Cada HU se descompone en uno o más requisitos de la tabla anterior, y sus criterios de aceptación pasan a ser los de la ficha. Así queda trazable: HU → requisito → caso de uso → mockup → modelo de datos → prueba (Bloque F).

## 5. Historias de usuario

Cada HU tiene su propia especificación en formato IEEE 29148. Este documento general explica el formato y enlaza a cada una.

| HU | Título | Especificación IEEE | HU en el tablero |
|---|---|---|---|
| HU-01 | Registro de nuevos productores agrícolas (Agricultores) | [ieee-hu-01.md](./ieee-hu-01.md) | [Tablero del Sprint 1](https://github.com/users/EthanAlejandroM/projects/1) |
| HU-11 | Configuración de CI, pruebas automatizadas, políticas Kanban y refactorización SOLID | [ieee-hu-11.md](./ieee-hu-11.md) | [Tablero del Sprint 1](https://github.com/users/EthanAlejandroM/projects/1) |

## 6. Referencias

- ISO/IEC/IEEE 29148:2018, *Systems and software engineering — Life cycle processes — Requirements engineering*.
- Documento de diseño de AgroValle Connect (README de diseño).
 |