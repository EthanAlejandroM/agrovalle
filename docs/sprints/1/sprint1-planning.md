# Sprint Goal del Sprint 1

Este primer Sprint tiene como objetivo **habilitar el registro de los Agricultores y Clientes**, garantizando la integridad de los datos en PostgreSQL.

Los HU seleccionados para cumplir con el objetivo del Sprint 1 se muestran a continuación:

---

## HU-01: Registro de Agricultores

**Como Agricultor**, quiero registrarme en la plataforma para ofrecer mis productos.

### Priorización

**Must**

### Estimación

**5 Story Points**

### Escenario BDD

- **Given:** Que el usuario ingresa a `/api/v1/auth/register`.
- **When:** Envía un JSON con `nombre`, `ubicacion_valle` y `documento_válido`.
- **Then:** El sistema responde con un **Status 201 Created** y el registro persiste en la base de datos PostgreSQL.

---

## HU-11: Registrar clientes

**Como Comprador**, quiero registrarme en la aplicación, con el fin de poder comprar productos agrícolas.

### Priorización

**Must**

### Estimación

**5 Story Points**

### Escenario BDD

- **Given:** Que el usuario ingresa a `/api/v1/auth/register`.
- **When:** Envía un JSON con `nombre`, `ubicacion_valle` y `documento_válido`.
- **Then:** El sistema responde con un **Status 201 Created** y el registro persiste en la base de datos PostgreSQL.

---

## Carga total del Sprint

Como se observa en el campo de estimación de los HU seleccionados, tanto el **HU-01** como el **HU-11** tienen una estimación de **5 Story Points** cada uno.

Es decir, la carga total del **Sprint 1** fue de:

> **10 Story Points**
Según la estimación de Fibonacci.

---

# Desglose técnico de las tareas correspondientes para los HU seleccionados

| ID Tarea | Descripción técnica | Componente / Tecnología | Atributo ISO 25010 |
|---|---|---|---|
| **T-01.1** | Configurar conexión a PostgreSQL y dependencias | Spring Boot, PostgreSQL, Maven, JDBC | Compatibilidad |
| **T-01.2** | Modelar entidad `Usuario` + enum `TipoUsuario` | Java JPA (Jakarta Persistence), Hibernate, Spring Boot | Mantenibilidad |
| **T-01.3** | Migración de base de datos (tabla usuarios) | PostgreSQL, SQL, `schema.sql` | Fiabilidad |
| **T-01.4** | Crear `UsuarioRepository` (Spring Data JPA) | Spring Data JPA, `JpaRepository`, Java | Mantenibilidad |
| **T-01.5** | DTO base de registro + validaciones (Bean Validation) | Java DTO, Bean Validation, Jakarta Validation | Fiabilidad |
| **T-01.6** | Crear un `Usuario` con `rol=AGRICULTOR`, validando que el documento no esté duplicado | `AuthService.registrarAgricultor()`, Spring Boot, `UsuarioRepository`, JPA | Fiabilidad |
| **T-01.7** | Controlador `POST /api/v1/auth/register` que invoca el servicio y devuelve el ID de usuario creado con `201 Created` | REST Controller, Spring Web, HTTP POST API | Usabilidad |
| **T-01.8** | Validación de documento y manejo de errores | Bean Validation, Spring Validation, Exception Handling | Fiabilidad |
| **T-01.9** | Pruebas unitarias (service + controller) | JUnit 5, Mockito, Spring Test | Mantenibilidad |
| **T-01.10** | Prueba BDD de integración | BDD, Cucumber, JUnit 5 | Adecuación funcional |
| **T-01.11** | Rama `feature/HU-01`, PR revisado por 2 integrantes según el DoD, merge a `develop` | Git, GitHub, Pull Request, Git Flow | Mantenibilidad |
| **T-01.12** | Actualizar documentación | Word, GitHub, documentación técnica | Mantenibilidad |
| **T-11.1** | `AuthService.registrarComprador()` | AuthService, Spring Boot, `UsuarioRepository`, JPA | Fiabilidad |
| **T-11.2** | Endpoint de registro (rol `COMPRADOR`) | REST Controller, Spring Web, HTTP POST API | Usabilidad |
| **T-11.3** | Validación de documento y manejo de errores | Bean Validation, Spring Validation, Exception Handling, `UsuarioRepository` | Fiabilidad |
| **T-11.4** | Pruebas unitarias (service + controller) | JUnit 5, Mockito, Spring Test | Mantenibilidad |
| **T-11.5** | Prueba BDD de integración | Cucumber BDD, JUnit 5, Spring Boot Test | Adecuación funcional |
| **T-11.6** | Code review + PR a `develop` | Git, GitHub, Pull Request, Code Review | Mantenibilidad |
| **T-11.7** | Actualizar documentación | Word, GitHub, documentación técnica | Mantenibilidad |
| **HU-01 & HU-11** | Preparar demo del flujo de registro para ambos roles | PgAdmin | Usabilidad |


