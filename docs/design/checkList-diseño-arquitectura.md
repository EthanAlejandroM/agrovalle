## CheckList de calidad de arquitectura
[ ] En la aplicación existe una división estricta e independiente por capas, es decir, existe una capa de presentación **Controllers**, lógica de negocio **Services**, acceso a datos **Repositories/ DAO** y modelos de dominio **Entities / DTOs**.

[ ] Se maneja un patron MVC, donde los controladores REST se encargan unicamente  de recibir las peticiones HTTP.

[ ] Se utiliza la inyección de dependencias de Spring **@Autowired** para desacoplar las clases y facilitar la legibilidad del código.

[ ] Se utilizan DTOs para la transferencia de datos entre cliente y servidor, evitando exponer directamente las entidades JPA a la capa externa.

[ ] Las rutas utilizan la convención de recursos en plural */api/v1/*  y los verbos HTTP adecuados **GET**, **POST**, **PUT**, **DELETE**, **PATCH**.

[ ] Los controladores retornan respuestas estándar estructuradas: **200 OK** para consultas exitosas, **201 Created** para creación de pedidos, **400 Bad Request** para errores de validación y **404 Not Foun**.

[ ] La arquitectura cuenta con un controlador global de errores **@ControllerAdvice / @ExceptionHandler** para interceptar fallos y devolver mensajes estructurados y amigables con el usuario final.

[ ] El acceso a la base de datos está totalmente encapsulado utilizando la abstracción de **JpaRepository** o DAOs bien estructurados

[ ] Las operaciones de escritura o modificaciones complejas estan delimitadas como transaccionales **@Transactional** para garantizar la consistencia.

[ ] Está configurado un pool de conexiones para gestionar de forma eficiente la concurrencia de compradores y agricultores accediendo simultáneamente.

[ ] Se definió el mecanismo para autenticar usuarios JWT

[ ] La arquitectura restringe el acceso a los endpoints según el rol autenticado.

[ ] Se diseñaron filtros en la cadena de seguridad para interceptar, validar credenciales y rechazar peticiones no autorizadas antes de tocar la capa de controladores.

[ ] La estructura de paquetes **com.agrovalle.** está organizada lógicamente ya sea por capas o por módulos

[ ] Los parámetros de configuración, claves de base de datos, puertos, credenciales de correo están aislados en archivos de entorno application.yml
 