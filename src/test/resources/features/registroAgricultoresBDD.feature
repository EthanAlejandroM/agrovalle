
# language: es

@HU-01
Característica: Registro de agricultores
  Como agricultor del Valle del Cauca
  Quiero registrarme en AgroValle Connect
  Para acceder a las funcionalidades destinadas a los agricultores

  Escenario: Registrar un agricultor con datos válidos
    Dado que ingreso los datos válidos de un agricultor
    Cuando envío la solicitud de registro
    Entonces el sistema responde con código 201
    Y el agricultor queda registrado en el sistema

  Escenario: Intentar registrar un agricultor con un documento duplicado
    Dado que ya existe un agricultor con ese documento
    Cuando intento registrar otro agricultor con el mismo documento
    Entonces el sistema responde con código 409
    Y el sistema informa que el documento está duplicado

  Escenario: Intentar registrar un agricultor con un documento inválido
    Dado que ingreso los datos de un agricultor con un documento inválido
    Cuando envío la solicitud de registro
    Entonces el sistema responde con código 400
    Y el sistema informa que la validación ha fallado
