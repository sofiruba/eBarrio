package reclamos.estados;

import reclamos.reclamo.Reclamo;

public class EstadoAsignado implements IEstadoReclamo{

    @Override
    public void avanzar(Reclamo reclamo) {
        System.out.println("El reclamo pasa de 'Asignado' a 'En proceso'.");
        reclamo.setEstado(new EstadoEnProceso());
    }

    @Override
    public void cancelar(Reclamo reclamo) {
        System.out.println("El reclamo que se encontraba ya asignado ha sido directamente cerrado.");
        reclamo.setEstado(new EstadoCerrado());
    }

    @Override
    public String mostrarNombreEstado() {
        return "Asignado";
    }
}