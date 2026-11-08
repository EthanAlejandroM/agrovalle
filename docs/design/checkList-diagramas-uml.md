# CheckList para el disño de los diagrmas UML
A continuación, se desglosan los aspectos a tener en cuenta para los diagramas de componentes, secuencia y despliegue. Cabe aclarar que los diagramas a realizar de AgroValle deben de realizarse para modelar el funcionamiento en general y no de una funcionalidad específica.

## Diagrama de Componentes 

[ ] El diagrama incluye todos los módulos clave del sistema agroValle: Autenticación y Usuarios, Gestión de Productos y Catálogo, Procesamiento de Pedidos y Módulo de Pagos.

[ ] Se distinguen visualmente los componentes de presentación/controladores, la lógica de negocio (Services) y la capa de persistencia (Repositories / Acceso a Datos).

[ ] Las flechas de dependencia reflejan cómo interactúan los subsistemas a nivel global.

[ ] Se contempla de forma general la interfaz o componente de comunicación con pasarelas de pago externas u otros servicios de terceros.

---

## Checklist para el Diagrama de Secuencia 
[ ] El diagrama modela una interacción de cada uno de los usuarios.

[ ] Intervienen actores principales y los componentes del sistem.

[ ] Los mensajes síncronos y asíncronos reflejan correctamente las llamadas reales de la arquitectura, peticiones HTTP, consultas SQL, respuestas JSON.

[ ] Se documentan o contemplan los flujos alternos generales como un fallo en la validación del stock o un rechazo de pago dentro de la secuencia principal o en notas aclaratorias.

---
## Checklist para el Diagrama de Despliegue 

[ ] Se identifican los entornos de ejecución para los usuarios finales,Navegadores web en PC/Laptops y dispositivos móviles Android/iOS.

[ ] Se representa el artefacto principal del backend agroValle.jar corriendo sobre la JVM / Tomcat embebido y su protocolo de comunicación con los clientes (HTTPS / REST).

[ ] Se especifico el motor de base de datos PostgreSQL y el canal de conexión correspondiente (JDBC / TCP).

[ ] Se mapeo la infraestructura externa, como la API de la pasarela de pagos y los puertos o protocolos de red estándar utilizados en toda la arquitectura.