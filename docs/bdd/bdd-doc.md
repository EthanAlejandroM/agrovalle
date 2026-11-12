# Diseño y pruebas BDD de AgroValle Connect

## Propósito y enfoque

Las pruebas BDD describen el comportamiento esperado desde el punto de vista de una persona. Los archivos están vinculados a las historias HU-01 (registro de agricultores), HU-11 (registro de compradores) y HU-02 (publicación de productos).

La documentación diferencia los escenarios Gherkin de las pruebas Java de integración que verifican comportamientos relacionados. Así se puede saber qué está expresado como BDD sin sumar pruebas de otra forma como si fueran escenarios `.feature`.

## Escenarios BDD implementados


| Historia | Escenario | Resultado esperado |
|---|---|---|
| HU-01: registro de agricultores | Registrar un agricultor con datos válidos | Respuesta HTTP 201 y agricultor registrado. |
| HU-01: registro de agricultores | Registrar otro agricultor con documento duplicado | Respuesta HTTP 409 y código de error `DOCUMENTO_DUPLICADO`. |
| HU-01: registro de agricultores | Registrar un agricultor con documento inválido | Respuesta HTTP 400 y código `VALIDACION_FALLIDA`. |
| HU-11: registro de compradores | Registrar un comprador con datos válidos | Respuesta HTTP 201 y comprador registrado con rol `COMPRADOR`. |
| HU-11: registro de compradores | Registrar otro comprador con documento duplicado | Respuesta HTTP 409 y código de error `DOCUMENTO_DUPLICADO`. |
| HU-11: registro de compradores | Registrar un comprador con documento inválido | Respuesta HTTP 400 y código `VALIDACION_FALLIDA`. |
| HU-02: publicación de productos | Publicar un producto con datos válidos | Respuesta HTTP 201 con `idProducto`; producto, lote inicial y trazabilidad `DISPONIBLE` guardados. |
| HU-02: publicación de productos | Cada producto publicado recibe un identificador único | Dos publicaciones devuelven `idProducto` distintos. |
| HU-02: publicación de productos | Rechazar un producto con precio inválido (0 o negativo) | Respuesta HTTP 400, código `VALIDACION_FALLIDA` y nada guardado. |
| HU-02: publicación de productos | Rechazar un producto con cantidad producida igual a cero | Respuesta HTTP 400, código `VALIDACION_FALLIDA` y nada guardado. |
| HU-02: publicación de productos | Rechazar un producto cuya cantidad disponible supera la producida | Respuesta HTTP 400, código `CANTIDAD_LOTE_INVALIDA` y nada guardado. |
| HU-02: publicación de productos | Rechazar un producto en una categoría que no existe | Respuesta HTTP 404, código `CATEGORIA_NO_ENCONTRADA` y nada guardado. |
| HU-02: publicación de productos | Rechazar la publicación hecha por un comprador | Respuesta HTTP 403, código `ROL_AGRICULTOR_INVALIDO` y nada guardado. |
| HU-02: publicación de productos | Rechazar la publicación sin sesión iniciada | Respuesta HTTP 401 y nada guardado. |
| HU-02: publicación de productos | Rechazar la publicación con un token JWT inválido | Respuesta HTTP 401 y nada guardado. |
| HU-02: publicación de productos | Rechazar un producto con fecha de cosecha posterior a hoy | Respuesta HTTP 400, código `FECHA_COSECHA_INVALIDA` y nada guardado. |
| HU-02: publicación de productos | Rechazar la publicación a nombre de otro agricultor | Respuesta HTTP 403, código `AGRICULTOR_NO_AUTORIZADO` y nada guardado. |
| HU-02: publicación de productos | Rechazar la publicación de un comprador a nombre de un agricultor | Respuesta HTTP 403, código `AGRICULTOR_NO_AUTORIZADO` y nada guardado. |

Los tres casos de cada historia cubren el registro exitoso, el rechazo de un documento duplicado y el rechazo de un documento inválido. 

Archivos: [publicacionProductosBDD.feature](https://github.com/EthanAlejandroM/agrovalle/blob/develop/src/test/resources/features/publicacionProductosBDD.feature), [registroAgricultoresBDD.feature](https://github.com/EthanAlejandroM/agrovalle/blob/develop/src/test/resources/features/registroAgricultoresBDD.feature) y [registroCompradoresBDD.feature](https://github.com/EthanAlejandroM/agrovalle/blob/develop/src/test/resources/features/registroCompradoresBDD.feature).

## Cómo se implementan

- [RegistroUsuarioSteps.java](https://github.com/EthanAlejandroM/agrovalle/blob/develop/src/test/java/com/agrovalle/bdd/RegistroUsuarioSteps.java) enlaza los pasos en español con acciones y comprobaciones Java. Envía solicitudes `POST` a `/api/v1/auth/register` por medio de `MockMvc` y consulta `UsuarioRepository` para verificar persistencia o duplicados.

- [CucumberSpringConfiguration.java](https://github.com/EthanAlejandroM/agrovalle/blob/develop/src/test/java/com/agrovalle/bdd/CucumberSpringConfiguration.java) integra los escenarios con Spring Boot y configura `MockMvc`.
- [PublicacionProductoSteps.java](https://github.com/EthanAlejandroM/agrovalle/blob/develop/src/test/java/com/agrovalle/bdd/PublicacionProductoSteps.java) implementa los pasos de HU-02. Registra un usuario, inicia sesión en `/api/v1/auth/login` para obtener el JWT y publica en `POST /api/v1/productos` con `Authorization: Bearer`. Verifica en `ProductoRepository`, `LoteProduccionRepository` y `EstadoTrazabilidadRepository` lo que quedó guardado.
- [BddSecurityConfig.java](https://github.com/EthanAlejandroM/agrovalle/blob/develop/src/test/java/com/agrovalle/bdd/BddSecurityConfig.java) replica la cadena de `SecurityConfig` (que no se carga con el perfil `test`) para que el JWT se exija de verdad en las pruebas BDD. Si cambia `SecurityConfig`, debe actualizarse también.
- [CategoriaProductoFabrica.java](https://github.com/EthanAlejandroM/agrovalle/blob/develop/src/test/java/com/agrovalle/CategoriaProductoFabrica.java) crea categorías de prueba, ya que `CategoriaProducto` tiene constructor `protected`.
- [Hooks.java](https://github.com/EthanAlejandroM/agrovalle/blob/develop/src/test/java/com/agrovalle/bdd/Hooks.java) vacía las tablas antes de cada escenario para aislar sus datos, en orden inverso a las llaves foráneas (trazabilidad, lote, producto, categoría y usuario).
- [RunCucumberTest.java](https://github.com/EthanAlejandroM/agrovalle/blob/develop/src/test/java/com/agrovalle/RunCucumberTest.java) define una suite JUnit Platform que incluye el motor `cucumber`, busca las definiciones en `com.agrovalle.bdd` y excluye los escenarios con el tag `@pendiente` y configura los reportes `pretty`, `summary` y `target/cucumber-reports/cucumber.html`.
- [application.yml de pruebas](https://github.com/EthanAlejandroM/agrovalle/blob/develop/src/test/resources/application.yml) configura H2 en memoria en modo de compatibilidad PostgreSQL. Las credenciales se leen de `DB_USERNAME` y `DB_PASSWORD`.

En [pom.xml](https://github.com/EthanAlejandroM/agrovalle/blob/develop/pom.xml) se declaran Java 17, Cucumber 7.20.1 (`cucumber-java`, `cucumber-junit-platform-engine` y `cucumber-spring`), JUnit Platform Suite y Maven Surefire 3.5.4.

## Ejecución

El workflow de GitHub Actions prepara Temurin Java 17 y define las variables de H2. Desde la raíz del repositorio, su ejecución de pruebas es:

```bash
mvn --batch-mode --no-transfer-progress verify
```

Para ejecutar de forma local la suite Cucumber desde Maven, se puede usar el runner definido en el proyecto:

```bash
mvn -Dtest=RunCucumberTest test
```

CI también ejecuta `mvn --batch-mode --no-transfer-progress checkstyle:check`. Los reportes de Surefire se guardan en `target/surefire-reports/`; la suite configura el reporte HTML de Cucumber en `target/cucumber-reports/cucumber.html`. El workflow publica los resultados de Surefire y JaCoCo como el artefacto `ci-reports`.

## Referencias del repositorio

Esta revisión corresponde a la rama [`develop`](https://github.com/EthanAlejandroM/agrovalle/tree/develop), especialmente a `src/test/resources/features`, `src/test/java/com/agrovalle/bdd`, `RunCucumberTest.java`, `pom.xml` y `.github/workflows/ci.yml`.
