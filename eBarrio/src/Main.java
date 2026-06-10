import model.accesos.Acceso;
import model.barrio.Barrio;
import model.barrio.Residente;
import model.barrio.Vivienda;
import sistema.SistemaBarrio;
import model.solicitud.personal.IncidenteSeguridad;
import model.solicitud.personal.PersonalMantenimiento;
import model.solicitud.personal.PersonalSeguridad;
import model.solicitud.personal.TareaMantenimiento;
import model.solicitud.reclamo.Reclamo;
import model.Administrador;

public class Main {

    public static void main(String[] args) {

        System.out.println("============================================");
        System.out.println("       SISTEMA eBarrio - Demostración       ");
        System.out.println("============================================\n");

        // ── 1. Inicializar el sistema (Facade) 
        SistemaBarrio sistema = new SistemaBarrio();

        // ── 2. Crear estructura del barrio
        Barrio barrio = new Barrio(1, "eBarrio Norte", "Av. Central 1000");

        Vivienda vivienda1 = new Vivienda(1, "Lote 12", "Calle Roble 120");
        Vivienda vivienda2 = new Vivienda(2, "Lote 18", "Calle Lago 85");

        Residente residente1 = new Residente(1, "Sofía", "Gómez", "40111222", "sofia@email.com", "1130000000");
        Residente residente2 = new Residente(2, "Martín", "Pérez", "38999888", "martin@email.com", "1140000000");

        vivienda1.agregarResidente(residente1);
        vivienda2.agregarResidente(residente2);
        barrio.agregarVivienda(vivienda1);
        barrio.agregarVivienda(vivienda2);

        System.out.println("Barrio configurado: " + barrio);
        System.out.println("Residentes en Lote 12: " + vivienda1.obtenerResidentes());

        // ── 3. Registrar un Administrador como observador global 
        Administrador admin = new Administrador(1, "María García", "maria@ebarrio.com");
        sistema.registrarObservadorGlobal(admin);
        System.out.println("\nAdministrador registrado: " + admin);

        // ── 4. Crear solicitudes a través del sistema (Facade) 
        System.out.println("\n--- Creando solicitudes ---");

        Reclamo reclamo = sistema.crearReclamo(
            "R01","Luminaria rota en sector G", "Alta", "Infraestructura"
        );

        TareaMantenimiento tarea = sistema.crearTareaMantenimiento(
            "T02", "Cortar el pasto del parque central", "Media", "Parque Central"
        );

        IncidenteSeguridad incidente = sistema.crearIncidenteSeguridad(
            "I01", "Persona desconocida merodeando el perímetro", "Alta", "Alto"
        );

        sistema.listarSolicitudes();

        // ── 5. Ciclo de vida completo de un reclamo (State + Observer) 
        System.out.println("--- Avanzando el reclamo por todos sus estados ---");
        sistema.avanzarEstadoSolicitud(reclamo); // Pendiente -> Asignado
        sistema.avanzarEstadoSolicitud(reclamo); // Asignado  -> En proceso
        sistema.avanzarEstadoSolicitud(reclamo); // En proceso -> Resuelto
        sistema.avanzarEstadoSolicitud(reclamo); // Resuelto  -> Cerrado
        sistema.avanzarEstadoSolicitud(reclamo); // Cerrado   -> error esperado

        // ── 6. Cancelar una solicitud desde un estado intermedio 
        System.out.println("\n--- Cancelando la tarea desde estado Asignado ---");
        sistema.avanzarEstadoSolicitud(tarea);  // Pendiente -> Asignado
        sistema.cancelarSolicitud(tarea);       // Asignado  -> Cerrado

        // ── 7. Accesos (PersonalSeguridad + Acceso) 
        System.out.println("\n--- Registrando accesos de visitantes ---");

        PersonalSeguridad guardia = new PersonalSeguridad(1, "Carlos López", "Mañana");
        System.out.println(guardia);

        Acceso acceso1 = sistema.registrarAcceso("Juan Torres", "42123123");
        Acceso acceso2 = sistema.registrarAcceso("Camila Ruiz", "38111222");

        System.out.println("\n--- Registrando egreso de Juan Torres ---");
        sistema.registrarEgresoAcceso(acceso1);

        sistema.listarAccesos();

        // ── 8. Personal de mantenimiento actualizando una tarea 
        System.out.println("--- Personal de mantenimiento operando ---");

        PersonalMantenimiento operario = new PersonalMantenimiento(1, "Sergio Ruiz", "Parque Central");
        System.out.println(operario);

        TareaMantenimiento tarea2 = sistema.crearTareaMantenimiento(
            "T03", "Reparar banco roto en plaza", "Baja", "Plaza"
        );
        operario.consultarTarea(tarea2);
        operario.actualizarEstadoTarea(tarea2); // Observer

        // ── 9. Estado final 
        System.out.println("\n--- Estado final de todas las solicitudes ---");
        sistema.listarSolicitudes();

        System.out.println("============================================");
        System.out.println("          Demostración finalizada           ");
        System.out.println("============================================");
    }
}