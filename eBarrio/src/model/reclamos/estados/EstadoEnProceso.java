package reclamos.estados;

import reclamos.reclamo.Reclamo;

public class EstadoEnProceso implements IEstadoReclamo {

    @Override
    public void avanzar(Reclamo reclamo) {
        System.out.println("El reclamo pasa de 'En proceso' a 'Resuelto'.");
        reclamo.setEstado(new EstadoResuelto());
    }

    @Override
    public void cancelar(Reclamo reclamo) {
        System.out.println("El reclamo que se encontraba en proceso de resolución ha sido directamente cerrado.");
        reclamo.setEstado(new EstadoCerrado());
    }

    @Override
    public String mostrarNombreEstado() {
        return "En proceso";
    }

}