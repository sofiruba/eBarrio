# Escenarios de prueba

## Preparacion

Compilar desde la raiz del repositorio:

```bash
javac --module-path eBarrio/libs/javafx-sdk-17.0.19/lib --add-modules javafx.controls -encoding UTF-8 -d out $(find eBarrio/src -name '*.java')
```

Ejecutar interfaz:

```bash
java --module-path eBarrio/libs/javafx-sdk-17.0.19/lib --add-modules javafx.controls -cp out app.MainApp
```

Usuarios de prueba:

- Administrador: `admin@ebarrio.com` / `admin123`
- Residente: `sofia@email.com` / `sofia123`
- Residente: `martin@email.com` / `martin123`
- Residente: `ana@email.com` / `ana123`
- Residente: `facu@gmail.com` / `34567891`

## Escenario 1: login por rol

Pasos:

1. Ejecutar `app.MainApp`.
2. Iniciar sesion con `admin@ebarrio.com` / `admin123`.
3. Verificar que se muestra la vista de administrador.
4. Cerrar y volver a iniciar sesion con `sofia@email.com` / `sofia123`.
5. Verificar que se muestra la vista de residente.

Resultado esperado: cada usuario ingresa a la vista correspondiente. Si la clave es
incorrecta, la app muestra un mensaje amigable: `Ingresa bien el email y la clave para
continuar.`

## Escenario 2: alta de residente con cuenta automatica

Pasos:

1. Iniciar sesion como administrador.
2. Entrar en Residentes.
3. Crear un residente completando nombre, apellido, DNI, email, telefono, lote y
   direccion.
4. Confirmar el alta.
5. Revisar que el residente aparece en la tabla.
6. Cerrar la app y volver a abrirla.
7. Iniciar sesion con el email del residente y clave inicial igual al DNI.

Resultado esperado: el residente queda guardado en `datos.json` y su cuenta queda
guardada en `usuarios.json`.

## Escenario 3: modificar residente

Pasos:

1. Iniciar sesion como administrador.
2. Seleccionar un residente.
3. Presionar Editar.
4. Cambiar telefono, email, lote o direccion.
5. Guardar.
6. Cerrar y reabrir la aplicacion.

Resultado esperado: los cambios se mantienen y, si cambia el email, tambien se actualiza
la cuenta de inicio de sesion asociada.

## Escenario 4: visitantes y proveedores

Pasos:

1. Iniciar sesion como administrador o residente.
2. Entrar en Accesos y visitantes o Mis visitantes.
3. Crear una persona autorizada.
4. Elegir tipo `Visitante` o `Proveedor`.
5. Elegir frecuencia `Unica vez`, `Semanal` o `Mensual`.
6. Dejar patente vacia para validar que es opcional.
7. Filtrar por Todos, Visitantes y Proveedores.

Resultado esperado: la persona aparece en la tabla correcta, se filtra por tipo y queda
persistida.

## Escenario 5: ingreso y egreso

Pasos:

1. Ir a Accesos y visitantes.
2. Seleccionar o registrar un visitante/proveedor.
3. Registrar ingreso.
4. Verificar fecha y hora de ingreso.
5. Seleccionar el acceso activo.
6. Registrar egreso.
7. Verificar fecha y hora de egreso.

Resultado esperado: no se permite duplicar un ingreso activo para el mismo DNI y el
acceso pasa de Activo a Finalizado.

## Escenario 6: ciclo de vida de reclamo

Comando:

```bash
java -cp out model.solicitud.test.TestReclamo
```

Resultado esperado:

- Pendiente -> Asignado.
- Asignado -> En proceso.
- En proceso -> Resuelto.
- Resuelto -> Cerrado.
- Cerrado no permite avanzar.
- Una solicitud pendiente puede cancelarse y pasar a Cerrado.

Evidencia obtenida:

```text
La solicitud pasa de 'Pendiente' a 'Asignado'.
La solicitud pasa de 'Asignado' a 'En proceso'.
La solicitud pasa de 'En proceso' a 'Resuelto'.
La solicitud pasa de 'Resuelto' a 'Cerrado'.
El estado de la solicitud no puede avanzar: ya fue cerrado.
La solicitud que se encontraba pendiente de asignación ha sido directamente cerrada.
La solicitud ya esta cerrada y no se puede cancelar.
```

## Escenario 7: prueba integral automatizada

Comando:

```bash
java -cp out model.solicitud.test.EscenariosEjecucion
```

Valida:

- Alta de barrio, vivienda, residente y visitante.
- Registro de ingreso y egreso.
- Creacion y avance completo de reclamo.
- Creacion y cancelacion de tarea de mantenimiento.
- Busqueda de solicitud por ID.

Evidencia obtenida:

```text
Ingreso registrado para Camila Ruiz
Egreso registrado para Camila Ruiz
Reclamo creado: Reclamo [1] Luminaria rota
Tarea de mantenimiento creada: TareaMantenimiento [2] Cortar pasto
Todos los escenarios de ejecucion finalizaron correctamente.
```

## Escenario 8: validaciones de datos

Pasos:

1. Intentar crear residente con email sin `@`.
2. Intentar crear residente con telefono demasiado corto.
3. Intentar crear visitante con DNI no numerico.
4. Intentar crear visitante con patente demasiado larga.

Resultado esperado: la aplicacion no guarda el registro y muestra mensajes claros para
corregir los datos.
