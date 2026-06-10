package model;

import java.util.ArrayList;
import java.util.List;

public class Barrio {
    private int id;
    private String nombre;
    private String direccion;
    private List<Vivienda> viviendas;

    public Barrio(int id, String nombre, String direccion) {
        this.id = id;
        this.nombre = nombre;
        this.direccion = direccion;
        this.viviendas = new ArrayList<>();
    }

    public void agregarVivienda(Vivienda vivienda) {
        if (vivienda != null) {
            viviendas.add(vivienda);
        }
    }

    public List<Vivienda> listarViviendas() {
        return viviendas;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public List<Vivienda> getViviendas() {
        return viviendas;
    }

    public void setViviendas(List<Vivienda> viviendas) {
        this.viviendas = viviendas;
    }

    @Override
    public String toString() {
        return "Barrio{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", direccion='" + direccion + '\'' +
                ", viviendas=" + viviendas.size() +
                '}';
    }
}
