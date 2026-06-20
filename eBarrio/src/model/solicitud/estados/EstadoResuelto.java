package model.solicitud.estados;

import model.solicitud.Solicitud;

public class EstadoResuelto implements IEstadoSolicitud {

    @Override
    public void avanzar(Solicitud solicitud) {
        System.out.println("La solicitud pasa de 'Resuelto' a 'Cerrado'.");
        solicitud.setEstado(new EstadoCerrado());
    }

    @Override
    public void cancelar(Solicitud solicitud) {
        System.out.println("La solicitud ya fue resuelta y no se puede cancelar.");
    }

    @Override
    public String mostrarNombreEstado() {
        return "Resuelto";
    }
}
