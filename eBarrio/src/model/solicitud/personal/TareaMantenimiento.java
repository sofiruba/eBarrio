package model.solicitud.personal;
// Tarea de mantenimiento dentro del barrio.

import model.solicitud.Solicitud;

public class TareaMantenimiento extends Solicitud {

    private String sector;

    public TareaMantenimiento(int id, String nombre, String descripcion, String prioridad, String sector) {
        super(id, nombre, descripcion, prioridad);
        this.sector = sector;
    }

    public String getSector() { return sector; }
    public void setSector(String sector) { this.sector = sector; }

    @Override
    public String toString() {
        return "TareaMantenimiento " + super.toString() + " | Sector: " + sector;
    }
}
