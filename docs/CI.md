# Integración continua (CI) de AgroValle Connect

## Propósito

El workflow propuesto está en `.github/workflows/ci.yml`.

## Qué verifica el workflow

1. Descarga el código del commit que se está comprobando.
2. Configura Eclipse Temurin Java 17, la versión declarada en `pom.xml`, y reutiliza dependencias Maven en caché.
3. Ejecuta `mvn verify`: compila el proyecto, corre pruebas unitarias e integración, genera el informe JaCoCo y falla si la cobertura global del bundle es menor al 60 % en instrucciones o en ramas.
4. Ejecuta `mvn checkstyle:check`, usando el `checkstyle.xml` existente como regla del equipo.
5. Publica los resultados de Surefire y el informe JaCoCo como artefacto descargable de la ejecución, incluso si alguna verificación falla.

El README del repositorio indica que los mensajes de commit deben escribirse en inglés y seguir Conventional Commits. El hook local de Husky y Commitlint revisa la estructura, pero ni ese hook ni este workflow detectan el idioma. El equipo debe mantener los mensajes en inglés y señalar cualquier incumplimiento al revisar el Pull Request.

El job define `DB_USERNAME` y `DB_PASSWORD` con valores ficticios porque `src/test/resources/application.yml` los referencia mientras configura H2 en memoria. No se conecta a PostgreSQL; la configuración de producción sigue usando PostgreSQL.

## Ejecutar localmente

Desde la raíz del repositorio, con Java 17 y Maven instalados:

```bash
mvn --batch-mode --no-transfer-progress test
mvn --batch-mode --no-transfer-progress jacoco:check
mvn --batch-mode --no-transfer-progress verify
mvn --batch-mode --no-transfer-progress checkstyle:check
```

`mvn test` genera los datos de ejecución de JaCoCo, y `mvn jacoco:check` verifica por separado sus umbrales. Para correr todas las fases y el gate en una sola ejecución, usa `mvn verify`.

En PowerShell, define variables de prueba solo para esa sesión antes de ejecutar las pruebas:

```powershell
$env:DB_USERNAME = 'ci_user'
$env:DB_PASSWORD = 'ci_password'
mvn --batch-mode --no-transfer-progress test
mvn --batch-mode --no-transfer-progress jacoco:check
mvn --batch-mode --no-transfer-progress verify
mvn --batch-mode --no-transfer-progress checkstyle:check
```

JaCoCo genera el informe HTML en `target/site/jacoco/index.html` y el XML en `target/site/jacoco/jacoco.xml`. Surefire deja los resultados en `target/surefire-reports/`. El gate mide el bundle completo del código de producción y exige al menos 60 % tanto en instrucciones como en ramas; no exige que cada clase alcance individualmente ese porcentaje. Si cualquiera de los dos umbrales no se alcanza, `mvn jacoco:check` o `mvn verify` falla y el workflow marca el check como rojo.

## Cómo leer una ejecución de GitHub Actions

1. Abre el Pull Request y espera el check **Java CI / build**.
2. Si está verde, compilación, pruebas y Checkstyle terminaron correctamente.
3. Si está rojo, abre el job y ubica el primer paso fallido; normalmente el log muestra la prueba o regla que causó el fallo.
4. Descarga `ci-reports` en la sección de artefactos para consultar resultados Surefire y el reporte de cobertura, si se generaron.
5. Corrige en la misma rama y sube un nuevo commit; GitHub vuelve a ejecutar CI.

Una ejecución verde demuestra que pasaron estos controles para ese commit. 


## Correspondencia con las tareas asignadas

| Tarea | Evidencia |
|---|---|
| CI-01 Maven y Java | `pom.xml`: propiedad `java.version` 17; job con Temurin 17. |
| CI-02 Workflow | `.github/workflows/ci.yml`, eventos `push`, `pull_request` y `workflow_dispatch`. |
| CI-03 Entorno Java/Maven | `actions/setup-java`, Temurin 17 y caché Maven. |
| CI-04 Compilación y pruebas | `mvn verify` ejecuta el ciclo Maven hasta `verify`. |
| CI-05 Checkstyle | `mvn checkstyle:check` y el `checkstyle.xml` ya existente. |
| CI-06 JaCoCo | El plugin crea reportes HTML/XML y hace cumplir mínimos globales de 60 % en instrucciones y 60 % en ramas durante `verify`; se suben como artefacto. |
| CI-07 PR real | Pendiente: crear/actualizar un PR en GitHub y revisar el resultado de Actions. |
| CI-08 Protección de ramas | Recomendación documentada; debe configurarla una persona administradora en GitHub. |

## Referencias oficiales

- [GitHub Actions: compilar y probar Java con Maven](https://docs.github.com/en/actions/tutorials/build-and-test-code/java-with-maven)
- [JaCoCo Maven Plugin](https://www.jacoco.org/jacoco/trunk/doc/maven.html)
- [JaCoCo, versiones publicadas](https://www.jacoco.org/jacoco/index.html)
