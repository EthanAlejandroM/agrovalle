
# language: es

@HU-11
Característica: Registro de compradores
  Como comprador
  Quiero registrarme en AgroValle Connect
  Para consultar los productos agrícolas disponibles

  Escenario: Registrar un comprador con datos válidos
    Dado que ingreso los datos válidos de un comprador
    Cuando envío la solicitud de registro
    Entonces el sistema responde con código 201
    Y el comprador queda registrado con el rol COMPRADOR

  Escenario: Intentar registrar un comprador con un documento duplicado
    Dado que ya existe un comprador con ese documento
    Cuando intento registrar otro comprador con el mismo documento
    Entonces el sistema responde con código 409
    Y el sistema informa que el documento está duplicado

  Escenario: Intentar registrar un comprador con un documento inválido
    Dado que ingreso los datos de un comprador con un documento inválido
    Cuando envío la solicitud de registro
    Entonces el sistema responde con código 400
    Y el sistema informa que la validación ha fallado
