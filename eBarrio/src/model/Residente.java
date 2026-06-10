package model;

import java.util.ArrayList;
import java.util.List;

public class Residente {
    private int id;
    private String nombre;
    private String apellido;
    private String dni;
    private String email;
    private String telefono;
    private Vivienda vivienda;
    private List<Visitante> visitantes;

    public Residente(int id, String nombre, String apellido, String dni, String email, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.email = email;
        this.telefono = telefono;
        this.visitantes = new ArrayList<>();
    }

    public void registrarVisitante(Visitante visitante) {
        if (visitante != null) {
            visitantes.add(visitante);
        }
    }

    public void crearReclamo(String descripcion) {
        // Método preparado para integrarse con el módulo de solicitudes.
        // La implementación completa se realizará cuando exista la clase Solicitud/Reclamo.
        System.out.println("Reclamo solicitado por " + nombre + ": " + descripcion);
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

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Vivienda getVivienda() {
        return vivienda;
    }

    public void setVivienda(Vivienda vivienda) {
        this.vivienda = vivienda;
    }

    public List<Visitante> getVisitantes() {
        return visitantes;
    }

    public void setVisitantes(List<Visitante> visitantes) {
        this.visitantes = visitantes;
    }

    @Override
    public String toString() {
        return nombre + " " + apellido + " - DNI: " + dni;
    }
}
