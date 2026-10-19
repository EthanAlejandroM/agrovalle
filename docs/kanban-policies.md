# Políticas Explícitas del Flujo Kanban

## Descripción

El flujo Kanban del proyecto AgroValle Connect tiene como objetivo organizar y controlar el avance de las historias de usuario y tareas del equipo de desarrollo. Cada estado del tablero representa una fase específica del trabajo y cuenta con criterios definidos de entrada y salida. Estas políticas permiten mejorar la organización, facilitar el seguimiento de las actividades, identificar bloqueos oportunamente y garantizar que todas las tareas sean revisadas antes de darse por finalizadas.

---

# Estados del Flujo

## Backlog

### Descripción
Contiene todas las historias de usuario, funcionalidades y tareas identificadas para el proyecto que aún no están preparadas para iniciar su desarrollo.

### Condiciones de Entrada
- Se identifica una nueva necesidad o requisito.
- Se crea una historia de usuario o tarea.
- La tarea aún no ha sido priorizada.

### Condiciones de Salida
- La tarea está claramente definida.
- Cuenta con una prioridad asignada.
- Posee criterios de aceptación establecidos.

---

## To Do

### Descripción
Contiene las tareas que han sido analizadas y están listas para ser desarrolladas.

### Condiciones de Entrada
- La tarea proviene del Backlog.
- Los requisitos y objetivos están claros.
- La tarea fue priorizada por el equipo.

### Condiciones de Salida
- Un integrante del equipo asume la responsabilidad de la tarea.
- Se inicia el trabajo de implementación.

---

## In Progress

### Descripción
Contiene las tareas que están siendo desarrolladas activamente por un integrante del equipo.

### Condiciones de Entrada
- Existe un responsable asignado.
- El desarrollo de la tarea ha comenzado.

### Condiciones de Salida
- La implementación ha sido completada.
- Se realizaron pruebas iniciales.
- La tarea está lista para revisión.

### Posibles Bloqueos
- Errores de compilación.
- Problemas de conexión con la base de datos.
- Dependencia de otras tareas no finalizadas.
- Falta de información para continuar el desarrollo.
- Conflictos en la integración del código.

---

## In Review

### Descripción
Contiene las tareas terminadas que requieren validación por parte del equipo antes de ser integradas al proyecto.

### Condiciones de Entrada
- La implementación se encuentra finalizada.
- Se creó el Pull Request correspondiente.
- Las pruebas básicas fueron ejecutadas correctamente.

### Condiciones de Salida
- El código fue revisado por otro integrante.
- Se solucionaron las observaciones encontradas.
- El Pull Request fue aprobado.

### Posibles Bloqueos
- Errores detectados durante la revisión.
- Fallo de pruebas.
- Solicitud de cambios por parte del revisor.

---

## Done

### Descripción
Contiene las tareas completamente finalizadas y aprobadas por el equipo.

### Condiciones de Entrada
- El Pull Request fue aprobado.
- Los cambios fueron integrados a la rama de desarrollo.
- Se cumplieron los criterios de aceptación establecidos.

### Resultado Esperado
- La funcionalidad se encuentra terminada.
- La documentación fue actualizada cuando corresponde.
- La tarea queda registrada como completada.

---

# Política de Revisión

Para garantizar la calidad del software, todas las tareas desarrolladas deberán pasar por un proceso de revisión antes de ser consideradas finalizadas.

## Reglas de Revisión

1. Todo Pull Request debe ser revisado por otro integrante del equipo.
2. El autor de los cambios no debe aprobar su propio Pull Request.
3. El revisor verificará:
   - Funcionamiento correcto de la solución.
   - Cumplimiento de los requisitos definidos.
   - Calidad y organización del código.
   - Resultados de las pruebas realizadas.
4. Si se encuentran errores o inconsistencias, se solicitarán cambios antes de la aprobación.
5. Solo las tareas aprobadas podrán pasar al estado Done.

---

## Beneficios del Flujo Kanban

- Mayor control sobre el avance del proyecto.
- Identificación temprana de bloqueos.
- Mejor organización de las tareas.
- Seguimiento claro del trabajo realizado.
- Mayor calidad del software gracias al proceso de revisión.
