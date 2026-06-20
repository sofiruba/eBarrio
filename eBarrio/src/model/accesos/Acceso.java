package model.accesos;

import java.time.LocalDateTime;

// Registra el ingreso y egreso de un visitante al barrio.

public class Acceso {

    private int id;
    private String nombreVisitante;
    private String dniVisitante;
    private LocalDateTime fechaIngreso;
    private LocalDateTime fechaEgreso;

    public Acceso(int id, String nombreVisitante, String dniVisitante) {
        this.id = id;
        this.nombreVisitante = nombreVisitante;
        this.dniVisitante = dniVisitante;
    }

    public void registrarIngreso() {
        this.fechaIngreso = LocalDateTime.now();
        System.out.println("Ingreso registrado para " + nombreVisitante +
            " a las " + fechaIngreso);
    }

    public void registrarEgreso() {
        if (fechaIngreso == null) {
            System.out.println("Primero registra el ingreso y luego el egreso.");
            return;
        }
        this.fechaEgreso = LocalDateTime.now();
        System.out.println("Egreso registrado para " + nombreVisitante +
            " a las " + fechaEgreso);
    }

    public boolean estaActivo() {
        return fechaIngreso != null && fechaEgreso == null;
    }

    public int getId() { return id; }
    public String getNombreVisitante() { return nombreVisitante; }
    public String getDniVisitante() { return dniVisitante; }
    public LocalDateTime getFechaIngreso() { return fechaIngreso; }
    public LocalDateTime getFechaEgreso() { return fechaEgreso; }
    public void setFechaIngreso(LocalDateTime fechaIngreso) { this.fechaIngreso = fechaIngreso; }
    public void setFechaEgreso(LocalDateTime fechaEgreso) { this.fechaEgreso = fechaEgreso; }

    @Override
    public String toString() {
        return "Acceso [" + id + "] " + nombreVisitante +
               " | Ingreso: " + (fechaIngreso != null ? fechaIngreso : "sin registrar") +
               " | Egreso: " + (fechaEgreso != null ? fechaEgreso : "sin registrar");
    }
}
