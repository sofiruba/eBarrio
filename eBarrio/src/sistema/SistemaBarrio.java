package sistema;

import java.util.ArrayList;
import java.util.List;

import model.notificaciones.Notificacion;
import model.solicitud.Solicitud;
import model.solicitud.SolicitudFactory;
import model.solicitud.personal.TareaMantenimiento;
import model.solicitud.personal.IncidenteSeguridad;
import model.solicitud.IObservador;
import model.solicitud.reclamo.Reclamo;
import model.accesos.Acceso;

// Clase principal del sistema, que actúa como fachada para gestionar las operaciones

public class SistemaBarrio {

    // Listas en memoria
    private List<Solicitud> solicitudes;
    private List<Acceso> accesos;
    private List<Notificacion> notificaciones;
    private List<IObservador> observadoresGlobales;

    // Contadores de IDs
    private int contadorSolicitudes = 1;
    private int contadorAccesos = 1;
    private int contadorNotificaciones = 1;

    public SistemaBarrio() {
        this.solicitudes = new ArrayList<>();
        this.accesos = new ArrayList<>();
        this.notificaciones = new ArrayList<>();
        this.observadoresGlobales = new ArrayList<>();
    }

    // ── Observadores globales ──────────────────────────────────────────────
    // Cualquier observador registrado aquí recibe notificaciones de TODAS
    // las solicitudes creadas desde ese momento.

    public void registrarObservadorGlobal(IObservador observador) {
        observadoresGlobales.add(observador);
        System.out.println("Observador global registrado en el sistema.");
    }

    public void quitarObservadorGlobal(IObservador observador) {
        observadoresGlobales.remove(observador);
    }

    // ── Creación de solicitudes ────────────────────────────────────────────

    public Reclamo crearReclamo(String nombre, String descripcion, String prioridad, String tipoReclamo) {
        Reclamo reclamo = (Reclamo) SolicitudFactory.crearSolicitud(
                "RECLAMO",
                contadorSolicitudes++,
                nombre,
                descripcion,
                prioridad,
                tipoReclamo
        );

        suscribirObservadoresGlobales(reclamo);
        solicitudes.add(reclamo);

        System.out.println("Reclamo creado: " + reclamo);
        notificar("Se creó el reclamo [" + reclamo.getId() + "]: " + descripcion, "Sistema");

        return reclamo;
    }

    public TareaMantenimiento crearTareaMantenimiento(String nombre, String descripcion, String prioridad, String sector) {
        TareaMantenimiento tarea = (TareaMantenimiento) SolicitudFactory.crearSolicitud(
                "TAREA_MANTENIMIENTO",
                contadorSolicitudes++,
                nombre,
                descripcion,
                prioridad,
                sector
        );

        suscribirObservadoresGlobales(tarea);
        solicitudes.add(tarea);

        System.out.println("Tarea de mantenimiento creada: " + tarea);
        notificar("Se creó la tarea [" + tarea.getId() + "]: " + descripcion, "Sistema");

        return tarea;
    }

    public IncidenteSeguridad crearIncidenteSeguridad(String nombre, String descripcion, String prioridad, String nivelRiesgo) {
        IncidenteSeguridad incidente = (IncidenteSeguridad) SolicitudFactory.crearSolicitud(
                "INCIDENTE_SEGURIDAD",
                contadorSolicitudes++,
                nombre,
                descripcion,
                prioridad,
                nivelRiesgo
        );

        suscribirObservadoresGlobales(incidente);
        solicitudes.add(incidente);

        System.out.println("Incidente de seguridad creado: " + incidente);
        notificar("Se creó el incidente [" + incidente.getId() + "]: " + descripcion, "Sistema");

        return incidente;
    }

    // ── Gestión de estados 

    public void avanzarEstadoSolicitud(Solicitud solicitud) {
        System.out.println("Avanzando solicitud [" + solicitud.getId() + "]...");
        solicitud.avanzar(); // State + Observer se disparan internamente
    }

    public void cancelarSolicitud(Solicitud solicitud) {
        System.out.println("Cancelando solicitud [" + solicitud.getId() + "]...");
        solicitud.cancelar();
    }

    // ── Accesos ────────────────────────────────────────────────────────────

    public Acceso registrarAcceso(String nombreVisitante, String dniVisitante) {
        Acceso acceso = new Acceso(contadorAccesos++, nombreVisitante, dniVisitante);
        acceso.registrarIngreso();
        accesos.add(acceso);
        notificar("Ingreso registrado: " + nombreVisitante, "Seguridad");
        return acceso;
    }

    public void registrarEgresoAcceso(Acceso acceso) {
        acceso.registrarEgreso();
        notificar("Egreso registrado: " + acceso.getNombreVisitante(), "Seguridad");
    }

    // ── Notificaciones ─────────────────────────────────────────────────────

    public void notificar(String mensaje, String destinatario) {
        Notificacion n = new Notificacion(contadorNotificaciones++, mensaje, destinatario);
        n.enviar();
        notificaciones.add(n);
    }

    // ── Consultas ──────────────────────────────────────────────────────────

    public void listarSolicitudes() {
        System.out.println("\n=== SOLICITUDES ===");
        if (solicitudes.isEmpty()) {
            System.out.println("No hay solicitudes registradas.");
        } else {
            for (Solicitud s : solicitudes) System.out.println(s);
        }
        System.out.println("==================\n");
    }

    public void listarAccesos() {
        System.out.println("\n=== ACCESOS ===");
        if (accesos.isEmpty()) {
            System.out.println("No hay accesos registrados.");
        } else {
            for (Acceso a : accesos) System.out.println(a);
        }
        System.out.println("===============\n");
    }

    public Solicitud buscarSolicitudPorId(int id) {
        for (Solicitud s : solicitudes) {
            if (s.getId() == id) return s;
        }
        System.out.println("No se encontró solicitud con ID: " + id);
        return null;
    }

    public List<Solicitud> getSolicitudes() { return solicitudes; }
    public List<Acceso> getAccesos() { return accesos; }
    public List<Notificacion> getNotificaciones() { return notificaciones; }

    // ── Auxiliares ─────────────────────────────────────────────────────────

    private void suscribirObservadoresGlobales(Solicitud solicitud) {
        for (IObservador obs : observadoresGlobales) {
            solicitud.agregarObservador(obs);
        }
    }
}
