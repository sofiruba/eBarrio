# Cumplimiento de la consigna final

Fecha de revision: 20/06/2026.

Repositorio: https://github.com/sofiruba/eBarrio

## Checklist de entrega

| Requisito | Estado | Evidencia                                                                                              |
| --- | --- |--------------------------------------------------------------------------------------------------------|
| Codigo fuente Java completo | Cumple | `eBarrio/src`                                                                                          |
| Proyecto limpio, ejecutable y sin errores de compilacion | Cumple | Compilado con `javac` y JavaFX SDK 17 sin errores.                                                     |
| Separacion de responsabilidades | Cumple | `app`, `sistema`, `model`, `data`, `documentacion`.                                                    |
| Flujos de negocio requeridos | Cumple | Login, residentes, viviendas, visitantes/proveedores, accesos, solicitudes, notificaciones y persistencia. |
| Casos de prueba | Cumple | `Main`, `TestReclamo`, `EscenariosEjecucion`.                                                          |
| Historial de versiones | Cumple | Git + resumen en `documentacion/commits_individuales.md`.                                              |
| Diagramas UML actualizados | Cumple | `documentacion/uml/casos_de_uso.puml` y `documentacion/uml/diagrama_clases.puml`.                      |
| Informe final | Cumple | `documentacion/informes/informe final eBarrio.pdf`.                                                    |
| Instrucciones de despliegue | Cumple | README e informe final.                                                                                |
| Evidencia de ejecucion | Cumple | Logs documentados en `informe final eBarrio.pdf` y `documentacion/escenarios_de_prueba.md`.            |

## Flujos implementados

- Login de administrador y residente con usuarios persistidos.
- Alta y edicion de residentes.
- Creacion de vivienda y asociacion con residente.
- Creacion automatica de cuenta para residentes nuevos.
- Registro, edicion, eliminacion y filtrado de visitantes/proveedores.
- Registro de ingreso y egreso con fecha y hora.
- Creacion y seguimiento de reclamos.
- Creacion y seguimiento de tareas de mantenimiento.
- Registro de incidentes de seguridad.
- Avance y cancelacion de solicitudes con patron State.
- Notificaciones por eventos relevantes.
- Persistencia JSON de datos generales y usuarios.

## Separacion de responsabilidades

- `app.MainApp`: interfaz JavaFX y coordinacion de pantallas.
- `app.views`: estructuras de vista para login, administrador y residente.
- `sistema.SistemaBarrio`: fachada/controlador de casos de uso y persistencia.
- `model.barrio`: barrio, vivienda y residente.
- `model.accesos`: visitante/proveedor y acceso.
- `model.solicitud`: solicitudes, factory, observer y estados.
- `model.notificaciones`: notificaciones.
- `data`: archivos JSON que funcionan como base de datos simple.

## Verificacion tecnica realizada

Compilacion:

```bash
javac --module-path eBarrio/libs/javafx-sdk-17.0.19/lib --add-modules javafx.controls -Xlint:unchecked -encoding UTF-8 -d /tmp/ebarrio-docs $(find eBarrio/src -name '*.java')
```

Resultado: sin errores.

Pruebas ejecutadas:

```bash
java -cp /tmp/ebarrio-docs Main
java -cp /tmp/ebarrio-docs model.solicitud.test.TestReclamo
java -cp /tmp/ebarrio-docs model.solicitud.test.EscenariosEjecucion
```

Resultado principal: `Todos los escenarios de ejecucion finalizaron correctamente.`
