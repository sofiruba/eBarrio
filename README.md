# eBarrio

Sistema de gestión operativa para un barrio cerrado. El proyecto permite administrar
residentes, viviendas, visitantes, accesos, reclamos, tareas de mantenimiento,
incidentes de seguridad, usuarios y notificaciones.

## Integrantes

- Ignacio Cortés - 1203485
- Facundo Etchart - 1201391
- Sofía Rubachín - 1196599

## Problema abordado

En un barrio cerrado se necesita centralizar información que normalmente queda
dispersa entre administración, seguridad, mantenimiento y residentes. eBarrio resuelve
ese problema con una aplicación Java que registra personas y viviendas, autoriza
visitantes o proveedores, controla ingresos y egresos, y permite hacer seguimiento de
solicitudes operativas hasta su cierre.

## Funcionalidades implementadas

- Login con usuarios administradores y residentes.
- Alta, edición (incluye baja) y consulta de residentes.
- Registro de viviendas y asociación de residentes a una vivienda.
- Autorización de visitantes y proveedores por residente.
- Registro de ingresos y egresos de visitantes.
- Creación de reclamos, tareas de mantenimiento e incidentes de seguridad.
- Seguimiento de solicitudes por estados: Pendiente, Asignado, En proceso, Resuelto y Cerrado.
- Cancelación de solicitudes desde estados intermedios.
- Notificaciones ante eventos relevantes del sistema.
- Persistencia simple en archivos JSON dentro de `data`.
- Interfaz JavaFX con vistas diferenciadas para login, administrador y residente.
- Escenarios de prueba ejecutables desde clases `main`.

## Estructura del proyecto

```text
eBarrio/
  src/
    Main.java                         Demo/consulta por consola
    app/MainApp.java                  Aplicacion JavaFX y coordinacion de flujos
    app/views/                        Vistas principales: login, admin y residente
    data/                             Datos JSON de prueba y persistencia
    images/                           Logo del programa
    model/                            Dominio del sistema
      accesos/                        Visitantes y accesos
      barrio/                         Barrio, vivienda y residente
      notificaciones/                 Notificaciones
      solicitud/                      Solicitudes, estados, factory
    test/                             Pruebas
    sistema/SistemaBarrio.java        Fachada/controlador de casos de uso
documentacion/
  cumplimiento.md                     Revision contra la consigna final
  commits_individuales.md             Resumen de participacion en Git
  escenarios_de_prueba.md             Escenarios manuales y automatizables
  tareas_por_integrante.md            Distribucion de tareas
  informes/                           Informes de fase 1, fase 2 y el informe final
  uml/                                Diagramas PlantUML
```

## Separacion de responsabilidades

- **Vista:** `app.MainApp` coordina la pantalla activa y las clases `app.views.LoginView`,
  `app.views.AdministradorView` y `app.views.ResidenteView` construyen las vistas
  principales de la interfaz.
- **Dominio:** los paquetes `model.barrio`, `model.accesos`, `model.solicitud`,
  `model.notificaciones` y `model.solicitud.personal` contienen las entidades y reglas
  centrales.
- **Controlador/fachada:** `sistema.SistemaBarrio` concentra los casos de uso que usa la
  interfaz y las pruebas.
- **Datos:** `src/data/datos.json` y `src/data/usuarios.json` conservan datos de prueba y
  cambios realizados desde la aplicación.

## Patrones de diseno

- **Factory Method / Simple Factory:** `SolicitudFactory` centraliza la creación de
  `Reclamo`, `TareaMantenimiento` e `IncidenteSeguridad`.
- **Facade:** `SistemaBarrio` ofrece una interfaz unificada para registrar personas,
  accesos, solicitudes, usuarios, notificaciones y persistencia.
- **State:** las clases de `model.solicitud.estados` encapsulan las transiciones de estado
  de una solicitud.
- **Observer:** `Solicitud` notifica a objetos que implementan `IObservador`, como
  `Administrador`.

## Principios SOLID y GRASP

- **SRP:** cada clase mantiene una responsabilidad clara: `Acceso` gestiona ingreso/egreso,
  `Notificacion` representa mensajes, los estados gestionan transiciones y `SistemaBarrio`
  coordina casos de uso.
- **OCP/LSP:** nuevas solicitudes pueden agregarse extendiendo `Solicitud` y usando la
  fábrica sin romper el uso polimórfico existente.
- **ISP/DIP:** `Solicitud` depende de `IObservador` en vez de una clase concreta.
- **GRASP Creator:** `SolicitudFactory` crea solicitudes y `SistemaBarrio` crea entidades
  del flujo.
- **GRASP Controller:** `SistemaBarrio` recibe las operaciones principales del sistema.
- **GRASP Information Expert:** clases como `Acceso`, `Vivienda`, `Residente` y los estados
  resuelven comportamientos asociados a sus propios datos.
- **GRASP Low Coupling / High Cohesion:** paquetes por responsabilidad y acceso a los casos
  de uso a través de la fachada.

## Repositorio

https://github.com/sofiruba/eBarrio

## Cómo ejecutar

La forma recomendada es abrir el proyecto en IntelliJ IDEA y ejecutar
`app.MainApp` con JavaFX configurado.

Tambien se puede compilar y ejecutar por consola desde la raíz del repositorio:

```bash
javac --module-path eBarrio/libs/javafx-sdk-17.0.19/lib --add-modules javafx.controls -encoding UTF-8 -d out $(find eBarrio/src -name '*.java')
java --module-path eBarrio/libs/javafx-sdk-17.0.19/lib --add-modules javafx.controls -cp out app.MainApp
```

Para escenarios de prueba por consola:

```bash
java -cp out Main
java -cp out model.solicitud.test.TestReclamo
java -cp out model.solicitud.test.EscenariosEjecucion
```

## Usuarios de prueba

Los usuarios están en `eBarrio/src/data/usuarios.json`. Ejemplos:

- Administrador: `admin@ebarrio.com` / `admin123`
- Residente: `sofia@email.com` / `sofia123`
- Residente: `martin@email.com` / `martin123`
- Residente: `ana@email.com` / `ana123`
- Residente: `facu@gmail.com` / `34567891`

## Estado para entrega

El código implementa los flujos principales y alternativos requeridos. La documentación
final se encuentra en `documentacion/ifnromes/Informe final eBarrio.pdf`, el checklist de consigna en
`documentacion/cumplimiento.md`, los escenarios en `documentacion/escenarios_de_prueba.md`
y los diagramas UML en `documentacion/uml`.
