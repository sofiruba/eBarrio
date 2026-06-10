package model.solicitud.estados;

import model.solicitud.Solicitud;

public class EstadoResuelto implements IEstadoSolicitud {

    @Override
    public void avanzar(Solicitud solicitud) {
        System.out.println("El reclamo pasa de 'Resuelto' a 'Cerrado'.");
        solicitud.setEstado(new EstadoCerrado());
    }

    @Override
    public void cancelar(Solicitud solicitud) {
        System.out.println("Error: no se puede cancelar un reclamo que ya ha sido marcado como resuelto.");
    }

    @Override
    public String mostrarNombreEstado() {
        return "Resuelto";
    }
}