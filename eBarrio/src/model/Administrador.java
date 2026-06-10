import solicitud.IObservador;
import solicitud.Solicitud;

// Administrador del barrio.

public class Administrador implements IObservador {

    private int id;
    private String nombre;
    private String email;

    public Administrador(int id, String nombre, String email) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
    }

    /**
     * Se ejecuta automáticamente cuando una Solicitud cambia de estado.
     * El método es llamado por Solicitud.notificarObservadores().
     */
    @Override
    public void actualizar(Solicitud solicitud) {
        System.out.println("[Notificación -> Administrador " + nombre + "] " +
            "Solicitud [" + solicitud.getId() + "] cambió a: " +
            solicitud.getEstado().mostrarNombreEstado());
    }

    public void gestionarSolicitud(Solicitud solicitud) {
        System.out.println("Administrador " + nombre +
            " gestionando solicitud [" + solicitud.getId() + "]");
        solicitud.avanzar();
    }

    public void asignarResponsable(Solicitud solicitud) {
        System.out.println("Administrador " + nombre +
            " asignando responsable a solicitud [" + solicitud.getId() + "]");
        solicitud.avanzar();
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return "Administrador [" + id + "] " + nombre + " | Email: " + email;
    }
}
