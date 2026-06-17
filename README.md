# eBarrio

Sistema de gestion operativa para un barrio cerrado. El proyecto centraliza residentes,
viviendas, visitantes, accesos, reclamos, tareas de mantenimiento, incidentes de
seguridad y notificaciones en memoria.

## Integrantes

- Facundo Etchart
- Sofia Rubachin
- Ignacio Cortes

Legajos informados en las entregas previas:

- Cortes, Ignacio - 1203485
- Etchart, Facundo - 1201391
- Rubachin, Sofia - 1196599

## Descripcion breve

eBarrio permite administrar informacion basica del barrio y ejecutar los casos de uso
principales de la problematica: registrar residentes y viviendas, autorizar visitantes,
registrar ingresos y egresos, crear reclamos o tareas, avanzar su estado y notificar a
los responsables cuando una solicitud cambia.

La implementacion actual contiene una demostracion por consola. La interfaz JavaFX esta
en desarrollo y se conserva como primera version visual del panel.

## Estructura del proyecto

```text
eBarrio/
  src/
    Main.java                         Demo funcional por consola
    app/MainApp.java                  Prototipo JavaFX
    model/                            Clases de dominio
    sistema/SistemaBarrio.java        Fachada / servicio principal
documentacion/
  cumplimiento.md                     Revision contra la consigna
  tareas_por_integrante.md            Distribucion inicial de tareas
  escenarios_de_prueba.md             Casos de prueba manuales
  informes/                           Informes de fase y final
  uml/                                Diagramas PlantUML
  entregas_previas/                   PDFs originales de Fase 1 y Fase 2
  commits_individuales.md             Resumen de participacion en Git
```

## Como ejecutar

Desde la raiz del repositorio, compilar y ejecutar la demo por consola:

```bash
javac -encoding UTF-8 -d out $(find eBarrio/src -name "*.java" ! -path "*app*")
java -cp out Main
```

En Windows PowerShell:

```powershell
javac -encoding UTF-8 -d out (Get-ChildItem -Recurse eBarrio\src -Filter *.java | Where-Object { $_.FullName -notlike "*\app\MainApp.java" } | ForEach-Object { $_.FullName })
java -cp out Main
```

Para probar el flujo especifico de reclamos:

```powershell
java -cp out model.solicitud.test.TestReclamo
```

Nota: para ejecutar `app.MainApp` se requiere tener JavaFX configurado en el IDE o en el
classpath/module-path.

## Casos de uso implementados

- Crear la estructura inicial del barrio con viviendas y residentes.
- Registrar visitantes asociados a residentes.
- Registrar ingreso y egreso de visitantes.
- Crear reclamos, tareas de mantenimiento e incidentes de seguridad.
- Avanzar solicitudes por el ciclo Pendiente -> Asignado -> En proceso -> Resuelto -> Cerrado.
- Cancelar solicitudes desde estados intermedios.
- Notificar a observadores cuando cambia el estado de una solicitud.

## Patrones de diseno aplicados

- Factory Method / Simple Factory: `SolicitudFactory` centraliza la creacion de
  `Reclamo`, `TareaMantenimiento` e `IncidenteSeguridad`.
- Facade: `SistemaBarrio` ofrece una interfaz simple para crear solicitudes, registrar
  accesos, emitir notificaciones y consultar informacion.
- State: las clases de `model.solicitud.estados` encapsulan las transiciones de una
  solicitud.
- Observer: `Solicitud` notifica a objetos que implementan `IObservador`, por ejemplo
  `Administrador`.

## Principios SOLID aplicados

- SRP: las clases tienen responsabilidades separadas: `Acceso` gestiona ingresos y
  egresos, `Notificacion` representa mensajes, `SistemaBarrio` coordina operaciones.
- OCP: se pueden agregar nuevos tipos de solicitud o nuevos estados extendiendo clases
  existentes sin modificar todo el flujo de negocio.
- LSP: `Reclamo`, `TareaMantenimiento` e `IncidenteSeguridad` pueden usarse como
  `Solicitud`.
- ISP: `IObservador` define una interfaz pequena y especifica para recibir cambios.
- DIP: `Solicitud` depende de la abstraccion `IObservador` y no de una clase concreta.

## Patrones GRASP aplicados

- Creator: `SolicitudFactory` crea solicitudes y `SistemaBarrio` administra la creacion
  dentro del flujo de la aplicacion.
- Controller: `SistemaBarrio` recibe las operaciones principales del sistema y coordina
  el modelo.
- Information Expert: cada clase conserva el comportamiento que corresponde a sus datos,
  por ejemplo `Acceso.estaActivo()` y los estados de solicitud.
- Low Coupling: el uso de interfaces y fachada reduce dependencias directas entre capas.
- High Cohesion: las clases se agrupan por paquetes y responsabilidades del dominio.
- Polymorphism: las subclases de `Solicitud` y los estados implementan comportamiento
  intercambiable.

## Documentacion

La carpeta `documentacion/` contiene:

- Revision de cumplimiento contra la consigna.
- PDFs originales ya entregados de Fase 1 y Fase 2.
- Diagramas UML en PlantUML.
- Informes base de fase 1, fase 2 y fase final.
- Escenarios de prueba manuales.
- Distribucion de tareas por integrante.
- Resumen de commits individuales.

## Estado actual

Cumple la base de implementacion, patrones, paquetes, demo funcional y documentacion
inicial requerida. Queda pendiente completar o actualizar con el grupo:

- Capturas reales de ejecucion para el informe final.
- Diagramas exportados a imagen/PDF si la docente no acepta PlantUML fuente.
- Interfaz JavaFX terminada y configuracion de ejecucion documentada.
