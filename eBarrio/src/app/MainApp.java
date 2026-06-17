package app;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import model.accesos.Acceso;
import model.accesos.Visitante;

import model.barrio.Barrio;
import model.barrio.Residente;
import model.barrio.Vivienda;

import model.solicitud.Solicitud;
import model.solicitud.reclamo.Reclamo;

import sistema.SistemaBarrio;

import java.net.URL;
import java.util.Optional;

public class MainApp extends Application {

    private SistemaBarrio sistemaBarrio = new SistemaBarrio();

    private ObservableList<Residente> residentes = FXCollections.observableArrayList();
    private ObservableList<Visitante> visitantes = FXCollections.observableArrayList();
    private ObservableList<Solicitud> solicitudes = FXCollections.observableArrayList();
    private ObservableList<Acceso> accesos = FXCollections.observableArrayList();

    private TableView<Residente> tablaResidentes;
    private TableView<Visitante> tablaVisitantes;
    private TableView<Solicitud> tablaSolicitudes;
    private TableView<Acceso> tablaAccesos;

    private Barrio barrioPrincipal;

    @Override
    public void start(Stage stage) {
        cargarDatosDePrueba();

        BorderPane root = new BorderPane();
        root.getStyleClass().add("root-layout");

        VBox sidebar = crearMenuLateral();
        VBox contenido = crearPantallaPrincipal();

        root.setLeft(sidebar);
        root.setCenter(contenido);

        Scene scene = new Scene(root, 1100, 680);
        cargarEstilos(scene);

        stage.setTitle("eBarrio - Gestión de Barrio Cerrado");
        stage.setScene(scene);
        stage.show();
    }

    private void cargarEstilos(Scene scene) {
        URL css = getClass().getResource("/styles/styles.css");

        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        } else {
            System.out.println("No se encontró styles.css. La app va a abrir sin estilos.");
        }
    }

    private VBox crearMenuLateral() {
        Label logo = new Label("eBarrio");
        logo.getStyleClass().add("logo");

        Label subtitulo = new Label("Gestión segura del barrio");
        subtitulo.getStyleClass().add("subtitle");

        Button btnResidentes = new Button("Residentes");
        Button btnVisitantes = new Button("Visitantes");
        Button btnReclamos = new Button("Nuevo reclamo");
        Button btnAccesos = new Button("Registrar acceso");
        Button btnAvanzarEstado = new Button("Avanzar estado");

        btnResidentes.getStyleClass().add("menu-button");
        btnVisitantes.getStyleClass().add("menu-button");
        btnReclamos.getStyleClass().add("menu-button");
        btnAccesos.getStyleClass().add("menu-button");
        btnAvanzarEstado.getStyleClass().add("menu-button");

        btnResidentes.setOnAction(e ->
                mostrarAlerta("Residentes", "El módulo de residentes se visualiza en la tabla principal.")
        );

        btnVisitantes.setOnAction(e ->
                mostrarAlerta("Visitantes", "El módulo de visitantes se visualiza en la tabla principal.")
        );

        btnReclamos.setOnAction(e -> mostrarFormularioCrearReclamo());
        btnAccesos.setOnAction(e -> registrarAccesoVisitanteSeleccionado());
        btnAvanzarEstado.setOnAction(e -> avanzarEstadoSolicitudSeleccionada());

        VBox sidebar = new VBox(
                15,
                logo,
                subtitulo,
                btnResidentes,
                btnVisitantes,
                btnReclamos,
                btnAccesos,
                btnAvanzarEstado
        );

        sidebar.setPadding(new Insets(30));
        sidebar.setPrefWidth(250);
        sidebar.getStyleClass().add("sidebar");

        return sidebar;
    }

    private VBox crearPantallaPrincipal() {
        Label titulo = new Label("Panel inicial");
        titulo.getStyleClass().add("title");

        Label descripcion = new Label("Primera versión funcional en memoria del sistema eBarrio.");
        descripcion.getStyleClass().add("description");

        tablaResidentes = crearTablaResidentes();
        tablaVisitantes = crearTablaVisitantes();
        tablaSolicitudes = crearTablaSolicitudes();
        tablaAccesos = crearTablaAccesos();

        Button agregarResidente = new Button("Agregar residente");
        Button registrarVisitante = new Button("Registrar visitante");
        Button crearReclamo = new Button("Crear reclamo");
        Button registrarAcceso = new Button("Registrar acceso");

        agregarResidente.getStyleClass().add("primary-button");
        registrarVisitante.getStyleClass().add("secondary-button");
        crearReclamo.getStyleClass().add("secondary-button");
        registrarAcceso.getStyleClass().add("secondary-button");

        agregarResidente.setOnAction(e -> mostrarFormularioAgregarResidente());
        registrarVisitante.setOnAction(e -> mostrarFormularioRegistrarVisitante());
        crearReclamo.setOnAction(e -> mostrarFormularioCrearReclamo());
        registrarAcceso.setOnAction(e -> registrarAccesoVisitanteSeleccionado());

        HBox botones = new HBox(12, agregarResidente, registrarVisitante, crearReclamo, registrarAcceso);
        botones.setAlignment(Pos.CENTER_LEFT);

        VBox cardResidentes = crearCard("Residentes", tablaResidentes);
        VBox cardVisitantes = crearCard("Visitantes autorizados", tablaVisitantes);
        VBox cardSolicitudes = crearCard("Solicitudes / Reclamos", tablaSolicitudes);
        VBox cardAccesos = crearCard("Accesos registrados", tablaAccesos);

        HBox fila1 = new HBox(20, cardResidentes, cardVisitantes);
        HBox.setHgrow(cardResidentes, Priority.ALWAYS);
        HBox.setHgrow(cardVisitantes, Priority.ALWAYS);

        HBox fila2 = new HBox(20, cardSolicitudes, cardAccesos);
        HBox.setHgrow(cardSolicitudes, Priority.ALWAYS);
        HBox.setHgrow(cardAccesos, Priority.ALWAYS);

        VBox contenido = new VBox(18, titulo, descripcion, botones, fila1, fila2);
        contenido.setPadding(new Insets(35));

        return contenido;
    }

    private VBox crearCard(String titulo, TableView<?> tabla) {
        Label label = new Label(titulo);
        label.getStyleClass().add("card-title");

        VBox card = new VBox(10, label, tabla);
        card.getStyleClass().add("card");

        return card;
    }

    private TableView<Residente> crearTablaResidentes() {
        TableView<Residente> tabla = new TableView<>();
        tabla.setItems(residentes);

        TableColumn<Residente, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Residente, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        TableColumn<Residente, String> colApellido = new TableColumn<>("Apellido");
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));

        TableColumn<Residente, String> colDni = new TableColumn<>("DNI");
        colDni.setCellValueFactory(new PropertyValueFactory<>("dni"));

        tabla.getColumns().addAll(colId, colNombre, colApellido, colDni);
        tabla.setPrefHeight(210);

        return tabla;
    }

    private TableView<Visitante> crearTablaVisitantes() {
        TableView<Visitante> tabla = new TableView<>();
        tabla.setItems(visitantes);

        TableColumn<Visitante, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Visitante, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        TableColumn<Visitante, String> colDni = new TableColumn<>("DNI");
        colDni.setCellValueFactory(new PropertyValueFactory<>("dni"));

        TableColumn<Visitante, String> colMotivo = new TableColumn<>("Motivo");
        colMotivo.setCellValueFactory(new PropertyValueFactory<>("motivoVisita"));

        tabla.getColumns().addAll(colId, colNombre, colDni, colMotivo);
        tabla.setPrefHeight(210);

        return tabla;
    }

    private TableView<Solicitud> crearTablaSolicitudes() {
        TableView<Solicitud> tabla = new TableView<>();
        tabla.setItems(solicitudes);

        TableColumn<Solicitud, String> colDatos = new TableColumn<>("Solicitud");
        colDatos.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().toString()));
        colDatos.setPrefWidth(450);

        tabla.getColumns().add(colDatos);
        tabla.setPrefHeight(210);

        return tabla;
    }

    private TableView<Acceso> crearTablaAccesos() {
        TableView<Acceso> tabla = new TableView<>();
        tabla.setItems(accesos);

        TableColumn<Acceso, String> colDatos = new TableColumn<>("Acceso");
        colDatos.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().toString()));
        colDatos.setPrefWidth(450);

        tabla.getColumns().add(colDatos);
        tabla.setPrefHeight(210);

        return tabla;
    }

    private void mostrarFormularioAgregarResidente() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Agregar residente");
        dialog.setHeaderText("Cargar nuevo residente");

        TextField campoNombre = new TextField();
        campoNombre.setPromptText("Nombre");

        TextField campoApellido = new TextField();
        campoApellido.setPromptText("Apellido");

        TextField campoDni = new TextField();
        campoDni.setPromptText("DNI");

        TextField campoEmail = new TextField();
        campoEmail.setPromptText("Email");

        TextField campoTelefono = new TextField();
        campoTelefono.setPromptText("Teléfono");

        TextField campoLote = new TextField();
        campoLote.setPromptText("Lote / Vivienda");

        GridPane grid = crearGridFormulario();

        grid.add(new Label("Nombre:"), 0, 0);
        grid.add(campoNombre, 1, 0);

        grid.add(new Label("Apellido:"), 0, 1);
        grid.add(campoApellido, 1, 1);

        grid.add(new Label("DNI:"), 0, 2);
        grid.add(campoDni, 1, 2);

        grid.add(new Label("Email:"), 0, 3);
        grid.add(campoEmail, 1, 3);

        grid.add(new Label("Teléfono:"), 0, 4);
        grid.add(campoTelefono, 1, 4);

        grid.add(new Label("Lote:"), 0, 5);
        grid.add(campoLote, 1, 5);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> resultado = dialog.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            if (campoNombre.getText().isBlank()
                    || campoApellido.getText().isBlank()
                    || campoDni.getText().isBlank()) {
                mostrarAlerta("Datos incompletos", "Nombre, apellido y DNI son obligatorios.");
                return;
            }

            Vivienda vivienda = sistemaBarrio.registrarVivienda(
                    barrioPrincipal,
                    campoLote.getText().isBlank() ? "Sin lote" : campoLote.getText(),
                    "Sin dirección cargada"
            );

            sistemaBarrio.registrarResidente(
                    campoNombre.getText(),
                    campoApellido.getText(),
                    campoDni.getText(),
                    campoEmail.getText(),
                    campoTelefono.getText(),
                    vivienda
            );

            actualizarTablasDesdeSistema();

            mostrarAlerta("Residente agregado", "El residente fue cargado correctamente en memoria.");
        }
    }

    private void mostrarFormularioRegistrarVisitante() {
        Residente residenteSeleccionado = tablaResidentes.getSelectionModel().getSelectedItem();

        if (residenteSeleccionado == null) {
            mostrarAlerta("Seleccioná un residente", "Primero seleccioná un residente de la tabla.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Registrar visitante");
        dialog.setHeaderText("Registrar visitante para: "
                + residenteSeleccionado.getNombre()
                + " "
                + residenteSeleccionado.getApellido());

        TextField campoNombre = new TextField();
        campoNombre.setPromptText("Nombre del visitante");

        TextField campoDni = new TextField();
        campoDni.setPromptText("DNI");

        TextField campoPatente = new TextField();
        campoPatente.setPromptText("Patente");

        TextField campoMotivo = new TextField();
        campoMotivo.setPromptText("Motivo de visita");

        GridPane grid = crearGridFormulario();

        grid.add(new Label("Nombre:"), 0, 0);
        grid.add(campoNombre, 1, 0);

        grid.add(new Label("DNI:"), 0, 1);
        grid.add(campoDni, 1, 1);

        grid.add(new Label("Patente:"), 0, 2);
        grid.add(campoPatente, 1, 2);

        grid.add(new Label("Motivo:"), 0, 3);
        grid.add(campoMotivo, 1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> resultado = dialog.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            if (campoNombre.getText().isBlank() || campoDni.getText().isBlank()) {
                mostrarAlerta("Datos incompletos", "Nombre y DNI del visitante son obligatorios.");
                return;
            }

            sistemaBarrio.registrarVisitante(
                    residenteSeleccionado,
                    campoNombre.getText(),
                    campoDni.getText(),
                    campoPatente.getText(),
                    campoMotivo.getText()
            );

            actualizarTablasDesdeSistema();

            mostrarAlerta("Visitante registrado", "El visitante fue asociado al residente seleccionado.");
        }
    }

    private void mostrarFormularioCrearReclamo() {
        Residente residenteSeleccionado = tablaResidentes.getSelectionModel().getSelectedItem();

        if (residenteSeleccionado == null) {
            mostrarAlerta("Seleccioná un residente", "Primero seleccioná un residente para crearle un reclamo.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Nuevo reclamo");
        dialog.setHeaderText("Crear reclamo para: "
                + residenteSeleccionado.getNombre()
                + " "
                + residenteSeleccionado.getApellido());

        TextField campoDescripcion = new TextField();
        campoDescripcion.setPromptText("Descripción del reclamo");

        ComboBox<String> comboPrioridad = new ComboBox<>();
        comboPrioridad.getItems().addAll("Baja", "Media", "Alta");
        comboPrioridad.setValue("Media");

        TextField campoTipo = new TextField();
        campoTipo.setPromptText("Tipo de reclamo");

        GridPane grid = crearGridFormulario();

        grid.add(new Label("Descripción:"), 0, 0);
        grid.add(campoDescripcion, 1, 0);

        grid.add(new Label("Prioridad:"), 0, 1);
        grid.add(comboPrioridad, 1, 1);

        grid.add(new Label("Tipo:"), 0, 2);
        grid.add(campoTipo, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> resultado = dialog.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            if (campoDescripcion.getText().isBlank()) {
                mostrarAlerta("Datos incompletos", "La descripción del reclamo es obligatoria.");
                return;
            }

            String nombreResidente = residenteSeleccionado.getNombre() + " " + residenteSeleccionado.getApellido();

            sistemaBarrio.crearReclamo(
                    nombreResidente,
                    campoDescripcion.getText(),
                    comboPrioridad.getValue(),
                    campoTipo.getText().isBlank() ? "General" : campoTipo.getText()
            );

            actualizarTablasDesdeSistema();

            mostrarAlerta("Reclamo creado", "El reclamo fue creado correctamente en memoria.");
        }
    }

    private void registrarAccesoVisitanteSeleccionado() {
        Visitante visitanteSeleccionado = tablaVisitantes.getSelectionModel().getSelectedItem();

        if (visitanteSeleccionado == null) {
            mostrarAlerta("Seleccioná un visitante", "Primero seleccioná un visitante de la tabla.");
            return;
        }

        sistemaBarrio.registrarAcceso(
                visitanteSeleccionado.getNombre(),
                visitanteSeleccionado.getDni()
        );

        actualizarTablasDesdeSistema();

        mostrarAlerta("Acceso registrado", "Se registró el ingreso de "
                + visitanteSeleccionado.getNombre()
                + ".");
    }

    private void avanzarEstadoSolicitudSeleccionada() {
        Solicitud solicitudSeleccionada = tablaSolicitudes.getSelectionModel().getSelectedItem();

        if (solicitudSeleccionada == null) {
            mostrarAlerta("Seleccioná una solicitud", "Primero seleccioná una solicitud de la tabla.");
            return;
        }

        sistemaBarrio.avanzarEstadoSolicitud(solicitudSeleccionada);

        actualizarTablasDesdeSistema();

        mostrarAlerta("Estado actualizado", "La solicitud avanzó de estado correctamente.");
    }

    private void actualizarTablasDesdeSistema() {
        residentes.setAll(sistemaBarrio.getResidentes());
        visitantes.setAll(sistemaBarrio.getVisitantes());
        solicitudes.setAll(sistemaBarrio.getSolicitudes());
        accesos.setAll(sistemaBarrio.getAccesos());

        if (tablaResidentes != null) tablaResidentes.refresh();
        if (tablaVisitantes != null) tablaVisitantes.refresh();
        if (tablaSolicitudes != null) tablaSolicitudes.refresh();
        if (tablaAccesos != null) tablaAccesos.refresh();
    }

    private GridPane crearGridFormulario() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));
        return grid;
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void cargarDatosDePrueba() {
        barrioPrincipal = sistemaBarrio.registrarBarrio("eBarrio Norte", "Av. Central 1000");

        Vivienda vivienda1 = sistemaBarrio.registrarVivienda(
                barrioPrincipal,
                "Lote 12",
                "Calle Roble 120"
        );

        Vivienda vivienda2 = sistemaBarrio.registrarVivienda(
                barrioPrincipal,
                "Lote 18",
                "Calle Lago 85"
        );

        Residente residente1 = sistemaBarrio.registrarResidente(
                "Sofía",
                "Gómez",
                "40111222",
                "sofia@email.com",
                "1130000000",
                vivienda1
        );

        Residente residente2 = sistemaBarrio.registrarResidente(
                "Martín",
                "Pérez",
                "38999888",
                "martin@email.com",
                "1140000000",
                vivienda2
        );

        sistemaBarrio.registrarVisitante(
                residente1,
                "Camila Ruiz",
                "42123123",
                "AB123CD",
                "Visita familiar"
        );

        sistemaBarrio.registrarVisitante(
                residente2,
                "Juan Torres",
                "38111222",
                "AC456EF",
                "Reunión con residente"
        );

        actualizarTablasDesdeSistema();
    }

    public static void main(String[] args) {
        launch(args);
    }
}