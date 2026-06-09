package reclamos.reclamo;

public class Reclamo {

    private IEstadoReclamo estado;

    public Reclamo(){
        this.estado = new EstadoPendiente();
    }

    public void setEstado(IEstadoReclamo estado){
        this.estado = estado;
    }
    public void avanzar(){
        estado.avanzar(this);
    }
    public void cancelar(){
        estado.cancelar(this);
    }
    public void mostrarEstado(){
        System.out.println("Estado actual del reclamo: " + estado.mostrarNombreEstado());
    }
}