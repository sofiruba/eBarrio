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

## Escenario 3: cancelacion de solicitud

1. Crear una solicitud nueva.
2. Cancelarla desde Pendiente o Asignado.

Resultado esperado: la solicitud pasa a Cerrado.

## Escenario 4: control de acceso

1. Registrar un acceso de visitante.
2. Consultar si esta activo.
3. Registrar egreso.

Resultado esperado: el acceso queda con fecha de ingreso y fecha de egreso.
