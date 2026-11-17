# Diseño y pruebas BDD de AgroValle Connect

## Propósito y enfoque

Las pruebas BDD describen el comportamiento esperado desde el punto de vista de una persona. Los archivos están vinculados a las historias HU-01 (registro de agricultores), HU-11 (registro de compradores), HU-02 (publicación de productos) y HU-13 (control de inventario).

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
| HU-13: control de inventario | Reducir la cantidad disponible de un lote | Respuesta HTTP 200, lote en estado `DISPONIBLE` con la nueva cantidad, producción sin cambios y un registro de trazabilidad nuevo. |
| HU-13: control de inventario | Aceptar cantidades disponibles en los límites permitidos (1 y la cantidad producida) | Respuesta HTTP 200 y lote `DISPONIBLE` con la cantidad indicada. |
| HU-13: control de inventario | Agotar un lote al dejar la cantidad disponible en cero | Respuesta HTTP 200, `estadoLote` `AGOTADO` y trazabilidad `AGOTADO`. |
| HU-13: control de inventario | Reabastecer un lote agotado con nueva producción y fecha de cosecha | Respuesta HTTP 200, lote de nuevo `DISPONIBLE` con producción y disponible actualizados y trazabilidad `DISPONIBLE`. |
| HU-13: control de inventario | Reabastecer un lote agotado indicando solo la cantidad disponible | Respuesta HTTP 200, lote `DISPONIBLE` y producción sin cambios. |
| HU-13: control de inventario | Rechazar una cantidad disponible mayor que la producida | Respuesta HTTP 400, código `CANTIDAD_LOTE_INVALIDA` e inventario sin cambios. |
| HU-13: control de inventario | Rechazar una cantidad disponible negativa | Respuesta HTTP 400, código `VALIDACION_FALLIDA` e inventario sin cambios. |
| HU-13: control de inventario | Rechazar el cambio de cantidad producida o de fecha de cosecha en un lote disponible (2 escenarios) | Respuesta HTTP 409, código `CAMPO_NO_EDITABLE` e inventario sin cambios. |
| HU-13: control de inventario | Rechazar una actualización sin ningún campo | Respuesta HTTP 400, código `SOLICITUD_SIN_CAMBIOS` e inventario sin cambios. |
| HU-13: control de inventario | Rechazar reabastecer un lote agotado con cantidad cero, sin cantidad disponible o con disponible mayor que la nueva producción (3 escenarios) | Respuesta HTTP 400, código `CANTIDAD_LOTE_INVALIDA` e inventario sin cambios. |
| HU-13: control de inventario | Rechazar reabastecer un lote agotado con fecha de cosecha posterior a hoy | Respuesta HTTP 400, código `FECHA_COSECHA_INVALIDA` e inventario sin cambios. |
| HU-13: control de inventario | Rechazar la actualización hecha por otro agricultor o por un comprador (2 escenarios) | Respuesta HTTP 403, código `AGRICULTOR_NO_AUTORIZADO` e inventario sin cambios. |
| HU-13: control de inventario | Rechazar la actualización sin sesión iniciada o con un token JWT inválido (2 escenarios) | Respuesta HTTP 401 e inventario sin cambios. |
| HU-13: control de inventario | Rechazar la actualización de un producto que no existe | Respuesta HTTP 404 y código `PRODUCTO_NO_ENCONTRADO`. |
| HU-13: control de inventario | Rechazar la actualización de un lote que no existe o que pertenece a otro producto (2 escenarios) | Respuesta HTTP 404, código `LOTE_NO_ENCONTRADO` e inventario sin cambios. |

Los tres casos de cada historia cubren el registro exitoso, el rechazo de un documento duplicado y el rechazo de un documento inválido. 

> [!IMPORTANT]
> **HU-13 tiene un escenario pendiente:** «El comprador ve la nueva cantidad disponible después de la actualización».
>
> - **Por qué está pendiente:** el criterio de aceptación de la historia pide que el cliente vea la nueva cantidad, pero todavía no existe un endpoint `GET` para consultar productos y sus lotes. HU-04 y HU-15 solo definen filtros sobre un listado que ninguna historia crea.
> - **Cómo se excluye:** el escenario lleva el tag `@pendiente` y `RunCucumberTest` lo omite (`not @pendiente`). Su último paso aún no tiene definición en Java.
> - **Qué se verifica hoy:** el efecto de la actualización sobre el inventario se comprueba contra la base de datos (`LoteProduccionRepository` y trazabilidad), no desde la perspectiva del comprador.
> - **Cómo retomarlo:** cuando exista la consulta, se implementa el paso «un comprador que consulta el catálogo ve … con … unidades disponibles» en `ControlInventarioSteps.java` y se retira el tag `@pendiente`.

Archivos: [controlInventarioBDD.feature](https://github.com/EthanAlejandroM/agrovalle/blob/develop/src/test/resources/features/controlInventarioBDD.feature), [publicacionProductosBDD.feature](https://github.com/EthanAlejandroM/agrovalle/blob/develop/src/test/resources/features/publicacionProductosBDD.feature), [registroAgricultoresBDD.feature](https://github.com/EthanAlejandroM/agrovalle/blob/develop/src/test/resources/features/registroAgricultoresBDD.feature) y [registroCompradoresBDD.feature](https://github.com/EthanAlejandroM/agrovalle/blob/develop/src/test/resources/features/registroCompradoresBDD.feature).

## Cómo se implementan

- [RegistroUsuarioSteps.java](https://github.com/EthanAlejandroM/agrovalle/blob/develop/src/test/java/com/agrovalle/bdd/RegistroUsuarioSteps.java) enlaza los pasos en español con acciones y comprobaciones Java. Envía solicitudes `POST` a `/api/v1/auth/register` por medio de `MockMvc` y consulta `UsuarioRepository` para verificar persistencia o duplicados.

- [CucumberSpringConfiguration.java](https://github.com/EthanAlejandroM/agrovalle/blob/develop/src/test/java/com/agrovalle/bdd/CucumberSpringConfiguration.java) integra los escenarios con Spring Boot y configura `MockMvc`.
- [PublicacionProductoSteps.java](https://github.com/EthanAlejandroM/agrovalle/blob/develop/src/test/java/com/agrovalle/bdd/PublicacionProductoSteps.java) implementa los pasos de HU-02. Registra un usuario, inicia sesión en `/api/v1/auth/login` para obtener el JWT y publica en `POST /api/v1/productos` con `Authorization: Bearer`. Verifica en `ProductoRepository`, `LoteProduccionRepository` y `EstadoTrazabilidadRepository` lo que quedó guardado.
- [ControlInventarioSteps.java](https://github.com/EthanAlejandroM/agrovalle/blob/develop/src/test/java/com/agrovalle/bdd/ControlInventarioSteps.java) implementa los pasos de HU-13. Registra y autentica usuarios, publica el producto con `POST /api/v1/productos` y actualiza el lote con `PATCH /api/v1/productos/{idProducto}/lotes/{idLote}` usando `Authorization: Bearer`. Verifica en `LoteProduccionRepository` las cantidades guardadas y, con una consulta JPQL, el último estado y el número de registros de trazabilidad del lote. Es independiente de `PublicacionProductoSteps` (token y respuesta propios) y usa redacciones de pasos distintas para evitar definiciones duplicadas en Cucumber.
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
