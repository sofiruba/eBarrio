# Escenarios de prueba manuales

## Escenario 1: demo general por consola

1. Ejecutar `Main`.
2. Verificar que se cree el barrio con viviendas y residentes.
3. Verificar que se registre un administrador como observador.
4. Verificar que se creen un reclamo, una tarea de mantenimiento y un incidente.
5. Verificar que se listen las solicitudes.
6. Verificar que el reclamo avance hasta Cerrado.
7. Verificar que una tarea pueda cancelarse desde Asignado.
8. Verificar que se registre ingreso y egreso de visitantes.

Resultado esperado: la consola muestra los cambios de estado, las notificaciones y los
listados finales sin errores de ejecucion.

Comando sugerido:

```powershell
java -cp out Main
```

## Escenario 2: ciclo de vida de reclamo

1. Ejecutar `model.solicitud.test.TestReclamo`.
2. Crear un reclamo en estado Pendiente.
3. Avanzar el estado varias veces.

Resultado esperado:

- Pendiente -> Asignado.
- Asignado -> En proceso.
- En proceso -> Resuelto.
- Resuelto -> Cerrado.
- Cerrado no permite avanzar.

Comando sugerido:

```powershell
java -cp out model.solicitud.test.TestReclamo
```

## Escenario 3: cancelacion de solicitud

1. Crear una solicitud nueva.
2. Cancelarla desde Pendiente o Asignado.

Resultado esperado: la solicitud pasa a Cerrado.

## Escenario 4: control de acceso

1. Registrar un acceso de visitante.
2. Consultar si esta activo.
3. Registrar egreso.

Resultado esperado: el acceso queda con fecha de ingreso y fecha de egreso.

## Escenario 5: prueba integral automatizable

1. Ejecutar `model.solicitud.test.EscenariosEjecucion`.
2. Validar altas de barrio, vivienda, residente y visitante.
3. Validar ingreso y egreso.
4. Validar ciclo de vida de reclamo.
5. Validar cancelacion de tarea.
6. Validar busqueda de solicitud por ID.

Resultado esperado: la consola muestra `Todos los escenarios de ejecucion finalizaron correctamente.`

Comando sugerido:

```powershell
java -cp out model.solicitud.test.EscenariosEjecucion
```

## Escenario 6: interfaz JavaFX

1. Ejecutar `app.MainApp` con JavaFX configurado.
2. Verificar el dashboard inicial con metricas y tablas.
3. Ir a Residentes y crear un residente.
4. Seleccionar un residente y registrar un visitante.
5. Seleccionar un visitante y registrar un acceso.
6. Seleccionar un acceso y registrar egreso.
7. Crear un reclamo, una tarea y un incidente.
8. Avanzar o cancelar una solicitud seleccionada.

Resultado esperado: las tablas y metricas se actualizan en memoria sin reiniciar la app.
