# Diseño y estado de las pruebas TDD

## Propósito

Se describen los comportamientos que están comprobados en el código y cómo está configurada su ejecución.

En TDD se escribe una prueba que expresa un comportamiento esperado, se implementa lo necesario para hacerla pasar y luego se mejora el código manteniendo las pruebas verdes.

## Alcance

Se crearon **35 métodos anotados con `@Test`, distribuidos en nueve clases Java**. 

### Pruebas de integración del registro

Las clases `RegistroAgricultorTDDTest` y `RegistroCompradorTDDTest` prueban el endpoint `POST /api/v1/auth/register` con Spring Boot, `MockMvc` y H2. Recorren el flujo de controlador, servicio y repositorio; usan `@Transactional` para revertir los datos de cada prueba.

| Clase | Métodos | Comportamientos cubiertos |
|---|---:|---|
| `RegistroAgricultorTDDTest` | 3 | Registro exitoso con persistencia y rol `AGRICULTOR`; documento duplicado con respuesta 409 sin crear otro registro; documento con guion rechazado con 400 sin persistencia. |
| `RegistroCompradorTDDTest` | 5 | Registro exitoso con persistencia y rol `COMPRADOR`; documento duplicado; documento inválido; registro de agricultor y comprador por el mismo endpoint; rechazo del uso como comprador de un documento ya registrado por agricultor. |

### Pruebas del servicio

`UsuarioServiceAgricultorTest`, `UsuarioServiceCompradorTest` y `UsuarioServiceTest` usan Mockito para aislar `UsuarioService` de la base de datos. En cada clase se comprueban dos casos: guardar un usuario cuando el documento no existe —con los datos, rol y fecha de registro esperados— y lanzar `DocumentoDuplicadoException` sin guardar cuando ya existe.

`UsuarioServiceTest` y `UsuarioServiceAgricultorTest` prueban el mismo flujo de registro de agricultor con datos y aserciones equivalentes. Por eso, sus cuatro métodos representan dos casos repetidos entre ambas clases.

### Pruebas del controlador

Las clases usan `@WebMvcTest`, `MockMvc` y un `UsuarioService` simulado. Comprueban la respuesta HTTP y el JSON sin iniciar el repositorio real.

| Clase | Métodos | Comportamientos cubiertos |
|---|---:|---|
| `UsuarioControllerTest` | 8 | Solicitud válida (201); alias JSON `ubicacion_valle`; cuerpo ausente; JSON vacío y cinco campos faltantes; documento inválido; nombre vacío; rol desconocido; documento duplicado (409). |
| `UsuarioControllerAgricultorTest` | 8 | Comprueba los mismos ocho casos de `UsuarioControllerTest` para el registro de agricultor. Los casos son equivalentes y están duplicados en las dos clases. |
| `UsuarioControllerCompradorTest` | 4 | Registro válido como comprador (201) y rol enviado al servicio; documento duplicado (409); documento inválido; rol ausente. |

### Prueba del repositorio

`UsuarioRepositoryTest` contiene un método de contexto: comprueba que `UsuarioRepository` se cree y esté disponible en el contexto de Spring. 

## Ejecución

El workflow de GitHub Actions configura Temurin Java 17 y ejecuta el ciclo Maven completo:

```bash
mvn --batch-mode --no-transfer-progress verify
```

Para ejecutar solo las clases TDD/JUnit de esta ruta, sin seleccionar `RunCucumberTest`, se puede usar desde la raíz del repositorio:

```bash
mvn "-Dtest=Registro*Test,UsuarioRepositoryTest,UsuarioService*Test,UsuarioController*Test" test
```

Las pruebas de integración necesitan las variables referenciadas por `src/test/resources/application.yml`. En PowerShell:

```powershell
$env:DB_USERNAME = 'ci_user'
$env:DB_PASSWORD = 'ci_password'
mvn "-Dtest=Registro*Test,UsuarioRepositoryTest,UsuarioService*Test,UsuarioController*Test" test
```

El proyecto usa Surefire 3.5.4. Los resultados se generan en `target/surefire-reports/`; JaCoCo genera su reporte durante `verify` y el `pom.xml` exige un mínimo de 60 % tanto en instrucciones como en ramas para el bundle completo. El control se ejecuta durante `verify`; `mvn test` por sí solo corre las pruebas pero no llega a ese gate. GitHub Actions publica los reportes Surefire y JaCoCo en el artefacto `ci-reports`.

## Archivos 

- [pom.xml](https://github.com/EthanAlejandroM/agrovalle/blob/develop/pom.xml)
- [Workflow de CI](https://github.com/EthanAlejandroM/agrovalle/blob/develop/.github/workflows/ci.yml)
- [Configuración de pruebas](https://github.com/EthanAlejandroM/agrovalle/blob/develop/src/test/resources/application.yml)
