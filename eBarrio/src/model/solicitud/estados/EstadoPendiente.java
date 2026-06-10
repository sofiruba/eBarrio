package model.solicitud.estados;

import model.solicitud.Solicitud;

public class EstadoPendiente implements IEstadoSolicitud {

    @Override
    public void avanzar(Solicitud solicitud) {
        System.out.println("La solicitud pasa de 'Pendiente' a 'Asignado'.");
        solicitud.setEstado(new EstadoAsignado());
    }

    @Override
    public void cancelar(Solicitud solicitud) {
        System.out.println("La solicitud que se encontraba pendiente de asignación ha sido directamente cerrada.");
        solicitud.setEstado(new EstadoCerrado());
    }

    @Override
    public String mostrarNombreEstado() {
        return "Pendiente";
    }
}