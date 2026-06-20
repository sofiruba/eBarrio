# Informe final del proyecto eBarrio

## Enlace al repositorio

Repositorio del proyecto: https://github.com/sofiruba/eBarrio

## a) Descripción del problema

eBarrio es un sistema de gestión operativa para un barrio cerrado. El objetivo fue
centralizar en una aplicacion Java la administración de residentes, viviendas,
visitantes, proveedores, accesos, reclamos, tareas de mantenimiento, incidentes de
seguridad, usuarios y notificaciones.

El problema original era la dispersion de información entre administración, seguridad,
mantenimiento y residentes. El sistema resuelve ese escenario mediante una interfaz
JavaFX con login por rol, vistas separadas para administrador y residente, persistencia
en archivos JSON y una fachada central (`SistemaBarrio`) que concentra los flujos de
negocio.

## b) Requerimientos funcionales resueltos

- Inicio de sesión con usuarios administradores y residentes.
- Vista de administrador con gestión de residentes, viviendas, solicitudes, accesos,
  visitantes, proveedores y notificaciones.
- Vista de residente con dashboard propio, visitantes/proveedores autorizados y
  solicitudes propias.
- Alta y modificación de residentes, con creación automática de cuenta de usuario.
- Persistencia de usuarios, residentes, viviendas, visitantes, accesos, solicitudes y
  notificaciones en JSON.
- Registro, edición, eliminación y filtrado de visitantes y proveedores.
- Registro de ingreso y egreso con fecha y hora.
- Patente opcional para visitantes/proveedores.
- Validaciones de datos de entrada: documento, email, teléfono y patente.
- Creación de reclamos, tareas de mantenimiento e incidentes de seguridad.
- Avance y cancelación de solicitudes mediante estados.
- Generación de notificaciones ante ingresos, egresos y solicitudes.
- Escenarios de prueba por consola para demostrar flujos principales y alternativos.

## c) Justificación del diseño arquitectónico

### Separación de responsabilidades

- `app`: contiene la interfaz JavaFX y las vistas principales.
- `sistema`: contiene `SistemaBarrio`, fachada/controlador de casos de uso.
- `model`: contiene entidades de dominio, reglas de negocio y patrones aplicados.
- `src/data`: contiene los archivos JSON usados como base de datos simple.
- `documentacion`: contiene informe, UML, escenarios y registro de commits.

### Patrones GoF aplicados

#### Facade

Problemática detectada: la interfaz necesitaba ejecutar muchas operaciones sobre
residentes, visitantes, accesos, solicitudes, usuarios, notificaciones y persistencia.
Si cada pantalla conocía todas las clases internas, el acoplamiento crecía demasiado.

Solución con el patron: `SistemaBarrio` expone métodos como `registrarResidente`,
`registrarVisitante`, `registrarAcceso`, `crearReclamo`, `avanzarEstadoSolicitud`,
`autenticarUsuario`, `cargarDesdeJson` y métodos de guardado. La vista invoca la fachada
y no manipula directamente la persistencia ni la creación compleja de objetos.

Ventaja obtenida: menor acoplamiento entre UI y dominio, punto único de entrada para los
casos de uso y mayor facilidad para probar los flujos por consola.

#### Factory Method / Simple Factory

Problemática detectada: las solicitudes comparten datos y comportamiento, pero existen
tipos concretos distintos: reclamos, tareas de mantenimiento e incidentes de seguridad.

Solución con el patrón: `SolicitudFactory.crearSolicitud(...)` recibe el tipo y crea la
subclase correspondiente: `Reclamo`, `TareaMantenimiento` o `IncidenteSeguridad`.

Ventaja obtenida: se centraliza la creación de solicitudes y se evita distribuir
condicionales de construcción en varias partes de la aplicación.

#### State

Problemática detectada: una solicitud cambia de estado siguiendo reglas específicas:
Pendiente, Asignado, En proceso, Resuelto y Cerrado. Cada estado habilita o limita
acciones distintas.

Solución con el patron: `Solicitud` mantiene una referencia a `IEstadoSolicitud`. Las
clases `EstadoPendiente`, `EstadoAsignado`, `EstadoEnProceso`, `EstadoResuelto` y
`EstadoCerrado` implementan las transiciones de `avanzar` y `cancelar`.

Ventaja obtenida: las reglas de transición quedan encapsuladas en clases pequeñas,
cohesivas y fáciles de modificar sin llenar `Solicitud` de condicionales.

#### Observer

Problemática detectada: cuando cambia una solicitud, otros actores del sistema pueden
necesitar enterarse sin que la solicitud dependa de clases concretas.

Solución con el patrón: `Solicitud` mantiene una lista de `IObservador` y notifica con
`notificarObservadores`. `Administrador` implementa `IObservador`.

Ventaja obtenida: bajo acoplamiento entre solicitud y observadores, con posibilidad de
agregar nuevos observadores sin cambiar la clase `Solicitud`.

### Principios SOLID aplicados

- SRP: `Acceso` controla ingreso/egreso, `Visitante` representa autorizados,
  `Notificacion` representa mensajes, los estados gestionan transiciones y
  `SistemaBarrio` coordina casos de uso.
- OCP: se pueden agregar nuevos tipos de solicitud extendiendo `Solicitud` y agregando
  su creacion en `SolicitudFactory`.
- LSP: `Reclamo`, `TareaMantenimiento` e `IncidenteSeguridad` pueden usarse como
  `Solicitud` sin romper los flujos.
- ISP: `IObservador` expone solo el método `actualizar`, suficiente para el patrón
  Observer.
- DIP: `Solicitud` depende de la abstracción `IObservador`, no de un observador concreto.

### Patrones GRASP aplicados

- Controller: `SistemaBarrio` recibe las acciones principales desde la interfaz y las
  pruebas.
- Creator: `SistemaBarrio` crea entidades del flujo y `SolicitudFactory` crea
  solicitudes concretas.
- Information Expert: `Acceso` sabe si está activo, `Residente` administra sus
  visitantes, `Solicitud` delega su avance al estado actual.
- Low Coupling: la vista trabaja contra la fachada y las solicitudes observan
  `IObservador`.
- High Cohesion: los paquetes separan barrio, accesos, solicitudes, notificaciones,
  vistas y datos.

Vinculación con SOLID: Controller y Low Coupling se relacionan con DIP; High Cohesion se
relaciona con SRP; Creator se apoya en SRP y OCP; Information Expert reduce acoplamiento
al ubicar comportamiento cerca de los datos que lo necesitan.

## d) Instrucciones de despliegue

Requisitos:

- JDK instalado.
- JavaFX SDK 17 disponible. El repositorio incluye `eBarrio/libs/javafx-sdk-17.0.19/lib`.

Compilar desde la raiz del repositorio:

```bash
javac --module-path eBarrio/libs/javafx-sdk-17.0.19/lib --add-modules javafx.controls -encoding UTF-8 -d out $(find eBarrio/src -name '*.java')
```

Ejecutar interfaz JavaFX:

```bash
java --module-path eBarrio/libs/javafx-sdk-17.0.19/lib --add-modules javafx.controls -cp out app.MainApp
```

Ejecutar pruebas por consola:

```bash
java -cp out Main
java -cp out model.solicitud.test.TestReclamo
java -cp out model.solicitud.test.EscenariosEjecucion
```

Usuarios de prueba:

- Administrador: `admin@ebarrio.com` / `admin123`
- Residente: `sofia@email.com` / `sofia123`
- Residente: `martin@email.com` / `martin123`
- Residente: `ana@email.com` / `ana123`
- Residente: `facu@gmail.com` / `34567891`

## e) Evidencia de ejecución

Compilación verificada el 20/06/2026:

```text
javac --module-path eBarrio/libs/javafx-sdk-17.0.19/lib --add-modules javafx.controls -Xlint:unchecked -encoding UTF-8 -d /tmp/ebarrio-docs $(find eBarrio/src -name '*.java')
Resultado: sin errores de compilacion.
```

Ejecución de `TestReclamo`:

```text
Estado de solicitud [1]: Pendiente
La solicitud pasa de 'Pendiente' a 'Asignado'.
La solicitud pasa de 'Asignado' a 'En proceso'.
La solicitud pasa de 'En proceso' a 'Resuelto'.
La solicitud pasa de 'Resuelto' a 'Cerrado'.
El estado de la solicitud no puede avanzar: ya fue cerrado.
La solicitud que se encontraba pendiente de asignación ha sido directamente cerrada.
La solicitud ya esta cerrada y no se puede cancelar.
```

Ejecución de `EscenariosEjecucion`:

```text
Ingreso registrado para Camila Ruiz
Egreso registrado para Camila Ruiz
Reclamo creado: Reclamo [1] Luminaria rota
La solicitud pasa de 'Pendiente' a 'Asignado'.
La solicitud pasa de 'Asignado' a 'En proceso'.
La solicitud pasa de 'En proceso' a 'Resuelto'.
La solicitud pasa de 'Resuelto' a 'Cerrado'.
Tarea de mantenimiento creada: TareaMantenimiento [2] Cortar pasto
La solicitud que se encontraba pendiente de asignación ha sido directamente cerrada.
Todos los escenarios de ejecucion finalizaron correctamente.
```

## f) Conclusiones

El proyecto permitió integrar conceptos de programación orientada a objetos, patrones de diseño,
persistencia simple e interfaz gráfica en un dominio concreto. La mayor dificultad fue
mantener coherencia entre el modelo, la interfaz y los datos persistidos, especialmente
cuando se agregaron roles, usuarios, visitantes/proveedores y accesos.

Los patrones aportaron orden al diseño: Facade simplificó el uso del sistema desde la
interfaz, State evitó condicionales extensos para las solicitudes, Factory concentró la
creación de tipos de solicitud y Observer permitió desacoplar notificaciones de cambios.
Como desventaja, aplicar patrones aumenta la cantidad de clases y exige mantener el UML
actualizado, pero el resultado final es más claro, extensible y defendible.

Como equipo, se concluye que el sistema alcanza los objetivos funcionales principales de
la problemática y deja una base ordenada para futuras mejoras, como usar una base de
datos real, agregar autenticación mas robusta y exportar reportes de administracion.
