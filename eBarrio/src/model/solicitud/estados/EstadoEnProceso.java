package model.solicitud.estados;

import model.solicitud.Solicitud;

public class EstadoEnProceso implements IEstadoSolicitud {

    @Override
    public void avanzar(Solicitud solicitud) {
        System.out.println("La solicitud pasa de 'En proceso' a 'Resuelto'.");
        solicitud.setEstado(new EstadoResuelto());
    }

    @Override
    public void cancelar(Solicitud solicitud) {
        System.out.println("La solicitud que se encontraba en proceso de resolución ha sido directamente cerrada.");
        solicitud.setEstado(new EstadoCerrado());
    }

    @Override
    public String mostrarNombreEstado() {
        return "En proceso";
    }

}