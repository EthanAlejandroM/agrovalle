# Diseño y pruebas BDD de AgroValle Connect

## Propósito y enfoque

Las pruebas BDD describen el comportamiento esperado desde el punto de vista de una persona. Los archivos están vinculados a las historias HU-01 (registro de agricultores) y HU-11 (registro de compradores).

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

Los tres casos de cada historia cubren el registro exitoso, el rechazo de un documento duplicado y el rechazo de un documento inválido. 

Archivos: [registroAgricultoresBDD.feature](https://github.com/EthanAlejandroM/agrovalle/blob/develop/src/test/resources/features/registroAgricultoresBDD.feature) y [registroCompradoresBDD.feature](https://github.com/EthanAlejandroM/agrovalle/blob/develop/src/test/resources/features/registroCompradoresBDD.feature).

## Cómo se implementan

- [RegistroUsuarioSteps.java](https://github.com/EthanAlejandroM/agrovalle/blob/develop/src/test/java/com/agrovalle/bdd/RegistroUsuarioSteps.java) enlaza los pasos en español con acciones y comprobaciones Java. Envía solicitudes `POST` a `/api/v1/auth/register` por medio de `MockMvc` y consulta `UsuarioRepository` para verificar persistencia o duplicados.

- [CucumberSpringConfiguration.java](https://github.com/EthanAlejandroM/agrovalle/blob/develop/src/test/java/com/agrovalle/bdd/CucumberSpringConfiguration.java) integra los escenarios con Spring Boot y configura `MockMvc`.
- [Hooks.java](https://github.com/EthanAlejandroM/agrovalle/blob/develop/src/test/java/com/agrovalle/bdd/Hooks.java) vacía el repositorio antes de cada escenario para aislar sus datos.
- [RunCucumberTest.java](https://github.com/EthanAlejandroM/agrovalle/blob/develop/src/test/java/com/agrovalle/RunCucumberTest.java) define una suite JUnit Platform que incluye el motor `cucumber`, busca las definiciones en `com.agrovalle.bdd` y configura los reportes `pretty`, `summary` y `target/cucumber-reports/cucumber.html`.
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
