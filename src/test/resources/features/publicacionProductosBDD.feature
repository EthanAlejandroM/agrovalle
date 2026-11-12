# language: es

@HU-02
Característica: Publicación de productos
  Como agricultor del Valle del Cauca
  Quiero publicar mis cosechas en AgroValle Connect
  Para que los compradores puedan verlas

  Antecedentes:
    Dado que existe la categoría de producto "Hortalizas"

  Escenario: Publicar un producto con datos válidos
    Dado que existe un agricultor autenticado con token JWT
    Cuando publica el producto "Tomate chonto" de tipo "Hortaliza" con cantidad 500 y precio 2500
    Entonces el sistema responde con el código HTTP 201
    Y la respuesta incluye un identificador de producto
    Y el producto "Tomate chonto" queda guardado con precio 2500
    Y el lote inicial queda guardado con 500 unidades disponibles
    Y la trazabilidad del lote queda en estado "DISPONIBLE"

  Escenario: Cada producto publicado recibe un identificador único
    Dado que existe un agricultor autenticado con token JWT
    Cuando publica el producto "Tomate chonto" de tipo "Hortaliza" con cantidad 500 y precio 2500
    Y publica el producto "Cebolla larga" de tipo "Hortaliza" con cantidad 300 y precio 1800
    Entonces los dos productos reciben identificadores distintos

  Esquema del escenario: Rechazar un producto con precio inválido
    Dado que existe un agricultor autenticado con token JWT
    Cuando publica un producto con cantidad producida 100, cantidad disponible 100 y precio <precio>
    Entonces el sistema responde con el código HTTP 400
    Y el sistema informa el error "VALIDACION_FALLIDA"
    Y no se guarda ningún producto

    Ejemplos:
      | precio |
      | 0      |
      | -100   |

  Escenario: Rechazar un producto con cantidad producida igual a cero
    Dado que existe un agricultor autenticado con token JWT
    Cuando publica un producto con cantidad producida 0, cantidad disponible 0 y precio 2500
    Entonces el sistema responde con el código HTTP 400
    Y el sistema informa el error "VALIDACION_FALLIDA"
    Y no se guarda ningún producto

  Escenario: Rechazar un producto cuya cantidad disponible supera la producida
    Dado que existe un agricultor autenticado con token JWT
    Cuando publica un producto con cantidad producida 100, cantidad disponible 150 y precio 2500
    Entonces el sistema responde con el código HTTP 400
    Y el sistema informa el error "CANTIDAD_LOTE_INVALIDA"
    Y no se guarda ningún producto

  Escenario: Rechazar un producto en una categoría que no existe
    Dado que existe un agricultor autenticado con token JWT
    Cuando publica un producto en una categoría que no existe
    Entonces el sistema responde con el código HTTP 404
    Y el sistema informa el error "CATEGORIA_NO_ENCONTRADA"
    Y no se guarda ningún producto

  Escenario: Rechazar la publicación hecha por un comprador
    Dado que existe un comprador autenticado con token JWT
    Cuando publica el producto "Tomate chonto" de tipo "Hortaliza" con cantidad 500 y precio 2500
    Entonces el sistema responde con el código HTTP 403
    Y el sistema informa el error "ROL_AGRICULTOR_INVALIDO"
    Y no se guarda ningún producto

  Escenario: Rechazar la publicación sin sesión iniciada
    Dado que no hay ninguna sesión iniciada
    Cuando publica el producto "Tomate chonto" de tipo "Hortaliza" con cantidad 500 y precio 2500
    Entonces el sistema responde con el código HTTP 401
    Y no se guarda ningún producto

  Escenario: Rechazar la publicación con un token JWT inválido
    Dado que el cliente envía un token JWT inválido
    Cuando publica el producto "Tomate chonto" de tipo "Hortaliza" con cantidad 500 y precio 2500
    Entonces el sistema responde con el código HTTP 401
    Y no se guarda ningún producto

  Escenario: Rechazar un producto con fecha de cosecha posterior a hoy
    Dado que existe un agricultor autenticado con token JWT
    Cuando publica un producto con fecha de cosecha posterior a la fecha actual
    Entonces el sistema responde con el código HTTP 400
    Y el sistema informa el error "FECHA_COSECHA_INVALIDA"
    Y no se guarda ningún producto

  Escenario: Rechazar la publicación a nombre de otro agricultor
    Dado que existe un agricultor autenticado con token JWT
    Y que existe otro agricultor registrado
    Cuando publica un producto a nombre del otro agricultor
    Entonces el sistema responde con el código HTTP 403
    Y el sistema informa el error "AGRICULTOR_NO_AUTORIZADO"
    Y no se guarda ningún producto

  Escenario: Rechazar la publicación de un comprador a nombre de un agricultor
    Dado que existe un comprador autenticado con token JWT
    Y que existe otro agricultor registrado
    Cuando publica un producto a nombre del otro agricultor
    Entonces el sistema responde con el código HTTP 403
    Y el sistema informa el error "AGRICULTOR_NO_AUTORIZADO"
    Y no se guarda ningún producto
