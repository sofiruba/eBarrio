# Registro de tareas por integrante

Este registro esta alineado con el informe breve de avance individual presentado en la
Fase 2.

| Integrante | Tareas principales | Evidencia sugerida |
| --- | --- | --- |
| Etchart, Facundo | Modulo de solicitudes y patrones de diseno. Jerarquia `Solicitud`, `Reclamo`, `TareaMantenimiento`, `IncidenteSeguridad` y estructura de State. | Paquetes `model.solicitud`, `model.solicitud.estados` e historial de Git. |
| Cortes, Ignacio | Modulo de accesos, seguridad, administracion y notificaciones. Clases `Acceso`, `Administrador`, `PersonalSeguridad`, `PersonalMantenimiento`, `Notificacion`, colaboracion en `SistemaBarrio` y Observer. | Paquetes `model.accesos`, `model.notificaciones`, `model.solicitud.personal`, `sistema` e historial de Git. |
| Rubachin, Sofia | Modulo de residentes, viviendas y visitantes. Clases `Barrio`, `Vivienda`, `Residente`, `Visitante`, primera interfaz JavaFX y documentacion. | Paquetes `model.barrio`, `model.accesos`, `app` e historial de Git. |

## Artefactos finales

- Informe final: `documentacion/informe_final.md`.
- Evidencia de ejecucion por consola: `documentacion/informe_final.md` y
  `documentacion/escenarios_de_prueba.md`.
- UML final en PlantUML: `documentacion/uml/casos_de_uso.puml` y
  `documentacion/uml/diagrama_clases.puml`.
- Repositorio: https://github.com/sofiruba/eBarrio.

Si la docente solicita imagenes, exportar los archivos PlantUML a PNG o PDF antes de
subir la entrega.

El detalle del historial de commits queda documentado en
`documentacion/commits_individuales.md`. Para evaluar participacion conviene mirar la
responsabilidad por modulo junto con los commits, no solamente la cantidad bruta.
