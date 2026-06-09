package reclamos.estados;

import reclamos.reclamo.Reclamo;

public class EstadoCerrado implements IEstadoReclamo{

    @Override
    public void avanzar(Reclamo reclamo) {
        System.out.println("El estado del reclamo no puede avanzar: ya fue cerrado.");
    }

    @Override
    public void cancelar(Reclamo reclamo) {
        System.out.println("Error: no se puede cancelar un reclamo que ya ha sido cerrado.");
    }

    @Override
    public String mostrarNombreEstado() {
        return "Cerrado";
    }

}