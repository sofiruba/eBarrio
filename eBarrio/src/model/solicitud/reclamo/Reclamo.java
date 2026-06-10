package solicitud.reclamo;

import solicitud.Solicitud;

public class Reclamo extends Solicitud {

    private String tipoReclamo;

    public Reclamo(int id, String nombre, String descripcion, String prioridad, String tipoReclamo) {
        super(id, nombre, descripcion, prioridad);
        this.tipoReclamo = tipoReclamo;
    }

    public String getTipoReclamo() { return tipoReclamo; }
    public void setTipoReclamo(String tipoReclamo) { this.tipoReclamo = tipoReclamo; }

    @Override
    public String toString() {
        return "Reclamo " + super.toString() + " | Tipo: " + tipoReclamo;
    }
}
