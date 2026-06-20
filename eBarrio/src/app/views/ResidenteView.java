package app.views;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ResidenteView {

    private final String nombreResidente;
    private final int cantidadNotificaciones;
    private final Runnable mostrarNotificaciones;
    private final Runnable cerrarSesion;
    private final Runnable mostrarInicio;
    private final Runnable mostrarVisitantes;
    private final Runnable mostrarSolicitudes;
    private final Runnable mostrarNuevoReclamo;

    public ResidenteView(
            String nombreResidente,
            int cantidadNotificaciones,
            Runnable mostrarNotificaciones,
            Runnable cerrarSesion,
            Runnable mostrarInicio,
            Runnable mostrarVisitantes,
            Runnable mostrarSolicitudes,
            Runnable mostrarNuevoReclamo
    ) {
        this.nombreResidente = nombreResidente;
        this.cantidadNotificaciones = cantidadNotificaciones;
        this.mostrarNotificaciones = mostrarNotificaciones;
        this.cerrarSesion = cerrarSesion;
        this.mostrarInicio = mostrarInicio;
        this.mostrarVisitantes = mostrarVisitantes;
        this.mostrarSolicitudes = mostrarSolicitudes;
        this.mostrarNuevoReclamo = mostrarNuevoReclamo;
    }

    public HBox crearBarraSuperior() {
        return AdministradorView.crearBarraSuperior(nombreResidente, cantidadNotificaciones, mostrarNotificaciones, cerrarSesion);
    }

    public VBox crearMenuLateral() {
        VBox sidebar = AdministradorView.crearBaseMenu("Panel residente");

        Button inicio = AdministradorView.crearBotonMenu("Mi inicio");
        Button visitantes = AdministradorView.crearBotonMenu("Mis visitantes");
        Button solicitudes = AdministradorView.crearBotonMenu("Mis reclamos");
        Button nuevoReclamo = AdministradorView.crearBotonMenu("Nuevo reclamo");

        inicio.setOnAction(e -> mostrarInicio.run());
        visitantes.setOnAction(e -> mostrarVisitantes.run());
        solicitudes.setOnAction(e -> mostrarSolicitudes.run());
        nuevoReclamo.setOnAction(e -> mostrarNuevoReclamo.run());
        sidebar.getChildren().addAll(inicio, visitantes, solicitudes, nuevoReclamo);
        return sidebar;
    }
}
