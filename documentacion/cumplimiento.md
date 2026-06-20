# Cumplimiento de la consigna final

Fecha de revision: 20/06/2026.

## Resumen

El proyecto eBarrio cumple la base tecnica de la entrega final: esta desarrollado en
Java, aplica orientacion a objetos, organiza el codigo por responsabilidades, implementa
flujos funcionales de la problematica y cuenta con pruebas ejecutables por clases `main`.

La entrega todavia requiere completar artefactos documentales finales: informe final,
evidencia de ejecucion y revision/exportacion de UML. Esos puntos no bloquean el
funcionamiento del sistema, pero si son pedidos explicitamente por la consigna.

## Requisitos de la consigna

| Requisito | Estado | Evidencia / comentario |
| --- | --- | --- |
| Codigo fuente Java completo | Cumple | `eBarrio/src` |
| Proyecto ejecutable | Cumple en IDE | El grupo lo ejecuta desde la carpeta interna `eBarrio/eBarrio` con IntelliJ y JavaFX configurado. |
| Sin errores de compilacion | A verificar en maquina con JDK | En esta terminal no esta disponible `javac`; en el entorno del grupo funciona desde el IDE. |
| Separacion de responsabilidades | Cumple | `app/views` para vistas principales, `model` para dominio, `sistema` como fachada/controlador y `src/data` para JSON. |
| Flujos principales de negocio | Cumple | Residentes, viviendas, visitantes, accesos, reclamos, tareas, incidentes, estados y notificaciones. |
| Casos de prueba / clase Main | Cumple | `Main`, `TestReclamo`, `EscenariosEjecucion`. |
| Historial de commits individuales | Cumple con aclaracion | Hay commits de los tres integrantes; se agrega `commits_individuales.md` para valorar aportes por area, no solo cantidad. |
| Diagramas UML actualizados | Parcial | Hay `.puml` en `documentacion/uml`; falta revisar fidelidad final y exportar si corresponde. |
| Informe final | Pendiente | Debe redactarse como documento final de entrega. |
| Descripcion del problema | Base disponible | README e informes previos. Debe integrarse al informe final. |
| Requerimientos funcionales resueltos | Base disponible | README y escenarios de prueba. Debe integrarse al informe final. |
| Justificacion GoF, SOLID y GRASP | Base disponible | README y codigo. Falta desarrollo completo en informe final. |
| Instrucciones de despliegue | Cumple como base | README explica ejecucion desde IDE y consola. |
| Evidencia de ejecucion | Pendiente | Agregar capturas/logs de JavaFX y escenarios por consola. |
| Conclusiones/autoevaluacion | Pendiente | Debe incluirse en informe final con reflexion del equipo. |

## Flujos implementados

- Login de administrador y residente.
- Alta y edicion de residentes.
- Registro de barrio, viviendas y asociacion de residentes.
- Registro, edicion y eliminacion de visitantes/proveedores autorizados.
- Registro de ingreso y egreso de visitantes.
- Creacion y seguimiento de reclamos.
- Creacion y seguimiento de tareas de mantenimiento.
- Registro de incidentes de seguridad.
- Avance y cancelacion de solicitudes con patron State.
- Notificacion de eventos y cambios relevantes.
- Persistencia simple en JSON para datos y usuarios.

## Separacion de responsabilidades

- `app.views.LoginView`: vista de inicio de sesion.
- `app.views.AdministradorView`: barra superior y menu de administrador.
- `app.views.ResidenteView`: barra superior y menu de residente.
- `app.MainApp`: coordinador de navegacion, formularios y acciones de interfaz.
- `model`: clases de dominio y reglas principales.
- `sistema.SistemaBarrio`: fachada/controlador que centraliza casos de uso.
- `src/data`: almacenamiento JSON simple.

## Pendientes para apuntar a una nota alta

- Crear el informe final real con todos los puntos de la consigna.
- Agregar capturas de ejecucion de JavaFX: login, vista admin, vista residente, alta de
  visitante, registro de acceso y creacion/avance de reclamo.
- Agregar logs de consola de `TestReclamo` y `EscenariosEjecucion`.
- Revisar el diagrama de clases para que refleje exactamente los atributos y metodos
  actuales, incluyendo vistas, usuarios, persistencia y nuevos campos de visitante.
- Exportar los diagramas PlantUML a imagen o PDF si la docente no acepta el fuente `.puml`.
- Confirmar permisos de acceso al repositorio para la docente.

## Nota sobre compilacion y ejecucion

La estructura del repositorio tiene una carpeta raiz con README, documentacion y archivos
de salida, y una carpeta interna `eBarrio/` que contiene el proyecto Java. Por eso la
ejecucion desde IntelliJ funciona abriendo la carpeta interna y corriendo `app.MainApp`.

Desde esta terminal no se pudo ejecutar `javac` porque no esta instalado o no esta en el
PATH. La verificacion final de compilacion debe hacerse en la maquina del grupo o en el
IDE usado para la defensa.
