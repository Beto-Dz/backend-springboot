# Prueba backend

Descripción:

Micro-servicio creado con Spring Framework O Spring Boot, el cual contenga un CRUD
de información. El repositorio debe estar dentro del mismo proyecto como parte
de la estructura, pero separado como una capa adicional de datos.
- Los datos deben estar en formato de texto simulando una salida de datos desde otro servicio.
- El micro-servicio consumidor debe tener la estructura necesaria para leer la información que
le regresa el contenido del archivo de texto.
- Los datos del archivo de texto deben contener los datos del usuario:
  - Id.
  - Clave
  - Tipo.
  - Nombre.
  - Username.
  - Apellido Paterno.
  - Apellido Materno.
  - Password.
  - Fecha inicio de sesión.
  - Fecha fin de sesión.
  - Tiempo del usuario en línea (calcular el rango desde que inicio sesión hasta la fecha de consulta => 3h 2m o 0h 55m).
  - Estatus del usuario.

>[!NOTE]
>Durante esta prueba en este framework java, me enfrenté a multiples retos partiendo desde el hecho de que solo sé un poco del framework, sin embargo, el mayor reto sin duda, fue la implementación de JWT para la autenticación.
