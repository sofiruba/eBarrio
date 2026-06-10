package model.solicitud;

import model.solicitud.Solicitud;
import model.solicitud.reclamo.Reclamo;
import model.solicitud.personal.TareaMantenimiento;
import model.solicitud.personal.IncidenteSeguridad;

public class SolicitudFactory {

    public static Solicitud crearSolicitud(
            String tipo,
            int id,
            String nombre,
            String descripcion,
            String prioridad,
            String datoExtra
    ) {
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de solicitud no puede ser null");
        }

        switch (tipo.toUpperCase()) {
            case "RECLAMO":
                return new Reclamo(id, nombre, descripcion, prioridad, datoExtra);

            case "TAREA_MANTENIMIENTO":
                return new TareaMantenimiento(id, nombre, descripcion, prioridad, datoExtra);

            case "INCIDENTE_SEGURIDAD":
                return new IncidenteSeguridad(id, nombre, descripcion, prioridad, datoExtra);

            default:
                throw new IllegalArgumentException("Tipo de solicitud no válido: " + tipo);
        }
    }
}