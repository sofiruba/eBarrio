package solicitud.estados;

import solicitud.Solicitud;

public class EstadoCerrado implements IEstadoSolicitud {

    @Override
    public void avanzar(Solicitud solicitud) {
        System.out.println("El estado de la solicitud no puede avanzar: ya fue cerrado.");
    }

    @Override
    public void cancelar(Solicitud solicitud) {
        System.out.println("Error: no se puede cancelar un reclamo que ya ha sido cerrado.");
    }

    @Override
    public String mostrarNombreEstado() {
        return "Cerrado";
    }

}