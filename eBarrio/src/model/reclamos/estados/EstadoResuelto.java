package reclamos.estados;

import reclamos.reclamo.Reclamo;

public class EstadoResuelto implements IEstadoReclamo{

    @Override
    public void avanzar(Reclamo reclamo) {
        System.out.println("El reclamo pasa de 'Resuelto' a 'Cerrado'.");
        reclamo.setEstado(new EstadoCerrado());
    }

    @Override
    public void cancelar(Reclamo reclamo) {
        System.out.println("Error: no se puede cancelar un reclamo que ya ha sido marcado como resuelto.");
    }

    @Override
    public String mostrarNombreEstado() {
        return "Resuelto";
    }
}