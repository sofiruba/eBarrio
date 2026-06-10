package reclamos.estados;

import reclamos.reclamo.Reclamo;

public interface IEstadoReclamo {

    void avanzar(Reclamo reclamo);
    void cancelar(Reclamo reclamo);
    String mostrarNombreEstado();

}