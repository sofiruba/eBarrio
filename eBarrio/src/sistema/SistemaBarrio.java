package sistema;

import java.util.ArrayList;
import java.util.List;

import model.solicitud.SolicitudFactory;

import model.accesos.Acceso;
import model.accesos.Visitante;

import model.barrio.Barrio;
import model.barrio.Residente;
import model.barrio.Vivienda;

import model.notificaciones.Notificacion;

import model.solicitud.IObservador;
import model.solicitud.Solicitud;
import model.solicitud.personal.IncidenteSeguridad;
import model.solicitud.personal.TareaMantenimiento;
import model.solicitud.reclamo.Reclamo;

// Clase principal del sistema. Funciona como fachada/controlador.
// Centraliza las operaciones principales y mantiene los datos en memoria.
public class SistemaBarrio {

    // Listas en memoria
    private List<Barrio> barrios;
    private List<Vivienda> viviendas;
    private List<Residente> residentes;
    private List<Visitante> visitantes;

    private List<Solicitud> solicitudes;
    private List<Acceso> accesos;
    private List<Notificacion> notificaciones;
    private List<IObservador> observadoresGlobales;

    // Contadores de IDs
    private int contadorBarrios = 1;
    private int contadorViviendas = 1;
    private int contadorResidentes = 1;
    private int contadorVisitantes = 1;
    private int contadorSolicitudes = 1;
    private int contadorAccesos = 1;
    private int contadorNotificaciones = 1;

    public SistemaBarrio() {
        this.barrios = new ArrayList<>();
        this.viviendas = new ArrayList<>();
        this.residentes = new ArrayList<>();
        this.visitantes = new ArrayList<>();

        this.solicitudes = new ArrayList<>();
        this.accesos = new ArrayList<>();
        this.notificaciones = new ArrayList<>();
        this.observadoresGlobales = new ArrayList<>();
    }

    // ─────────────────────────────────────────────
    // Barrio, viviendas, residentes y visitantes
    // ─────────────────────────────────────────────

    public Barrio registrarBarrio(String nombre, String direccion) {
        Barrio barrio = new Barrio(contadorBarrios++, nombre, direccion);
        barrios.add(barrio);
        return barrio;
    }

    public Vivienda registrarVivienda(Barrio barrio, String lote, String direccion) {
        Vivienda vivienda = new Vivienda(contadorViviendas++, lote, direccion);
        viviendas.add(vivienda);

        if (barrio != null) {
            barrio.agregarVivienda(vivienda);
        }

        return vivienda;
    }

    public Residente registrarResidente(
            String nombre,
            String apellido,
            String dni,
            String email,
            String telefono,
            Vivienda vivienda
    ) {
        Residente residente = new Residente(
                contadorResidentes++,
                nombre,
                apellido,
                dni,
                email,
                telefono
        );

        residentes.add(residente);

        if (vivienda != null) {
            vivienda.agregarResidente(residente);
        }

        return residente;
    }

    public Visitante registrarVisitante(
            Residente residente,
            String nombre,
            String dni,
            String patente,
            String motivoVisita
    ) {
        Visitante visitante = new Visitante(
                contadorVisitantes++,
                nombre,
                dni,
                patente,
                motivoVisita
        );

        visitantes.add(visitante);

        if (residente != null) {
            residente.registrarVisitante(visitante);
        }

        return visitante;
    }

    // ─────────────────────────────────────────────
    // Observadores globales
    // ─────────────────────────────────────────────

    public void registrarObservadorGlobal(IObservador observador) {
        observadoresGlobales.add(observador);
        System.out.println("Observador global registrado en el sistema.");
    }

    public void quitarObservadorGlobal(IObservador observador) {
        observadoresGlobales.remove(observador);
    }

    private void suscribirObservadoresGlobales(Solicitud solicitud) {
        for (IObservador observador : observadoresGlobales) {
            solicitud.agregarObservador(observador);
        }
    }

    // ─────────────────────────────────────────────
    // Creación de solicitudes con Factory
    // ─────────────────────────────────────────────

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

    public TareaMantenimiento crearTareaMantenimiento(
            String nombre,
            String descripcion,
            String prioridad,
            String sector
    ) {
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

    public IncidenteSeguridad crearIncidenteSeguridad(
            String nombre,
            String descripcion,
            String prioridad,
            String nivelRiesgo
    ) {
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

    // ─────────────────────────────────────────────
    // Gestión de estados
    // ─────────────────────────────────────────────

    public void avanzarEstadoSolicitud(Solicitud solicitud) {
        if (solicitud == null) {
            System.out.println("No se puede avanzar una solicitud nula.");
            return;
        }

        System.out.println("Avanzando solicitud [" + solicitud.getId() + "]...");
        solicitud.avanzar();
    }

    public void cancelarSolicitud(Solicitud solicitud) {
        if (solicitud == null) {
            System.out.println("No se puede cancelar una solicitud nula.");
            return;
        }

        System.out.println("Cancelando solicitud [" + solicitud.getId() + "]...");
        solicitud.cancelar();
    }

    public Solicitud buscarSolicitudPorId(int id) {
        for (Solicitud solicitud : solicitudes) {
            if (solicitud.getId() == id) {
                return solicitud;
            }
        }

        System.out.println("No se encontró solicitud con ID: " + id);
        return null;
    }

    // ─────────────────────────────────────────────
    // Accesos
    // ─────────────────────────────────────────────

    public Acceso registrarAcceso(String nombreVisitante, String dniVisitante) {
        Acceso acceso = new Acceso(contadorAccesos++, nombreVisitante, dniVisitante);
        acceso.registrarIngreso();

        accesos.add(acceso);

        notificar("Ingreso registrado: " + nombreVisitante, "Seguridad");

        return acceso;
    }

    public void registrarEgresoAcceso(Acceso acceso) {
        if (acceso == null) {
            System.out.println("No se puede registrar egreso de un acceso nulo.");
            return;
        }

        acceso.registrarEgreso();
        notificar("Egreso registrado: " + acceso.getNombreVisitante(), "Seguridad");
    }

    // ─────────────────────────────────────────────
    // Notificaciones
    // ─────────────────────────────────────────────

    public Notificacion notificar(String mensaje, String destinatario) {
        Notificacion notificacion = new Notificacion(contadorNotificaciones++, mensaje, destinatario);
        notificacion.enviar();

        notificaciones.add(notificacion);

        return notificacion;
    }

    // ─────────────────────────────────────────────
    // Consultas por consola
    // ─────────────────────────────────────────────

    public void listarSolicitudes() {
        System.out.println("\n=== SOLICITUDES ===");

        if (solicitudes.isEmpty()) {
            System.out.println("No hay solicitudes registradas.");
        } else {
            for (Solicitud solicitud : solicitudes) {
                System.out.println(solicitud);
            }
        }

        System.out.println("==================\n");
    }

    public void listarAccesos() {
        System.out.println("\n=== ACCESOS ===");

        if (accesos.isEmpty()) {
            System.out.println("No hay accesos registrados.");
        } else {
            for (Acceso acceso : accesos) {
                System.out.println(acceso);
            }
        }

        System.out.println("===============\n");
    }

    // ─────────────────────────────────────────────
    // Getters
    // ─────────────────────────────────────────────

    public List<Barrio> getBarrios() {
        return barrios;
    }

    public List<Vivienda> getViviendas() {
        return viviendas;
    }

    public List<Residente> getResidentes() {
        return residentes;
    }

    public List<Visitante> getVisitantes() {
        return visitantes;
    }

    public List<Solicitud> getSolicitudes() {
        return solicitudes;
    }

    public List<Acceso> getAccesos() {
        return accesos;
    }

    public List<Notificacion> getNotificaciones() {
        return notificaciones;
    }
}