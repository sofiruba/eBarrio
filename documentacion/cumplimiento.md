# Cumplimiento de la consigna

Fecha de revision: 17/06/2026.

## Resumen

El proyecto cumple con la base tecnica pedida para la fase de implementacion: esta en
Java, usa orientacion a objetos, organiza el codigo en paquetes, tiene clases de dominio,
un servicio/fachada, una demo ejecutable por consola y aplica patrones de diseno.

El repositorio ahora conserva tambien las entregas previas de Fase 1 y Fase 2 en
`documentacion/entregas_previas`, por lo que la documentacion nueva queda alineada con
lo ya presentado por el grupo.

## Requisitos generales

| Requisito | Estado | Evidencia |
| --- | --- | --- |
| Implementado en Java | Cumple | `eBarrio/src` |
| Programacion orientada a objetos | Cumple | Entidades de dominio, herencia en `Solicitud` |
| Estructura clara de paquetes | Cumple | `model`, `sistema`, `app` |
| Clases de dominio | Cumple | `Barrio`, `Vivienda`, `Residente`, `Acceso`, `Solicitud` |
| Servicios/controladores | Cumple | `SistemaBarrio` |
| Clases de prueba o ejecucion | Cumple | `Main`, `TestReclamo` |
| Patron creacional | Cumple | `SolicitudFactory` |
| Patron estructural | Cumple | `SistemaBarrio` como Facade |
| Patrones de comportamiento | Cumple | State y Observer |
| 3 principios SOLID | Cumple | SRP, OCP, LSP, ISP, DIP |
| 3 patrones GRASP | Cumple | Creator, Controller, Information Expert, Low Coupling, High Cohesion |
| UML coherente | Cumple como fuente | `documentacion/uml/*.puml` |
| Justificacion de decisiones | Cumple como base | Informes y README |
| Demo funcional | Cumple | `Main.java` |
| Entregas previas | Cumple | `documentacion/entregas_previas` |
| Interfaz JavaFX inicial | Cumple | `app/MainApp.java` |

## Pendientes recomendados

- Exportar los diagramas PlantUML a imagen o PDF si la entrega lo exige.
- Agregar capturas de pantalla o salida de consola al informe final.
- Completar reflexiones individuales reales de cada integrante.
- Verificar la compilacion en una maquina con JDK instalado.
- Ajustar configuracion JavaFX segun la maquina usada para la defensa.

## Observacion sobre verificacion local

En esta terminal no estaba disponible `javac`, por lo que no se pudo ejecutar una
compilacion local desde consola. La estructura del codigo fue revisada manualmente y el
README incluye los comandos esperados para compilar con un JDK instalado.
