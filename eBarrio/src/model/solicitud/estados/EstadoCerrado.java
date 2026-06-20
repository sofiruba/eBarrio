package model.solicitud.estados;

import model.solicitud.Solicitud;

public class EstadoCerrado implements IEstadoSolicitud {

    @Override
    public void avanzar(Solicitud solicitud) {
        System.out.println("El estado de la solicitud no puede avanzar: ya fue cerrado.");
    }

    @Override
    public void cancelar(Solicitud solicitud) {
        System.out.println("Error: no se puede cancelar una solicitud que ya fue cerrada.");
    }

    @Override
    public String mostrarNombreEstado() {
        return "Cerrado";
    }

}
