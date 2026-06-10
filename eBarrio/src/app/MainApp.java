package com.ebarrio.app;

import com.ebarrio.model.Barrio;
import com.ebarrio.model.Residente;
import com.ebarrio.model.Visitante;
import com.ebarrio.model.Vivienda;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MainApp extends Application {

    private ObservableList<Residente> residentes = FXCollections.observableArrayList();
    private ObservableList<Visitante> visitantes = FXCollections.observableArrayList();

    @Override
    public void start(Stage stage) {
        cargarDatosDePrueba();

        BorderPane root = new BorderPane();
        root.getStyleClass().add("root-layout");

        VBox sidebar = crearMenuLateral();
        VBox contenido = crearPantallaResidentesVisitantes();

        root.setLeft(sidebar);
        root.setCenter(contenido);

        Scene scene = new Scene(root, 1000, 620);
        scene.getStylesheets().add(getClass().getResource("/com/ebarrio/styles/styles.css").toExternalForm());

        stage.setTitle("eBarrio - Gestión de Barrio Cerrado");
        stage.setScene(scene);
        stage.show();
    }

    private VBox crearMenuLateral() {
        Label logo = new Label("eBarrio");
        logo.getStyleClass().add("logo");

        Label subtitulo = new Label("Gestión segura del barrio");
        subtitulo.getStyleClass().add("subtitle");

        Button btnResidentes = new Button("Residentes");
        Button btnVisitantes = new Button("Visitantes");
        Button btnReclamos = new Button("Reclamos");
        Button btnAccesos = new Button("Accesos");

        btnResidentes.getStyleClass().add("menu-button");
        btnVisitantes.getStyleClass().add("menu-button");
        btnReclamos.getStyleClass().add("menu-button");
        btnAccesos.getStyleClass().add("menu-button");

        VBox sidebar = new VBox(15, logo, subtitulo, btnResidentes, btnVisitantes, btnReclamos, btnAccesos);
        sidebar.setPadding(new Insets(30));
        sidebar.setPrefWidth(250);
        sidebar.getStyleClass().add("sidebar");
        return sidebar;
    }

    private VBox crearPantallaResidentesVisitantes() {
        Label titulo = new Label("Panel inicial");
        titulo.getStyleClass().add("title");

        Label descripcion = new Label("Primer avance visual del sistema eBarrio. Módulo de residentes, viviendas y visitantes.");
        descripcion.getStyleClass().add("description");

        TableView<Residente> tablaResidentes = crearTablaResidentes();
        TableView<Visitante> tablaVisitantes = crearTablaVisitantes();

        Button agregarResidente = new Button("Agregar residente");
        Button registrarVisitante = new Button("Registrar visitante");
        agregarResidente.getStyleClass().add("primary-button");
        registrarVisitante.getStyleClass().add("secondary-button");

        HBox botones = new HBox(12, agregarResidente, registrarVisitante);
        botones.setAlignment(Pos.CENTER_LEFT);

        VBox cardResidentes = new VBox(10, new Label("Residentes"), tablaResidentes);
        cardResidentes.getStyleClass().add("card");

        VBox cardVisitantes = new VBox(10, new Label("Visitantes autorizados"), tablaVisitantes);
        cardVisitantes.getStyleClass().add("card");

        HBox tablas = new HBox(20, cardResidentes, cardVisitantes);
        HBox.setHgrow(cardResidentes, Priority.ALWAYS);
        HBox.setHgrow(cardVisitantes, Priority.ALWAYS);

        VBox contenido = new VBox(18, titulo, descripcion, botones, tablas);
        contenido.setPadding(new Insets(35));
        return contenido;
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
        tabla.setPrefHeight(300);
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
        tabla.setPrefHeight(300);
        return tabla;
    }

    private void cargarDatosDePrueba() {
        Barrio barrio = new Barrio(1, "eBarrio Norte", "Av. Central 1000");
        Vivienda vivienda1 = new Vivienda(1, "Lote 12", "Calle Roble 120");
        Vivienda vivienda2 = new Vivienda(2, "Lote 18", "Calle Lago 85");

        Residente residente1 = new Residente(1, "Sofía", "Gómez", "40111222", "sofia@email.com", "1130000000");
        Residente residente2 = new Residente(2, "Martín", "Pérez", "38999888", "martin@email.com", "1140000000");

        Visitante visitante1 = new Visitante(1, "Camila Ruiz", "42123123", "AB123CD", "Visita familiar");
        Visitante visitante2 = new Visitante(2, "Juan Torres", "38111222", "AC456EF", "Reunión con residente");

        vivienda1.agregarResidente(residente1);
        vivienda2.agregarResidente(residente2);
        barrio.agregarVivienda(vivienda1);
        barrio.agregarVivienda(vivienda2);

        residente1.registrarVisitante(visitante1);
        residente2.registrarVisitante(visitante2);

        residentes.addAll(residente1, residente2);
        visitantes.addAll(visitante1, visitante2);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
