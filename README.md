# AgroValle Connect

## Descripción
Aplicación web empresarial (desarrollada en Java 17 / Spring Boot) que busca transformar la cadena de suministro agroalimentaria regional al eliminar la intermediación innecesaria.

## 📌 Visión del Producto
**AgroValle Connect**. Su propósito es conectar de forma directa la oferta agrícola de las fincas del Valle del Cauca con la demanda comercial urbana de municipios como Cali o sus alrededores, garantizando un comercio más justo, eficiente y transparente para productores y compradores. Todo mediante una plataforma que es accesible y fácil de manejar para los usuarios, destacando las diferentes formas de ventas agrícolas tradicionales, compartiendo además calidad y experiencias tanto con clientes como vendedores.

---

##  Integrantes
- Ethan Alejandro Mezu Quizaboni
- Julián David Peña Chocue
- Bairon Palacios Urrutia
- Nicolle Mera Gomez

---

## Estrategia de Ramificación (Git Flow)

Git Flow es una estrategia muy sencilla, clara y ordenada para equipos que especialmente se encuentran en etapa de aprendizaje. Además, trabajar con el flujo de control de versiones estructurado facilita la gestión de funcionalidades nuevas, correcciones y versiones que se encuentren en producción. También ayuda a mantener una comunicación constante con el equipo sobre cada cambio que se vaya realizando, reduciendo así la probabilidad de errores en la rama `main`.

**Guía rápida de ramas:**

- `main`: Contiene únicamente código estable y listo para producción.
- `develop`: Rama principal de integración donde se consolidan todas las nuevas funcionalidades antes de su liberación.
- `feature/*`: Ramas derivadas de `develop` para desarrollar una Historia de Usuario o tarea específica. Se recomienda utilizar nombres descriptivos.
- `hotfix/*` / `bugfix/*`: Ramas dedicadas a la corrección rápida de errores en producción o en etapas de integración.

>[!IMPORTANT]
> - No trabajaremos directamente sobre `main` ni `develop`.
> - Para cada tarea o Historia de Usuario creamos una rama `feature/*`. Evitar crear ramas demasiado generales.
> - Los mensajes de commit se escribirán en inglés siguiendo Conventional Commits.
> - Los nombres de las ramas pueden utilizar español e inglés, siempre que sean descriptivos y consistentes.
> - Luego de trabajar en las ramas creadas, hacemos un Pull Request hacia `develop`.
> - Cualquier duda, contactarse con un integrante de equipo antes de hacer algún cambio.
---

### Diagrama del Flujo de Ramas (Mermaid)

```mermaid

gitGraph
    commit id: "Initial project"

    branch develop
    checkout develop
    commit id: "Initialize develop"

    %% Desarrollo de una Historia de Usuario
    branch feature/HU-01
    checkout feature/HU-01
    commit id: "Implement HU-01"
    commit id: "Add HU-01 tests"

    checkout develop
    merge feature/HU-01

    %% Corrección durante integración
    branch bugfix/HU-01
    checkout bugfix/HU-01
    commit id: "Fix HU-01 validation"

    checkout develop
    merge bugfix/HU-01

    %% Preparación de una versión
    branch release/v1.0.0
    checkout release/v1.0.0
    commit id: "Prepare release"

    %% Liberación a producción
    checkout main
    merge release/v1.0.0 tag: "v1.0.0"

    %% Sincronización de develop
    checkout develop
    merge release/v1.0.0

    %% Corrección urgente en producción
    checkout main
    branch hotfix/v1.0.1
    checkout hotfix/v1.0.1
    commit id: "Fix production issue"

    checkout main
    merge hotfix/v1.0.1 tag: "v1.0.1"

    checkout develop
    merge hotfix/v1.0.1
```

---
## Configuración de la base de datos

El proyecto utiliza PostgreSQL y requiere configurar las siguientes variables de entorno:

```bash
export DB_USERNAME=postgres
export DB_PASSWORD='TU_CONTRASEÑA_DE_POSTGRES'
```
**Nota:** No se debe subir la contraseña real al repositorio. Cada desarrollador debe configurar sus propias variables de entorno localmente.