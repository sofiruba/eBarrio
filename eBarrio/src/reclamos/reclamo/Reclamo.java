package reclamos.reclamo;

import reclamos.estados.IEstadoReclamo;
import reclamos.estados.EstadoPendiente;

public class Reclamo {

    //ATRIBUTOS
    private String id;
    private String nombre;
    private String descripcion;
    private IEstadoReclamo estado;

    //CONSTRUCTOR
    public Reclamo(String id, String nombre, String descripcion){
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estado = new EstadoPendiente();
    }

    //METODOS
    public void avanzar(){
        estado.avanzar(this);
    }
    public void cancelar(){
        estado.cancelar(this);
    }
    public void mostrarEstado(){
        System.out.println("Estado actual del reclamo: " + estado.mostrarNombreEstado());
    }

    //GETTERS Y SETTERS
    public String getId(){
        return id;
    }
    public String getNombre(){
        return nombre;
    }
    public String getDescripcion(){
        return descripcion;
    }
    public void setNombre(String nombre){
        this.nombre = nombre;
    }
    public void setDescripcion(String descripcion){
        this.descripcion = descripcion;
    }
    public IEstadoReclamo getEstado(){
        return estado;
    }
    public void setEstado(IEstadoReclamo estado){
        this.estado = estado;
    }

}