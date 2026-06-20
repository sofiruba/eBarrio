package model.solicitud.test;

import model.accesos.Acceso;
import model.barrio.Barrio;
import model.barrio.Residente;
import model.barrio.Vivienda;
import model.solicitud.Solicitud;
import model.solicitud.personal.TareaMantenimiento;
import model.solicitud.reclamo.Reclamo;
import sistema.SistemaBarrio;

public class EscenariosEjecucion {

    public static void main(String[] args) {
        SistemaBarrio sistema = new SistemaBarrio();

        Barrio barrio = sistema.registrarBarrio("eBarrio Norte", "Av. Central 1000");
        Vivienda vivienda = sistema.registrarVivienda(barrio, "Lote 12", "Calle Roble 120");
        Residente residente = sistema.registrarResidente(
                "Sofia",
                "Gomez",
                "40111222",
                "sofia@email.com",
                "1130000000",
                vivienda
        );

        verificar(sistema.getBarrios().size() == 1, "Debe registrar un barrio");
        verificar(sistema.getViviendas().size() == 1, "Debe registrar una vivienda");
        verificar(sistema.getResidentes().size() == 1, "Debe registrar un residente");
        verificar(vivienda.getResidentes().contains(residente), "Debe asociar residente a vivienda");

        sistema.registrarVisitante(residente, "Camila Ruiz", "42123123", "AB123CD", "Visita familiar");
        verificar(sistema.getVisitantes().size() == 1, "Debe registrar visitante");
        verificar(residente.getVisitantes().size() == 1, "Debe asociar visitante a residente");

        Acceso acceso = sistema.registrarAcceso("Camila Ruiz", "42123123");
        verificar(acceso.estaActivo(), "El acceso debe quedar activo al ingresar");
        sistema.registrarEgresoAcceso(acceso);
        verificar(!acceso.estaActivo(), "El acceso debe finalizar al registrar egreso");

        Reclamo reclamo = sistema.crearReclamo("Sofia Gomez", "Luminaria rota", "Alta", "Infraestructura");
        verificar(sistema.getSolicitudes().size() == 1, "Debe registrar un reclamo");
        verificar("Pendiente".equals(reclamo.getEstado().mostrarNombreEstado()), "El reclamo inicia pendiente");

        sistema.avanzarEstadoSolicitud(reclamo);
        verificar("Asignado".equals(reclamo.getEstado().mostrarNombreEstado()), "Pendiente debe avanzar a Asignado");

        sistema.avanzarEstadoSolicitud(reclamo);
        verificar("En proceso".equals(reclamo.getEstado().mostrarNombreEstado()), "Asignado debe avanzar a En proceso");

        sistema.avanzarEstadoSolicitud(reclamo);
        verificar("Resuelto".equals(reclamo.getEstado().mostrarNombreEstado()), "En proceso debe avanzar a Resuelto");

        sistema.avanzarEstadoSolicitud(reclamo);
        verificar("Cerrado".equals(reclamo.getEstado().mostrarNombreEstado()), "Resuelto debe avanzar a Cerrado");

        TareaMantenimiento tarea = sistema.crearTareaMantenimiento("Mantenimiento", "Cortar pasto", "Media", "Parque");
        sistema.cancelarSolicitud(tarea);
        verificar("Cerrado".equals(tarea.getEstado().mostrarNombreEstado()), "Una tarea pendiente cancelada debe cerrar");

        Solicitud encontrada = sistema.buscarSolicitudPorId(reclamo.getId());
        verificar(encontrada == reclamo, "Debe buscar solicitud por id");

        System.out.println("Todos los escenarios de ejecucion finalizaron correctamente.");
    }

    private static void verificar(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new AssertionError(mensaje);
        }
    }
}
