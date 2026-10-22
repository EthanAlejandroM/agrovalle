# Especificación de requisitos (ISO/IEC/IEEE 29148): HU-01

**Proyecto:** AgroValle Connect  
**Ubicación sugerida:** `docs/requisitos/ieee-hu-01.md`  
**Documento general:** [especificacion-requisitos-ieee-29148.md](./especificacion-requisitos-ieee-29148.md)  
**Historia en el tablero:** [Ver HU-01 en el tablero del Sprint 1](https://github.com/users/EthanAlejandroM/projects/1)

## 1. Introducción

- **Propósito:** Permitir el registro formal de nuevos productores agrícolas (agricultores) en la plataforma para que puedan gestionar y ofertar sus productos directamente a compradores en el Valle del Cauca.
- **Alcance:** Abarca el formulario web de registro de agricultores, la validación de datos en cliente y servidor, el almacenamiento seguro de credenciales (contraseña cifrada con BCrypt) y la creación de su perfil en la base de datos dentro del MVP.
- **Definiciones y siglas:**
  - **HU:** Historia de Usuario.
  - **BCrypt:** Algoritmo de hash de contraseñas.
  - **MVP:** Producto Mínimo Viable.
  - **DANE:** Departamento Administrativo Nacional de Estadística.

## 2. Referencias

- Documento de diseño de AgroValle Connect (`docs/design/README.md`).
- Modelo Entidad-Relación (`docs/design/img/ModeloER_Agrovalle.drawio.svg`).
- Esquema DDL (`src/main/resources/schema.sql`).
- ISO/IEC/IEEE 29148:2018.

## 3. Descripción general

- **Historia de usuario:** Como agricultor del Valle del Cauca, quiero registrarme en la plataforma ingresando mis datos personales y de ubicación, para poder ofertar y publicar mis productos agrícolas directamente a compradores en Cali.
- **Rol(es):** Agricultor / Productor agrícola.
- **Precondiciones:** El usuario no debe poseer una cuenta previa registrada con el mismo correo electrónico.
- **Supuestos y dependencias:** Servicio de base de datos relacional y servidor de aplicaciones Spring Boot operativos. La validación del municipio se apoya técnicamente en una enumeración o archivo de constantes basado en el listado oficial del DANE.

## 4. Requisitos específicos

### 4.1 Funcionales

| ID | Enunciado | Prioridad | Verificación |
|---|---|---|---|
| RF-01.1 | El sistema deberá proveer un formulario de registro para recolectar nombre, correo electrónico, contraseña, teléfono y municipio de origen. | Debe | Demostración |
| RF-01.2 | El sistema deberá validar que la contraseña tenga al menos 8 caracteres, incluyendo al menos una letra y un número. | Debe | Prueba |
| RF-01.3 | El sistema deberá validar que el formato del correo electrónico ingresado cumpla con la sintaxis estándar. | Debe | Prueba |
| RF-01.4 | El sistema deberá verificar que el correo electrónico no se encuentre previamente registrado en la base de datos. | Debe | Prueba |
| RF-01.5 | El sistema deberá cifrar la contraseña del usuario utilizando BCrypt antes de almacenarla en la base de datos. | Debe | Inspección |
| RF-01.6 | El sistema deberá asignar automáticamente el rol de agricultor al nuevo usuario registrado. | Debe | Prueba |
| RF-01.7 | El sistema deberá rechazar el registro cuando el municipio no pertenezca al listado oficial de municipios del Valle del Cauca emitido por el DANE. | Debe | Prueba |

### 4.2 Interfaz de usuario

| ID | Enunciado | Pantalla o flujo |
|---|---|---|
| RI-01.1 | El sistema deberá mostrar un mensaje de error explícito junto al campo inválido al perder el foco o intentar enviar el formulario. | `registro-agricultor.mockup` (por implementar) |

### 4.3 No funcionales

| ID | Categoría | Enunciado (con medida) |
|---|---|---|
| RNF-01.1 | Seguridad | Ninguna columna ni tabla de la base de datos deberá almacenar la contraseña en texto plano. |
| RNF-01.2 | Rendimiento | El proceso completo de registro y persistencia en la base de datos deberá completarse en menos de 3.0 segundos. |

### 4.4 Reglas de negocio

| ID | Regla |
|---|---|
| RN-01.1 | Todo usuario con rol de agricultor debe estar asociado a un municipio válido del departamento del Valle del Cauca según los registros oficiales del DANE. |

### 4.5 Datos

Entidad `Usuario` con los campos almacenados como columnas en la tabla: `id`, `nombre`, `correo`, `contrasena`, `telefono`, `municipio` (tipo VARCHAR) y `rol` (tipo VARCHAR, según el DDL definido en `schema.sql`). La validación de los municipios del DANE se gestiona internamente mediante una enumeración o archivo de constantes de código.

## 5. Criterios de aceptación

| ID | Dado | Cuando | Entonces |
|---|---|---|---|
| CA-01.1 | Un agricultor en el formulario de registro con datos válidos y municipio del Valle del Cauca | Pulsa "Registrarse" | El sistema crea la cuenta, cifra la clave con BCrypt y redirige a la ruta `/agricultor/inicio`. |
| CA-01.2 | Un usuario ingresa un correo electrónico ya registrado | Envía el formulario | El sistema rechaza el registro y muestra el mensaje "El correo electrónico ya se encuentra registrado". |
| CA-01.3 | Un usuario ingresa un correo con sintaxis inválida (sin @ o dominio) | Abandona el campo de correo | El sistema muestra junto al campo el mensaje "Ingrese un correo electrónico válido". |
| CA-01.4 | Un usuario ingresa una contraseña de menos de 8 caracteres o sin combinación de letras y números | Abandona el campo o envía el formulario | El sistema bloquea el envío y muestra "La contraseña debe tener al menos 8 caracteres, incluyendo una letra y un número". |
| CA-01.5 | Un cliente malintencionado manipula la petición HTTP enviando un municipio externo a la lista oficial del DANE | Envía el formulario manipulado | El sistema rechaza la solicitud en el servidor indicando que el municipio no es válido. |
| CA-01.6 | Un agricultor registra su cuenta bajo condiciones normales de red | Completa el registro | La respuesta del servidor y la redirección ocurren en menos de 3.0 segundos. |

## 6. Verificación

Los requisitos RF-01.1 a RF-01.7 y RNF-01.1 se verifican mediante pruebas unitarias y de integración automatizadas (`RegistroAgricultorTDDTest.java` y `RegistroAgricultorBddTest.java`). RI-01.1 y RNF-01.2 se verificarán por inspección de la interfaz y pruebas de carga, respectivamente, aún por implementar.

## 7. Trazabilidad

| Requisito | Caso de uso | Mockup | Diagramas | Modelo de datos | Prueba |
|---|---|---|---|---|---|
| RF-01.1, RF-01.6 | UC-01 Registro Agricultor | `registro-agricultor.mockup` (por implementar) | `ModeloER_Agrovalle.drawio.svg` / `schema.sql` | Entidad `Usuario` (`contrasena`, `rol`) | `RegistroAgricultorTDDTest.java` / `RegistroAgricultorBddTest.java` |
| RF-01.2, RF-01.3, RF-01.4 | UC-01 Registro Agricultor | `registro-agricultor.mockup` (por implementar) | Diagrama de Secuencia (por implementar) | Entidad `Usuario` | `RegistroAgricultorTDDTest.java` |
| RF-01.5, RNF-01.1 | UC-01 Registro Agricultor | N/A | Diagrama de Componentes (por implementar) | Entidad `Usuario` (`contrasena`) | `RegistroAgricultorTDDTest.java` |
| RF-01.7, RN-01.1 | UC-01 Registro Agricultor | Selector de Municipio (por implementar) | `schema.sql` | Entidad `Usuario` (`municipio`) | `RegistroAgricultorTDDTest.java` / `RegistroAgricultorBddTest.java` |
| RI-01.1 | UC-01 Registro Agricultor | `registro-agricultor.mockup` (por implementar) | N/A | N/A | por implementar |
| RNF-01.2 | UC-01 Registro Agricultor | `registro-agricultor.mockup` (por implementar) | Diagrama de Despliegue (por implementar) | N/A | por implementar |