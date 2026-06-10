package model.solicitud.personal;

import model.accesos.Acceso;

public class PersonalSeguridad {

    private int id;
    private String nombre;
    private String turno;

    public PersonalSeguridad(int id, String nombre, String turno) {
        this.id = id;
        this.nombre = nombre;
        this.turno = turno;
    }

    public void controlarAcceso(Acceso acceso) {
        System.out.println("PersonalSeguridad " + nombre +
            " controlando acceso [" + acceso.getId() + "]");
        acceso.registrarIngreso();
    }

    public void registrarEgreso(Acceso acceso) {
        System.out.println("PersonalSeguridad " + nombre +
            " registrando egreso del acceso [" + acceso.getId() + "]");
        acceso.registrarEgreso();
    }

    public void reportarIncidente(IncidenteSeguridad incidente) {
        System.out.println("PersonalSeguridad " + nombre +
            " reportó incidente: " + incidente.getDescripcion());
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getTurno() { return turno; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setTurno(String turno) { this.turno = turno; }

    @Override
    public String toString() {
        return "PersonalSeguridad [" + id + "] " + nombre + " | Turno: " + turno;
    }
}
