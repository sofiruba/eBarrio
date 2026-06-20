import model.Administrador;
import model.accesos.Acceso;
import model.barrio.Barrio;
import model.barrio.Residente;
import model.barrio.Vivienda;
import model.solicitud.personal.IncidenteSeguridad;
import model.solicitud.personal.PersonalMantenimiento;
import model.solicitud.personal.PersonalSeguridad;
import model.solicitud.personal.TareaMantenimiento;
import model.solicitud.reclamo.Reclamo;
import sistema.SistemaBarrio;

public class Main {

    public static void main(String[] args) {
        System.out.println("============================================");
        System.out.println("       SISTEMA eBarrio - Demostracion       ");
        System.out.println("============================================\n");

        SistemaBarrio sistema = new SistemaBarrio();

        Barrio barrio = sistema.registrarBarrio("eBarrio Norte", "Av. Central 1000");
        Vivienda vivienda1 = sistema.registrarVivienda(barrio, "Lote 12", "Calle Roble 120");
        Vivienda vivienda2 = sistema.registrarVivienda(barrio, "Lote 18", "Calle Lago 85");

        Residente residente1 = sistema.registrarResidente(
                "Sofia",
                "Gomez",
                "40111222",
                "sofia@email.com",
                "1130000000",
                vivienda1
        );

        Residente residente2 = sistema.registrarResidente(
                "Martin",
                "Perez",
                "38999888",
                "martin@email.com",
                "1140000000",
                vivienda2
        );

        sistema.registrarVisitante(residente1, "Camila Ruiz", "42123123", "AB123CD", "Visita familiar");
        sistema.registrarVisitante(residente2, "Juan Torres", "38111222", "AC456EF", "Reunion con residente");

        System.out.println("Barrio configurado: " + barrio);
        System.out.println("Residentes registrados: " + sistema.getResidentes().size());
        System.out.println("Visitantes autorizados: " + sistema.getVisitantes().size());

        Administrador admin = new Administrador(1, "Maria Garcia", "maria@ebarrio.com");
        sistema.registrarObservadorGlobal(admin);
        System.out.println("\nAdministrador registrado: " + admin);

        System.out.println("\n--- Creando solicitudes con Factory + Facade ---");
        Reclamo reclamo = sistema.crearReclamo(
                "Sofia Gomez",
                "Luminaria rota en sector G",
                "Alta",
                "Infraestructura"
        );

        TareaMantenimiento tarea = sistema.crearTareaMantenimiento(
                "Mantenimiento",
                "Cortar el pasto del parque central",
                "Media",
                "Parque Central"
        );

        IncidenteSeguridad incidente = sistema.crearIncidenteSeguridad(
                "Seguridad",
                "Persona desconocida merodeando el perimetro",
                "Alta",
                "Alto"
        );

        sistema.listarSolicitudes();

        System.out.println("--- Avanzando el reclamo por todos sus estados (State + Observer) ---");
        sistema.avanzarEstadoSolicitud(reclamo);
        sistema.avanzarEstadoSolicitud(reclamo);
        sistema.avanzarEstadoSolicitud(reclamo);
        sistema.avanzarEstadoSolicitud(reclamo);
        sistema.avanzarEstadoSolicitud(reclamo);

        System.out.println("\n--- Cancelando una tarea desde estado Asignado ---");
        sistema.avanzarEstadoSolicitud(tarea);
        sistema.cancelarSolicitud(tarea);

        System.out.println("\n--- Registrando accesos ---");
        PersonalSeguridad guardia = new PersonalSeguridad(1, "Carlos Lopez", "Manana");
        System.out.println(guardia);

        Acceso acceso1 = sistema.registrarAcceso("Juan Torres", "42123123");
        sistema.registrarAcceso("Camila Ruiz", "38111222");
        sistema.registrarEgresoAcceso(acceso1);
        sistema.listarAccesos();

        System.out.println("--- Personal de mantenimiento actualizando tarea ---");
        PersonalMantenimiento operario = new PersonalMantenimiento(1, "Sergio Ruiz", "Parque Central");
        System.out.println(operario);
        operario.consultarTarea(tarea);

        TareaMantenimiento tarea2 = sistema.crearTareaMantenimiento(
                "Mantenimiento",
                "Reparar banco roto en plaza",
                "Baja",
                "Plaza"
        );
        operario.actualizarEstadoTarea(tarea2);

        System.out.println("\n--- Estado final ---");
        sistema.listarSolicitudes();
        System.out.println("Notificaciones emitidas: " + sistema.getNotificaciones().size());

        System.out.println("============================================");
        System.out.println("          Demostracion finalizada           ");
        System.out.println("============================================");
    }
}
