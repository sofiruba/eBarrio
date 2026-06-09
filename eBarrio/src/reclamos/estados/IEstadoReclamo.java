package reclamos.estados;

public interface IEstadoReclamo {

    void avanzar(Reclamo reclamo);
    void cancelar(Reclamo reclamo);
    String mostrarNombreEstado();

}