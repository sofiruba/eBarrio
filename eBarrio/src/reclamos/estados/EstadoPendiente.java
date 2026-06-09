package reclamos.estados;

import reclamos.reclamo.Reclamo;

public class EstadoPendiente implements IEstadoReclamo{

    @Override
    public void avanzar(Reclamo reclamo) {
        System.out.println("El reclamo pasa de 'Pendiente' a 'Asignado'.");
        reclamo.setEstado(new EstadoAsignado());
    }

    @Override
    public void cancelar(Reclamo reclamo) {
        System.out.println("El reclamo que se encontraba pendiente de asignación ha sido directamente cerrado.");
        reclamo.setEstado(new EstadoCerrado());
    }

    @Override
    public String mostrarNombreEstado() {
        return "Pendiente";
    }
}