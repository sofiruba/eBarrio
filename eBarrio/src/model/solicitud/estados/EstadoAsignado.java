package model.solicitud.estados;

import model.solicitud.Solicitud;

public class EstadoAsignado implements IEstadoSolicitud {

    @Override
    public void avanzar(Solicitud solicitud) {
        System.out.println("La solicitud pasa de 'Asignado' a 'En proceso'.");
        solicitud.setEstado(new EstadoEnProceso());
    }

    @Override
    public void cancelar(Solicitud solicitud) {
        System.out.println("La solicitud que se encontraba ya asignada ha sido directamente cerrada.");
        solicitud.setEstado(new EstadoCerrado());
    }

    @Override
    public String mostrarNombreEstado() {
        return "Asignado";
    }
}
