# Integración continua (CI)

## Propósito

La integración continua ejecuta comprobaciones repetibles cada vez que hay cambios en `develop` o `main`, y antes de integrar un Pull Request dirigido a esas ramas. 

## Qué verifica el workflow

1. Descarga el código del commit que se está comprobando.
2. Configura Eclipse Temurin Java 17, la versión declarada en `pom.xml`, y reutiliza dependencias Maven en caché.
3. Ejecuta `mvn verify`: compila el proyecto, corre pruebas unitarias e integración, genera el informe JaCoCo y falla si la cobertura de líneas global del bundle es menor al 60 %.
4. Ejecuta `mvn checkstyle:check`, usando el `checkstyle.xml` existente como regla del equipo.
5. Publica los resultados de Surefire y el informe JaCoCo como artefacto descargable de la ejecución, incluso si alguna verificación falla.

## Ejecutar localmente

Desde la raíz del repositorio, con Java 17 y Maven instalados:

```bash
mvn --batch-mode --no-transfer-progress verify
mvn --batch-mode --no-transfer-progress checkstyle:check
```

En PowerShell, define variables de prueba solo para esa sesión antes de ejecutar las pruebas:

```powershell
$env:DB_USERNAME = 'ci_user'
$env:DB_PASSWORD = 'ci_password'
mvn --batch-mode --no-transfer-progress verify
mvn --batch-mode --no-transfer-progress checkstyle:check
```

>[!NOTE] 
> JaCoCo genera el informe HTML en `target/site/jacoco/index.html` y el XML en `target/site/jacoco/jacoco.xml`. Surefire deja los resultados en `target/surefire-reports/`. El mínimo del 60 % se mide sobre las líneas de todo el código de producción del bundle; no exige que cada clase alcance el 60 % y tampoco mide cobertura de ramas. Si no se alcanza, `mvn verify` falla y el workflow marca el check como rojo.

## Referencias oficiales

- [GitHub Actions: compilar y probar Java con Maven](https://docs.github.com/en/actions/tutorials/build-and-test-code/java-with-maven)
- [JaCoCo Maven Plugin](https://www.jacoco.org/jacoco/trunk/doc/maven.html)
- [JaCoCo, versiones publicadas](https://www.jacoco.org/jacoco/index.html)
