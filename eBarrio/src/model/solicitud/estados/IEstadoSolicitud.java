package model.solicitud.estados;

import model.solicitud.Solicitud;

public interface IEstadoSolicitud {

    void avanzar(Solicitud solicitud);
    void cancelar(Solicitud solicitud);
    String mostrarNombreEstado();


}