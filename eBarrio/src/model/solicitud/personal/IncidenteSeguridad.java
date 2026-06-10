package model.solicitud.personal;

// Incidente de seguridad registrado en el barrio.

import model.solicitud.Solicitud;

public class IncidenteSeguridad extends Solicitud {

    private String nivelRiesgo;

    public IncidenteSeguridad(int id, String nombre, String descripcion, String prioridad, String nivelRiesgo) {
        super(id, nombre, descripcion, prioridad);
        this.nivelRiesgo = nivelRiesgo;
    }

    public String getNivelRiesgo() { return nivelRiesgo; }
    public void setNivelRiesgo(String nivelRiesgo) { this.nivelRiesgo = nivelRiesgo; }

    @Override
    public String toString() {
        return "IncidenteSeguridad " + super.toString() + " | Nivel de riesgo: " + nivelRiesgo;
    }
}
