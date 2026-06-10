package model.solicitud.estados;

import model.solicitud.Solicitud;

public class EstadoAsignado implements IEstadoSolicitud {

    @Override
    public void avanzar(Solicitud solicitud) {
        System.out.println("El reclamo pasa de 'Asignado' a 'En proceso'.");
        solicitud.setEstado(new EstadoEnProceso());
    }

    @Override
    public void cancelar(Solicitud solicitud) {
        System.out.println("El reclamo que se encontraba ya asignado ha sido directamente cerrado.");
        solicitud.setEstado(new EstadoCerrado());
    }

    @Override
    public String mostrarNombreEstado() {
        return "Asignado";
    }
}