# language: es

@HU-13
Característica: Control de inventario
  Como agricultor del Valle del Cauca
  Quiero controlar la cantidad disponible de mis productos a través del lote
  Para mantener el inventario actualizado y evitar ofrecer productos que ya no están disponibles

  Antecedentes:
    Dado que existe la categoría de producto "Hortalizas"
    Y que existe un agricultor con sesión iniciada
    Y que ese agricultor tiene publicado el producto "Tomate chonto" con 500 unidades disponibles

  # ------------------------------------------------------------------
  # Actualización del inventario de un lote DISPONIBLE
  # ------------------------------------------------------------------

  Escenario: Reducir la cantidad disponible de un lote
    Cuando actualiza la cantidad disponible del lote de "Tomate chonto" a 350
    Entonces la API responde con estado HTTP 200
    Y la API confirma que el lote se actualizó correctamente
    Y la API confirma que el lote queda en estado "DISPONIBLE"
    Y el lote de "Tomate chonto" queda con 350 unidades disponibles
    Y el lote de "Tomate chonto" queda con una producción de 500 unidades
    Y la trazabilidad más reciente del lote de "Tomate chonto" es "DISPONIBLE"
    Y la trazabilidad del lote de "Tomate chonto" suma 2 registros

  Esquema del escenario: Aceptar cantidades disponibles en los límites permitidos
    Cuando actualiza la cantidad disponible del lote de "Tomate chonto" a <cantidad>
    Entonces la API responde con estado HTTP 200
    Y la API confirma que el lote queda en estado "DISPONIBLE"
    Y el lote de "Tomate chonto" queda con <cantidad> unidades disponibles

    Ejemplos:
      | cantidad |
      | 1        |
      | 500      |

  Escenario: Agotar un lote al dejar la cantidad disponible en cero
    Cuando actualiza la cantidad disponible del lote de "Tomate chonto" a 0
    Entonces la API responde con estado HTTP 200
    Y la API confirma que el lote queda en estado "AGOTADO"
    Y el lote de "Tomate chonto" queda con 0 unidades disponibles
    Y la trazabilidad más reciente del lote de "Tomate chonto" es "AGOTADO"
    Y la trazabilidad del lote de "Tomate chonto" suma 2 registros

  # ------------------------------------------------------------------
  # Reabastecimiento de un lote AGOTADO
  # ------------------------------------------------------------------

  Escenario: Reabastecer un lote agotado con nueva producción y fecha de cosecha
    Dado que el lote de "Tomate chonto" está agotado
    Cuando reabastece el lote agotado de "Tomate chonto" con producción de 200, disponible 120 y cosecha de ayer
    Entonces la API responde con estado HTTP 200
    Y la API confirma que el lote queda en estado "DISPONIBLE"
    Y el lote de "Tomate chonto" queda con 120 unidades disponibles
    Y el lote de "Tomate chonto" queda con una producción de 200 unidades
    Y la trazabilidad más reciente del lote de "Tomate chonto" es "DISPONIBLE"
    Y la trazabilidad del lote de "Tomate chonto" suma 3 registros

  Escenario: Reabastecer un lote agotado indicando solo la cantidad disponible
    Dado que el lote de "Tomate chonto" está agotado
    Cuando actualiza la cantidad disponible del lote de "Tomate chonto" a 80
    Entonces la API responde con estado HTTP 200
    Y la API confirma que el lote queda en estado "DISPONIBLE"
    Y el lote de "Tomate chonto" queda con 80 unidades disponibles
    Y el lote de "Tomate chonto" queda con una producción de 500 unidades

  # ------------------------------------------------------------------
  # Rechazos por reglas del inventario
  # ------------------------------------------------------------------

  Escenario: Rechazar una cantidad disponible mayor que la producida
    Cuando actualiza la cantidad disponible del lote de "Tomate chonto" a 600
    Entonces la API responde con estado HTTP 400
    Y la API informa el código de error "CANTIDAD_LOTE_INVALIDA"
    Y el inventario del lote de "Tomate chonto" no se modifica

  Escenario: Rechazar una cantidad disponible negativa
    Cuando actualiza la cantidad disponible del lote de "Tomate chonto" a -5
    Entonces la API responde con estado HTTP 400
    Y la API informa el código de error "VALIDACION_FALLIDA"
    Y el inventario del lote de "Tomate chonto" no se modifica

  Escenario: Rechazar el cambio de cantidad producida en un lote disponible
    Cuando intenta cambiar la cantidad producida del lote de "Tomate chonto" a 800
    Entonces la API responde con estado HTTP 409
    Y la API informa el código de error "CAMPO_NO_EDITABLE"
    Y el inventario del lote de "Tomate chonto" no se modifica

  Escenario: Rechazar el cambio de fecha de cosecha en un lote disponible
    Cuando intenta cambiar la fecha de cosecha del lote de "Tomate chonto" a la de ayer
    Entonces la API responde con estado HTTP 409
    Y la API informa el código de error "CAMPO_NO_EDITABLE"
    Y el inventario del lote de "Tomate chonto" no se modifica

  Escenario: Rechazar una actualización sin ningún campo
    Cuando envía la actualización del lote de "Tomate chonto" sin ningún campo
    Entonces la API responde con estado HTTP 400
    Y la API informa el código de error "SOLICITUD_SIN_CAMBIOS"
    Y el inventario del lote de "Tomate chonto" no se modifica

  Escenario: Rechazar reabastecer un lote agotado con cantidad cero
    Dado que el lote de "Tomate chonto" está agotado
    Cuando actualiza la cantidad disponible del lote de "Tomate chonto" a 0
    Entonces la API responde con estado HTTP 400
    Y la API informa el código de error "CANTIDAD_LOTE_INVALIDA"
    Y el inventario del lote de "Tomate chonto" no se modifica

  Escenario: Rechazar reabastecer un lote agotado sin cantidad disponible
    Dado que el lote de "Tomate chonto" está agotado
    Cuando intenta cambiar la cantidad producida del lote de "Tomate chonto" a 200
    Entonces la API responde con estado HTTP 400
    Y la API informa el código de error "CANTIDAD_LOTE_INVALIDA"
    Y el inventario del lote de "Tomate chonto" no se modifica

  Escenario: Rechazar reabastecer un lote agotado con disponible mayor que la nueva producción
    Dado que el lote de "Tomate chonto" está agotado
    Cuando reabastece el lote agotado de "Tomate chonto" con producción de 100, disponible 150 y cosecha de ayer
    Entonces la API responde con estado HTTP 400
    Y la API informa el código de error "CANTIDAD_LOTE_INVALIDA"
    Y el inventario del lote de "Tomate chonto" no se modifica

  Escenario: Rechazar reabastecer un lote agotado con fecha de cosecha posterior a hoy
    Dado que el lote de "Tomate chonto" está agotado
    Cuando reabastece el lote agotado de "Tomate chonto" con disponible 100 y cosecha posterior a la fecha actual
    Entonces la API responde con estado HTTP 400
    Y la API informa el código de error "FECHA_COSECHA_INVALIDA"
    Y el inventario del lote de "Tomate chonto" no se modifica

  # ------------------------------------------------------------------
  # Autenticación, autorización y existencia de recursos
  # ------------------------------------------------------------------

  Escenario: Rechazar la actualización hecha por otro agricultor
    Dado que otro agricultor distinto inicia sesión
    Cuando actualiza la cantidad disponible del lote de "Tomate chonto" a 350
    Entonces la API responde con estado HTTP 403
    Y la API informa el código de error "AGRICULTOR_NO_AUTORIZADO"
    Y el inventario del lote de "Tomate chonto" no se modifica

  Escenario: Rechazar la actualización hecha por un comprador
    Dado que un comprador inicia sesión
    Cuando actualiza la cantidad disponible del lote de "Tomate chonto" a 350
    Entonces la API responde con estado HTTP 403
    Y la API informa el código de error "AGRICULTOR_NO_AUTORIZADO"
    Y el inventario del lote de "Tomate chonto" no se modifica

  Escenario: Rechazar la actualización sin sesión iniciada
    Dado que el cliente no ha iniciado sesión
    Cuando actualiza la cantidad disponible del lote de "Tomate chonto" a 350
    Entonces la API responde con estado HTTP 401
    Y el inventario del lote de "Tomate chonto" no se modifica

  Escenario: Rechazar la actualización con un token JWT inválido
    Dado que la petición lleva un token JWT inválido
    Cuando actualiza la cantidad disponible del lote de "Tomate chonto" a 350
    Entonces la API responde con estado HTTP 401
    Y el inventario del lote de "Tomate chonto" no se modifica

  Escenario: Rechazar la actualización de un producto que no existe
    Cuando actualiza un producto que no existe
    Entonces la API responde con estado HTTP 404
    Y la API informa el código de error "PRODUCTO_NO_ENCONTRADO"

  Escenario: Rechazar la actualización de un lote que no existe
    Cuando actualiza un lote que no existe del producto "Tomate chonto"
    Entonces la API responde con estado HTTP 404
    Y la API informa el código de error "LOTE_NO_ENCONTRADO"
    Y el inventario del lote de "Tomate chonto" no se modifica

  Escenario: Rechazar la actualización de un lote a través de un producto que no es el suyo
    Dado que ese agricultor tiene publicado el producto "Cebolla larga" con 300 unidades disponibles
    Cuando actualiza el lote de "Tomate chonto" a través del producto "Cebolla larga" con 100 unidades disponibles
    Entonces la API responde con estado HTTP 404
    Y la API informa el código de error "LOTE_NO_ENCONTRADO"
    Y el inventario del lote de "Tomate chonto" no se modifica
    Y el inventario del lote de "Cebolla larga" no se modifica

  # ------------------------------------------------------------------
  # Pendiente: la historia pide que el cliente vea la nueva cantidad, pero
  # aún no existe un endpoint GET para consultar productos y sus lotes.
  # Se excluye de la ejecución con @pendiente (ver RunCucumberTest).
  # ------------------------------------------------------------------

  @pendiente
  Escenario: El comprador ve la nueva cantidad disponible después de la actualización
    Cuando actualiza la cantidad disponible del lote de "Tomate chonto" a 350
    Entonces un comprador que consulta el catálogo ve "Tomate chonto" con 350 unidades disponibles
