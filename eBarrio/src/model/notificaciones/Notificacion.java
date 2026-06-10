package model.notificaciones;

import java.time.LocalDateTime;

// Representa una notificación generada por el sistema.

public class Notificacion {

    private int id;
    private String mensaje;
    private LocalDateTime fecha;
    private String destinatario;

    public Notificacion(int id, String mensaje, String destinatario) {
        this.id = id;
        this.mensaje = mensaje;
        this.destinatario = destinatario;
        this.fecha = LocalDateTime.now();
    }

    public void enviar() {
        System.out.println("[Notificación " + id + " -> " + destinatario + "] " +
            mensaje + " | " + fecha);
    }

    public int getId() { return id; }
    public String getMensaje() { return mensaje; }
    public LocalDateTime getFecha() { return fecha; }
    public String getDestinatario() { return destinatario; }

    @Override
    public String toString() {
        return "[" + id + "] Para: " + destinatario + " | " + mensaje + " | " + fecha;
    }
}
