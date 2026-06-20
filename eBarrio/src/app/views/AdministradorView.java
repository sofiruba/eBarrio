package app.views;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    private final Runnable mostrarSeguridad;
    private final Runnable mostrarMantenimiento;

    public AdministradorView(
            int cantidadNotificaciones,
            Runnable mostrarNotificaciones,
            Runnable cerrarSesion,
            Runnable mostrarInicio,
            Runnable mostrarResidentes,
            Runnable mostrarAccesos,
            Runnable mostrarSolicitudes,
            Runnable mostrarSeguridad,
            Runnable mostrarMantenimiento
    ) {
        this.cantidadNotificaciones = cantidadNotificaciones;
        this.mostrarNotificaciones = mostrarNotificaciones;
        this.cerrarSesion = cerrarSesion;
        this.mostrarInicio = mostrarInicio;
        this.mostrarResidentes = mostrarResidentes;
        this.mostrarAccesos = mostrarAccesos;
        this.mostrarSolicitudes = mostrarSolicitudes;
        this.mostrarSeguridad = mostrarSeguridad;
        this.mostrarMantenimiento = mostrarMantenimiento;
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
        Button seguridad = crearBotonMenu("Seguridad");
        Button mantenimiento = crearBotonMenu("Mantenimiento");

        inicio.setOnAction(e -> mostrarInicio.run());
        residentes.setOnAction(e -> mostrarResidentes.run());
        accesos.setOnAction(e -> mostrarAccesos.run());
        solicitudes.setOnAction(e -> mostrarSolicitudes.run());
        seguridad.setOnAction(e -> mostrarSeguridad.run());
        mantenimiento.setOnAction(e -> mostrarMantenimiento.run());
        sidebar.getChildren().addAll(inicio, residentes, accesos, solicitudes, seguridad, mantenimiento);
        return sidebar;
    }

    static HBox crearBarraSuperior(String usuario, int cantidadNotificaciones, Runnable mostrarNotificaciones, Runnable cerrarSesion) {
        ImageView menu = crearLogo(38, 38, "top-logo");

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

        HBox top = new HBox(12, menu, marca, spacer, alerta, usuarioLabel, cerrarSesionBtn);
        top.setAlignment(Pos.CENTER_LEFT);
        top.getStyleClass().add("topbar");
        return top;
    }

    static VBox crearBaseMenu(String modo) {
        ImageView logo = crearLogo(170, 132, "sidebar-logo");

        Label modoLabel = new Label(modo);
        modoLabel.getStyleClass().add("menu-section");

        VBox sidebar = new VBox(12, logo, modoLabel);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPadding(new Insets(22, 14, 22, 14));
        sidebar.setPrefWidth(210);
        return sidebar;
    }

    private static ImageView crearLogo(double ancho, double alto, String estilo) {
        ImageView logo = new ImageView(new Image(urlLogo()));
        logo.getStyleClass().add(estilo);
        logo.setFitWidth(ancho);
        logo.setFitHeight(alto);
        logo.setPreserveRatio(true);
        logo.setSmooth(true);
        return logo;
    }

    private static String urlLogo() {
        URL recurso = AdministradorView.class.getResource("/images/eBarrio_logo.png");
        if (recurso != null) {
            return recurso.toExternalForm();
        }
        Path desdeRepo = Path.of("eBarrio/src/images/eBarrio_logo.png");
        Path desdeModulo = Path.of("src/images/eBarrio_logo.png");
        return Files.exists(desdeRepo) ? desdeRepo.toUri().toString() : desdeModulo.toUri().toString();
    }

    static Button crearBotonMenu(String texto) {
        Button boton = new Button(texto);
        boton.getStyleClass().add("menu-button");
        boton.setMaxWidth(Double.MAX_VALUE);
        return boton;
    }
}
