package solicitud;

import solicitud.estados.EstadoPendiente;
import solicitud.estados.IEstadoSolicitud;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public abstract class Solicitud {

    public int id;
    private String nombre;
    private String descripcion;
    private String prioridad;
    private LocalDate fechaCreacion;
    private IEstadoSolicitud estado;
    private List<IObservador> observadores;

    public Solicitud(int id, String nombre, String descripcion, String prioridad) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.prioridad = prioridad;
        this.fechaCreacion = LocalDate.now();
        this.estado = new EstadoPendiente();
        this.observadores = new ArrayList<>();
    }

    // ── Lógica de negocio 

    public void avanzar() {
        estado.avanzar(this);
        notificarObservadores();
    }

    public void cancelar() {
        estado.cancelar(this);
        notificarObservadores();
    }

    public void mostrarEstado() {
        System.out.println("Estado de solicitud [" + id + "]: " + estado.mostrarNombreEstado());
    }

    // ── Observer 

    public void agregarObservador(IObservador observador) {
        observadores.add(observador);
    }

    public void quitarObservador(IObservador observador) {
        observadores.remove(observador);
    }

    public void notificarObservadores() {
        for (IObservador obs : observadores) {
            obs.actualizar(this);
        }
    }

    // ── Getters y setters

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public String getPrioridad() { return prioridad; }
    public LocalDate getFechaCreacion() { return fechaCreacion; }
    public IEstadoSolicitud getEstado() { return estado; }

    public void setEstado(IEstadoSolicitud estado) { this.estado = estado; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setPrioridad(String prioridad) { this.prioridad = prioridad; }

    @Override
    public String toString() {
        return "[" + id + "] " + descripcion +
               " | Prioridad: " + prioridad +
               " | Estado: " + estado.mostrarNombreEstado() +
               " | Fecha: " + fechaCreacion;
    }


}
