package app.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class AdministradorView {

    private final int cantidadNotificaciones;
    private final Runnable mostrarNotificaciones;
    private final Runnable cerrarSesion;
    private final Runnable mostrarInicio;
    private final Runnable mostrarResidentes;
    private final Runnable mostrarAccesos;
    private final Runnable mostrarSolicitudes;

    public AdministradorView(
            int cantidadNotificaciones,
            Runnable mostrarNotificaciones,
            Runnable cerrarSesion,
            Runnable mostrarInicio,
            Runnable mostrarResidentes,
            Runnable mostrarAccesos,
            Runnable mostrarSolicitudes
    ) {
        this.cantidadNotificaciones = cantidadNotificaciones;
        this.mostrarNotificaciones = mostrarNotificaciones;
        this.cerrarSesion = cerrarSesion;
        this.mostrarInicio = mostrarInicio;
        this.mostrarResidentes = mostrarResidentes;
        this.mostrarAccesos = mostrarAccesos;
        this.mostrarSolicitudes = mostrarSolicitudes;
    }

    public HBox crearBarraSuperior() {
        return crearBarraSuperior("Administrador", cantidadNotificaciones, mostrarNotificaciones, cerrarSesion);
    }

    public VBox crearMenuLateral() {
        VBox sidebar = crearBaseMenu("Panel administrador");

        Button inicio = crearBotonMenu("Inicio");
        Button residentes = crearBotonMenu("Residentes");
        Button accesos = crearBotonMenu("Accesos");
        Button solicitudes = crearBotonMenu("Solicitudes");

        inicio.setOnAction(e -> mostrarInicio.run());
        residentes.setOnAction(e -> mostrarResidentes.run());
        accesos.setOnAction(e -> mostrarAccesos.run());
        solicitudes.setOnAction(e -> mostrarSolicitudes.run());
        sidebar.getChildren().addAll(inicio, residentes, accesos, solicitudes);
        return sidebar;
    }

    static HBox crearBarraSuperior(String usuario, int cantidadNotificaciones, Runnable mostrarNotificaciones, Runnable cerrarSesion) {
        Label menu = new Label("eB");
        menu.getStyleClass().add("top-icon");

        Label marca = new Label("eBarrio");
        marca.getStyleClass().add("top-brand");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button alerta = new Button("Notificaciones: " + cantidadNotificaciones);
        alerta.getStyleClass().add("top-pill");
        alerta.setOnAction(e -> mostrarNotificaciones.run());

        Label usuarioLabel = new Label(usuario);
        usuarioLabel.getStyleClass().add("top-user");

        Button cerrarSesionBtn = new Button("Cerrar sesion");
        cerrarSesionBtn.getStyleClass().add("view-switch");
        cerrarSesionBtn.setOnAction(e -> cerrarSesion.run());

        HBox top = new HBox(14, menu, marca, spacer, alerta, usuarioLabel, cerrarSesionBtn);
        top.setAlignment(Pos.CENTER_LEFT);
        top.getStyleClass().add("topbar");
        return top;
    }

    static VBox crearBaseMenu(String modo) {
        Label logoMark = new Label("eB");
        logoMark.getStyleClass().add("logo-mark");

        Label logo = new Label("eBarrio");
        logo.getStyleClass().add("logo");

        Label subtitulo = new Label("Gestion de barrios cerrados");
        subtitulo.getStyleClass().add("subtitle");

        Label modoLabel = new Label(modo);
        modoLabel.getStyleClass().add("menu-section");

        VBox sidebar = new VBox(10, logoMark, logo, subtitulo, modoLabel);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPadding(new Insets(26, 18, 26, 18));
        sidebar.setPrefWidth(235);
        return sidebar;
    }

    static Button crearBotonMenu(String texto) {
        Button boton = new Button(texto);
        boton.getStyleClass().add("menu-button");
        boton.setMaxWidth(Double.MAX_VALUE);
        return boton;
    }
}
