package app;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import model.Administrador;
import model.accesos.Acceso;
import model.accesos.Visitante;
import model.barrio.Barrio;
import model.barrio.Residente;
import model.barrio.Vivienda;
import model.solicitud.Solicitud;
import sistema.SistemaBarrio;

import java.net.URL;
import java.util.Optional;

public class MainApp extends Application {

    private final SistemaBarrio sistemaBarrio = new SistemaBarrio();

    private final ObservableList<Residente> residentes = FXCollections.observableArrayList();
    private final ObservableList<Visitante> visitantes = FXCollections.observableArrayList();
    private final ObservableList<Solicitud> solicitudes = FXCollections.observableArrayList();
    private final ObservableList<Acceso> accesos = FXCollections.observableArrayList();

    private BorderPane root;
    private VBox sidebar;
    private Barrio barrioPrincipal;

    private TableView<Residente> tablaResidentes;
    private TableView<Visitante> tablaVisitantes;
    private TableView<Solicitud> tablaSolicitudes;
    private TableView<Acceso> tablaAccesos;

    @Override
    public void start(Stage stage) {
        cargarDatosDePrueba();

        root = new BorderPane();
        root.getStyleClass().add("root-layout");
        root.setTop(crearBarraSuperior());
        root.setLeft(crearMenuLateral());
        mostrarInicio();

        Scene scene = new Scene(root, 1240, 760);
        cargarEstilos(scene);

        stage.setTitle("eBarrio - Gestion de Barrios Cerrados");
        stage.setMinWidth(1120);
        stage.setMinHeight(700);
        stage.setScene(scene);
        stage.show();
    }

    private void cargarEstilos(Scene scene) {
        URL css = getClass().getResource("/styles/styles.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }
    }

    private HBox crearBarraSuperior() {
        Label menu = new Label("=");
        menu.getStyleClass().add("top-icon");

        Label marca = new Label("eBarrio");
        marca.getStyleClass().add("top-brand");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label alerta = new Label("Notificaciones: " + sistemaBarrio.getNotificaciones().size());
        alerta.getStyleClass().add("top-pill");

        Label usuario = new Label("Administrador");
        usuario.getStyleClass().add("top-user");

        HBox top = new HBox(14, menu, marca, spacer, alerta, usuario);
        top.setAlignment(Pos.CENTER_LEFT);
        top.getStyleClass().add("topbar");
        return top;
    }

    private VBox crearMenuLateral() {
        Label logoMark = new Label("eB");
        logoMark.getStyleClass().add("logo-mark");

        Label logo = new Label("eBarrio");
        logo.getStyleClass().add("logo");

        Label subtitulo = new Label("Gestion de barrios cerrados");
        subtitulo.getStyleClass().add("subtitle");

        Button inicio = crearBotonMenu("Inicio");
        Button residentesBtn = crearBotonMenu("Residentes");
        Button visitantesBtn = crearBotonMenu("Visitantes");
        Button accesosBtn = crearBotonMenu("Accesos");
        Button reclamosBtn = crearBotonMenu("Solicitudes");
        Button nuevoReclamoBtn = crearBotonMenu("Nuevo reclamo");

        inicio.setOnAction(e -> mostrarInicio());
        residentesBtn.setOnAction(e -> mostrarResidentes());
        visitantesBtn.setOnAction(e -> mostrarVisitantes());
        accesosBtn.setOnAction(e -> mostrarAccesos());
        reclamosBtn.setOnAction(e -> mostrarSolicitudes());
        nuevoReclamoBtn.setOnAction(e -> mostrarFormularioCrearReclamo());

        sidebar = new VBox(
                10,
                logoMark,
                logo,
                subtitulo,
                inicio,
                residentesBtn,
                visitantesBtn,
                accesosBtn,
                reclamosBtn,
                nuevoReclamoBtn
        );
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPadding(new Insets(26, 18, 26, 18));
        sidebar.setPrefWidth(235);
        return sidebar;
    }

    private Button crearBotonMenu(String texto) {
        Button boton = new Button(texto);
        boton.getStyleClass().add("menu-button");
        boton.setMaxWidth(Double.MAX_VALUE);
        return boton;
    }

    private void mostrarInicio() {
        actualizarTablasDesdeSistema();

        VBox contenido = crearContenidoBase("Bienvenido, Administrador", "Resumen general del barrio");

        HBox metricas = new HBox(14,
                crearMetrica("Residentes", String.valueOf(residentes.size())),
                crearMetrica("Viviendas", String.valueOf(sistemaBarrio.getViviendas().size())),
                crearMetrica("Visitantes", String.valueOf(visitantes.size())),
                crearMetrica("Solicitudes", String.valueOf(solicitudes.size()))
        );
        metricas.getStyleClass().add("metric-row");

        tablaSolicitudes = crearTablaSolicitudes();
        tablaAccesos = crearTablaAccesos();

        VBox reclamosCard = crearCardConAccion("Solicitudes recientes", "Ver todas", this::mostrarSolicitudes, tablaSolicitudes);
        VBox accesosCard = crearCardConAccion("Accesos de hoy", "Ver todos", this::mostrarAccesos, tablaAccesos);

        HBox filaTablas = new HBox(16, reclamosCard, accesosCard);
        HBox.setHgrow(reclamosCard, Priority.ALWAYS);
        HBox.setHgrow(accesosCard, Priority.ALWAYS);

        VBox tareas = crearResumenOperativo();

        contenido.getChildren().addAll(metricas, filaTablas, tareas);
        root.setCenter(crearScroll(contenido));
    }

    private VBox crearResumenOperativo() {
        int pendientes = contarSolicitudesPorEstado("Pendiente");
        int enProceso = contarSolicitudesPorEstado("En proceso");
        int resueltas = contarSolicitudesPorEstado("Resuelto") + contarSolicitudesPorEstado("Cerrado");

        HBox resumen = new HBox(
                44,
                crearMiniDato("Pendientes", String.valueOf(pendientes)),
                crearMiniDato("En proceso", String.valueOf(enProceso)),
                crearMiniDato("Resueltas o cerradas", String.valueOf(resueltas)),
                crearMiniDato("Accesos activos", String.valueOf(contarAccesosActivos()))
        );
        resumen.setAlignment(Pos.CENTER_LEFT);

        VBox card = new VBox(18, crearTituloCard("Actividad operativa"), resumen);
        card.getStyleClass().add("card");
        return card;
    }

    private int contarSolicitudesPorEstado(String estado) {
        int total = 0;
        for (Solicitud solicitud : sistemaBarrio.getSolicitudes()) {
            if (solicitud.getEstado().mostrarNombreEstado().equalsIgnoreCase(estado)) {
                total++;
            }
        }
        return total;
    }

    private int contarAccesosActivos() {
        int total = 0;
        for (Acceso acceso : sistemaBarrio.getAccesos()) {
            if (acceso.estaActivo()) {
                total++;
            }
        }
        return total;
    }

    private void mostrarResidentes() {
        actualizarTablasDesdeSistema();
        tablaResidentes = crearTablaResidentes();

        VBox contenido = crearContenidoBase("Residentes", "Alta y consulta de residentes del barrio");
        HBox acciones = new HBox(10, crearBotonPrimario("Nuevo residente", this::mostrarFormularioAgregarResidente));
        contenido.getChildren().addAll(acciones, crearCard("Listado de residentes", tablaResidentes));
        root.setCenter(crearScroll(contenido));
    }

    private void mostrarVisitantes() {
        actualizarTablasDesdeSistema();
        tablaVisitantes = crearTablaVisitantes();

        VBox contenido = crearContenidoBase("Visitantes", "Visitantes autorizados por residentes");
        HBox acciones = new HBox(10, crearBotonPrimario("Nuevo visitante", this::mostrarFormularioRegistrarVisitante));
        contenido.getChildren().addAll(acciones, crearCard("Listado de visitantes", tablaVisitantes));
        root.setCenter(crearScroll(contenido));
    }

    private void mostrarAccesos() {
        actualizarTablasDesdeSistema();
        tablaAccesos = crearTablaAccesos();

        VBox contenido = crearContenidoBase("Accesos", "Ingresos y egresos registrados");
        HBox acciones = new HBox(
                10,
                crearBotonPrimario("Registrar ingreso", this::registrarAccesoVisitanteSeleccionado),
                crearBotonSecundario("Registrar egreso", this::registrarEgresoAccesoSeleccionado)
        );
        contenido.getChildren().addAll(acciones, crearCard("Control de accesos", tablaAccesos));
        root.setCenter(crearScroll(contenido));
    }

    private void mostrarSolicitudes() {
        actualizarTablasDesdeSistema();
        tablaSolicitudes = crearTablaSolicitudes();

        VBox contenido = crearContenidoBase("Solicitudes", "Reclamos, tareas e incidentes");
        HBox acciones = new HBox(
                10,
                crearBotonPrimario("Nuevo reclamo", this::mostrarFormularioCrearReclamo),
                crearBotonSecundario("Nueva tarea", this::mostrarFormularioCrearTarea),
                crearBotonSecundario("Nuevo incidente", this::mostrarFormularioCrearIncidente),
                crearBotonSecundario("Avanzar estado", this::avanzarEstadoSolicitudSeleccionada),
                crearBotonSecundario("Cancelar", this::cancelarSolicitudSeleccionada)
        );
        contenido.getChildren().addAll(acciones, crearCard("Seguimiento de solicitudes", tablaSolicitudes));
        root.setCenter(crearScroll(contenido));
    }

    private VBox crearContenidoBase(String titulo, String descripcion) {
        Label tituloVista = new Label(titulo);
        tituloVista.getStyleClass().add("title");

        Label bajada = new Label(descripcion);
        bajada.getStyleClass().add("description");

        VBox contenido = new VBox(18, tituloVista, bajada);
        contenido.setPadding(new Insets(30));
        contenido.getStyleClass().add("content");
        return contenido;
    }

    private ScrollPane crearScroll(Node contenido) {
        ScrollPane scroll = new ScrollPane(contenido);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("content-scroll");
        return scroll;
    }

    private VBox crearMetrica(String etiqueta, String valor) {
        Label numero = new Label(valor);
        numero.getStyleClass().add("metric-number");

        Label texto = new Label(etiqueta);
        texto.getStyleClass().add("metric-label");

        VBox card = new VBox(4, numero, texto);
        card.setAlignment(Pos.CENTER_LEFT);
        card.getStyleClass().add("metric-card");
        HBox.setHgrow(card, Priority.ALWAYS);
        return card;
    }

    private VBox crearMiniDato(String etiqueta, String valor) {
        Label numero = new Label(valor);
        numero.getStyleClass().add("mini-number");

        Label texto = new Label(etiqueta);
        texto.getStyleClass().add("mini-label");

        VBox box = new VBox(4, numero, texto);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private Label crearTituloCard(String texto) {
        Label titulo = new Label(texto);
        titulo.getStyleClass().add("card-title");
        return titulo;
    }

    private VBox crearCard(String titulo, Node contenido) {
        VBox card = new VBox(12, crearTituloCard(titulo), contenido);
        card.getStyleClass().add("card");
        return card;
    }

    private VBox crearCardConAccion(String titulo, String accion, Runnable runnable, Node contenido) {
        Label label = crearTituloCard(titulo);
        Button boton = new Button(accion);
        boton.getStyleClass().add("link-button");
        boton.setOnAction(e -> runnable.run());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox header = new HBox(label, spacer, boton);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox card = new VBox(12, header, contenido);
        card.getStyleClass().add("card");
        return card;
    }

    private Button crearBotonPrimario(String texto, Runnable accion) {
        Button boton = new Button(texto);
        boton.getStyleClass().add("primary-button");
        boton.setOnAction(e -> accion.run());
        return boton;
    }

    private Button crearBotonSecundario(String texto, Runnable accion) {
        Button boton = new Button(texto);
        boton.getStyleClass().add("secondary-button");
        boton.setOnAction(e -> accion.run());
        return boton;
    }

    private TableView<Residente> crearTablaResidentes() {
        TableView<Residente> tabla = new TableView<>();
        tabla.setItems(residentes);
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Residente, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(data -> new ReadOnlyStringWrapper(
                data.getValue().getNombre() + " " + data.getValue().getApellido()
        ));

        TableColumn<Residente, String> colDni = new TableColumn<>("DNI");
        colDni.setCellValueFactory(new PropertyValueFactory<>("dni"));

        TableColumn<Residente, String> colVivienda = new TableColumn<>("Vivienda");
        colVivienda.setCellValueFactory(data -> {
            Vivienda vivienda = data.getValue().getVivienda();
            return new ReadOnlyStringWrapper(vivienda == null ? "Sin vivienda" : vivienda.getLote());
        });

        TableColumn<Residente, String> colTelefono = new TableColumn<>("Telefono");
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));

        tabla.getColumns().addAll(colNombre, colDni, colVivienda, colTelefono);
        tabla.setPrefHeight(420);
        return tabla;
    }

    private TableView<Visitante> crearTablaVisitantes() {
        TableView<Visitante> tabla = new TableView<>();
        tabla.setItems(visitantes);
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Visitante, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        TableColumn<Visitante, String> colDni = new TableColumn<>("DNI");
        colDni.setCellValueFactory(new PropertyValueFactory<>("dni"));

        TableColumn<Visitante, String> colPatente = new TableColumn<>("Patente");
        colPatente.setCellValueFactory(new PropertyValueFactory<>("patente"));

        TableColumn<Visitante, String> colMotivo = new TableColumn<>("Motivo");
        colMotivo.setCellValueFactory(new PropertyValueFactory<>("motivoVisita"));

        tabla.getColumns().addAll(colNombre, colDni, colPatente, colMotivo);
        tabla.setPrefHeight(420);
        return tabla;
    }

    private TableView<Solicitud> crearTablaSolicitudes() {
        TableView<Solicitud> tabla = new TableView<>();
        tabla.setItems(solicitudes);
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Solicitud, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(data -> new ReadOnlyStringWrapper("#" + data.getValue().getId()));

        TableColumn<Solicitud, String> colDescripcion = new TableColumn<>("Descripcion");
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        TableColumn<Solicitud, String> colPrioridad = new TableColumn<>("Prioridad");
        colPrioridad.setCellValueFactory(new PropertyValueFactory<>("prioridad"));

        TableColumn<Solicitud, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(data -> new ReadOnlyStringWrapper(
                data.getValue().getEstado().mostrarNombreEstado()
        ));

        TableColumn<Solicitud, String> colFecha = new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getFechaCreacion().toString()));

        tabla.getColumns().addAll(colId, colDescripcion, colPrioridad, colEstado, colFecha);
        tabla.setPrefHeight(330);
        return tabla;
    }

    private TableView<Acceso> crearTablaAccesos() {
        TableView<Acceso> tabla = new TableView<>();
        tabla.setItems(accesos);
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Acceso, String> colHora = new TableColumn<>("Ingreso");
        colHora.setCellValueFactory(data -> new ReadOnlyStringWrapper(
                data.getValue().getFechaIngreso() == null ? "-" : data.getValue().getFechaIngreso().toLocalTime().withNano(0).toString()
        ));

        TableColumn<Acceso, String> colVisitante = new TableColumn<>("Visitante");
        colVisitante.setCellValueFactory(new PropertyValueFactory<>("nombreVisitante"));

        TableColumn<Acceso, String> colDni = new TableColumn<>("DNI");
        colDni.setCellValueFactory(new PropertyValueFactory<>("dniVisitante"));

        TableColumn<Acceso, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().estaActivo() ? "Activo" : "Finalizado"));

        tabla.getColumns().addAll(colHora, colVisitante, colDni, colEstado);
        tabla.setPrefHeight(330);
        return tabla;
    }

    private void mostrarFormularioAgregarResidente() {
        Dialog<ButtonType> dialog = crearDialogo("Agregar residente", "Cargar nuevo residente");

        TextField nombre = campo("Nombre");
        TextField apellido = campo("Apellido");
        TextField dni = campo("DNI");
        TextField email = campo("Email");
        TextField telefono = campo("Telefono");
        TextField lote = campo("Lote / vivienda");

        GridPane grid = crearGridFormulario();
        agregarFila(grid, 0, "Nombre", nombre);
        agregarFila(grid, 1, "Apellido", apellido);
        agregarFila(grid, 2, "DNI", dni);
        agregarFila(grid, 3, "Email", email);
        agregarFila(grid, 4, "Telefono", telefono);
        agregarFila(grid, 5, "Lote", lote);

        dialog.getDialogPane().setContent(grid);
        Optional<ButtonType> resultado = dialog.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            if (nombre.getText().isBlank() || apellido.getText().isBlank() || dni.getText().isBlank()) {
                mostrarAlerta("Datos incompletos", "Nombre, apellido y DNI son obligatorios.");
                return;
            }

            Vivienda vivienda = sistemaBarrio.registrarVivienda(
                    barrioPrincipal,
                    textoOValor(lote, "Sin lote"),
                    "Sin direccion cargada"
            );
            sistemaBarrio.registrarResidente(
                    nombre.getText(),
                    apellido.getText(),
                    dni.getText(),
                    email.getText(),
                    telefono.getText(),
                    vivienda
            );
            actualizarYVolverAResidentes();
        }
    }

    private void mostrarFormularioRegistrarVisitante() {
        Residente residente = obtenerResidenteSeleccionado();
        if (residente == null) {
            mostrarAlerta("Seleccion requerida", "Selecciona un residente para asociar el visitante.");
            return;
        }

        Dialog<ButtonType> dialog = crearDialogo("Registrar visitante", "Visitante para " + residente.getNombre() + " " + residente.getApellido());

        TextField nombre = campo("Nombre del visitante");
        TextField dni = campo("DNI");
        TextField patente = campo("Patente");
        TextField motivo = campo("Motivo de visita");

        GridPane grid = crearGridFormulario();
        agregarFila(grid, 0, "Nombre", nombre);
        agregarFila(grid, 1, "DNI", dni);
        agregarFila(grid, 2, "Patente", patente);
        agregarFila(grid, 3, "Motivo", motivo);

        dialog.getDialogPane().setContent(grid);
        Optional<ButtonType> resultado = dialog.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            if (nombre.getText().isBlank() || dni.getText().isBlank()) {
                mostrarAlerta("Datos incompletos", "Nombre y DNI del visitante son obligatorios.");
                return;
            }

            sistemaBarrio.registrarVisitante(residente, nombre.getText(), dni.getText(), patente.getText(), motivo.getText());
            actualizarYVolverAVisitantes();
        }
    }

    private void mostrarFormularioCrearReclamo() {
        Residente residente = obtenerResidenteSeleccionado();
        if (residente == null) {
            mostrarAlerta("Seleccion requerida", "Selecciona un residente para cargarle un reclamo.");
            return;
        }

        Dialog<ButtonType> dialog = crearDialogo("Nuevo reclamo", "Crear reclamo para " + residente.getNombre() + " " + residente.getApellido());

        TextArea descripcion = area("Descripcion del reclamo");
        ComboBox<String> prioridad = combo("Media", "Baja", "Media", "Alta");
        TextField tipo = campo("Tipo de reclamo");

        GridPane grid = crearGridFormulario();
        agregarFila(grid, 0, "Descripcion", descripcion);
        agregarFila(grid, 1, "Prioridad", prioridad);
        agregarFila(grid, 2, "Tipo", tipo);

        dialog.getDialogPane().setContent(grid);
        Optional<ButtonType> resultado = dialog.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            if (descripcion.getText().isBlank()) {
                mostrarAlerta("Datos incompletos", "La descripcion es obligatoria.");
                return;
            }

            sistemaBarrio.crearReclamo(
                    residente.getNombre() + " " + residente.getApellido(),
                    descripcion.getText(),
                    prioridad.getValue(),
                    textoOValor(tipo, "General")
            );
            actualizarYVolverASolicitudes();
        }
    }

    private void mostrarFormularioCrearTarea() {
        Dialog<ButtonType> dialog = crearDialogo("Nueva tarea", "Crear tarea de mantenimiento");

        TextArea descripcion = area("Descripcion de la tarea");
        ComboBox<String> prioridad = combo("Media", "Baja", "Media", "Alta");
        TextField sector = campo("Sector");

        GridPane grid = crearGridFormulario();
        agregarFila(grid, 0, "Descripcion", descripcion);
        agregarFila(grid, 1, "Prioridad", prioridad);
        agregarFila(grid, 2, "Sector", sector);

        dialog.getDialogPane().setContent(grid);
        Optional<ButtonType> resultado = dialog.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            if (descripcion.getText().isBlank()) {
                mostrarAlerta("Datos incompletos", "La descripcion es obligatoria.");
                return;
            }

            sistemaBarrio.crearTareaMantenimiento("Mantenimiento", descripcion.getText(), prioridad.getValue(), textoOValor(sector, "General"));
            actualizarYVolverASolicitudes();
        }
    }

    private void mostrarFormularioCrearIncidente() {
        Dialog<ButtonType> dialog = crearDialogo("Nuevo incidente", "Registrar incidente de seguridad");

        TextArea descripcion = area("Descripcion del incidente");
        ComboBox<String> prioridad = combo("Alta", "Baja", "Media", "Alta");
        ComboBox<String> riesgo = combo("Medio", "Bajo", "Medio", "Alto");

        GridPane grid = crearGridFormulario();
        agregarFila(grid, 0, "Descripcion", descripcion);
        agregarFila(grid, 1, "Prioridad", prioridad);
        agregarFila(grid, 2, "Riesgo", riesgo);

        dialog.getDialogPane().setContent(grid);
        Optional<ButtonType> resultado = dialog.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            if (descripcion.getText().isBlank()) {
                mostrarAlerta("Datos incompletos", "La descripcion es obligatoria.");
                return;
            }

            sistemaBarrio.crearIncidenteSeguridad("Seguridad", descripcion.getText(), prioridad.getValue(), riesgo.getValue());
            actualizarYVolverASolicitudes();
        }
    }

    private void registrarAccesoVisitanteSeleccionado() {
        Visitante visitante = obtenerVisitanteSeleccionado();
        if (visitante == null) {
            mostrarAlerta("Seleccion requerida", "Selecciona un visitante para registrar el ingreso.");
            return;
        }

        sistemaBarrio.registrarAcceso(visitante.getNombre(), visitante.getDni());
        actualizarYVolverAAccesos();
    }

    private void registrarEgresoAccesoSeleccionado() {
        Acceso acceso = tablaAccesos == null ? null : tablaAccesos.getSelectionModel().getSelectedItem();
        if (acceso == null) {
            mostrarAlerta("Seleccion requerida", "Selecciona un acceso activo para registrar el egreso.");
            return;
        }

        sistemaBarrio.registrarEgresoAcceso(acceso);
        actualizarYVolverAAccesos();
    }

    private void avanzarEstadoSolicitudSeleccionada() {
        Solicitud solicitud = obtenerSolicitudSeleccionada();
        if (solicitud == null) {
            mostrarAlerta("Seleccion requerida", "Selecciona una solicitud para avanzar su estado.");
            return;
        }

        sistemaBarrio.avanzarEstadoSolicitud(solicitud);
        actualizarYVolverASolicitudes();
    }

    private void cancelarSolicitudSeleccionada() {
        Solicitud solicitud = obtenerSolicitudSeleccionada();
        if (solicitud == null) {
            mostrarAlerta("Seleccion requerida", "Selecciona una solicitud para cancelarla.");
            return;
        }

        sistemaBarrio.cancelarSolicitud(solicitud);
        actualizarYVolverASolicitudes();
    }

    private Residente obtenerResidenteSeleccionado() {
        return tablaResidentes == null ? null : tablaResidentes.getSelectionModel().getSelectedItem();
    }

    private Visitante obtenerVisitanteSeleccionado() {
        return tablaVisitantes == null ? null : tablaVisitantes.getSelectionModel().getSelectedItem();
    }

    private Solicitud obtenerSolicitudSeleccionada() {
        return tablaSolicitudes == null ? null : tablaSolicitudes.getSelectionModel().getSelectedItem();
    }

    private void actualizarYVolverAResidentes() {
        actualizarTablasDesdeSistema();
        mostrarResidentes();
    }

    private void actualizarYVolverAVisitantes() {
        actualizarTablasDesdeSistema();
        mostrarVisitantes();
    }

    private void actualizarYVolverAAccesos() {
        actualizarTablasDesdeSistema();
        mostrarAccesos();
    }

    private void actualizarYVolverASolicitudes() {
        actualizarTablasDesdeSistema();
        mostrarSolicitudes();
    }

    private void actualizarTablasDesdeSistema() {
        residentes.setAll(sistemaBarrio.getResidentes());
        visitantes.setAll(sistemaBarrio.getVisitantes());
        solicitudes.setAll(sistemaBarrio.getSolicitudes());
        accesos.setAll(sistemaBarrio.getAccesos());

        if (root != null) {
            root.setTop(crearBarraSuperior());
        }
    }

    private Dialog<ButtonType> crearDialogo(String titulo, String header) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(titulo);
        dialog.setHeaderText(header);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.getDialogPane().getStyleClass().add("dialog-pane");
        return dialog;
    }

    private GridPane crearGridFormulario() {
        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(12);
        grid.setPadding(new Insets(18));
        return grid;
    }

    private void agregarFila(GridPane grid, int fila, String etiqueta, Node campo) {
        Label label = new Label(etiqueta);
        label.getStyleClass().add("form-label");
        grid.add(label, 0, fila);
        grid.add(campo, 1, fila);
        GridPane.setHgrow(campo, Priority.ALWAYS);
    }

    private TextField campo(String prompt) {
        TextField campo = new TextField();
        campo.setPromptText(prompt);
        campo.setPrefWidth(360);
        return campo;
    }

    private TextArea area(String prompt) {
        TextArea area = new TextArea();
        area.setPromptText(prompt);
        area.setPrefWidth(360);
        area.setPrefRowCount(4);
        area.setWrapText(true);
        return area;
    }

    private ComboBox<String> combo(String valorInicial, String... valores) {
        ComboBox<String> combo = new ComboBox<>();
        combo.getItems().addAll(valores);
        combo.setValue(valorInicial);
        combo.setMaxWidth(Double.MAX_VALUE);
        return combo;
    }

    private String textoOValor(TextField campo, String valorPorDefecto) {
        return campo.getText().isBlank() ? valorPorDefecto : campo.getText();
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
        sistemaBarrio.registrarObservadorGlobal(new Administrador(1, "Maria Garcia", "maria@ebarrio.com"));

        Vivienda vivienda1 = sistemaBarrio.registrarVivienda(barrioPrincipal, "Lote 12", "Calle Roble 120");
        Vivienda vivienda2 = sistemaBarrio.registrarVivienda(barrioPrincipal, "Lote 18", "Calle Lago 85");
        Vivienda vivienda3 = sistemaBarrio.registrarVivienda(barrioPrincipal, "Lote 23", "Calle Los Pinos 41");

        Residente residente1 = sistemaBarrio.registrarResidente("Sofia", "Gomez", "40111222", "sofia@email.com", "1130000000", vivienda1);
        Residente residente2 = sistemaBarrio.registrarResidente("Martin", "Perez", "38999888", "martin@email.com", "1140000000", vivienda2);
        Residente residente3 = sistemaBarrio.registrarResidente("Ana", "Torres", "36777888", "ana@email.com", "1150000000", vivienda3);

        sistemaBarrio.registrarVisitante(residente1, "Camila Ruiz", "42123123", "AB123CD", "Visita familiar");
        sistemaBarrio.registrarVisitante(residente2, "Juan Torres", "38111222", "AC456EF", "Reunion con residente");
        sistemaBarrio.registrarVisitante(residente3, "Carlos Ruiz", "34999888", "EF456GH", "Proveedor autorizado");

        sistemaBarrio.crearReclamo("Sofia Gomez", "Luminaria quemada en calle Los Pinos", "Alta", "Infraestructura");
        sistemaBarrio.crearReclamo("Martin Perez", "Perdida de agua en vereda", "Media", "Agua");
        sistemaBarrio.crearTareaMantenimiento("Mantenimiento", "Cortar pasto del parque central", "Media", "Parque central");
        sistemaBarrio.crearIncidenteSeguridad("Seguridad", "Persona no identificada en el perimetro", "Alta", "Alto");

        sistemaBarrio.registrarAcceso("Camila Ruiz", "42123123");
        Acceso accesoFinalizado = sistemaBarrio.registrarAcceso("Juan Torres", "38111222");
        sistemaBarrio.registrarEgresoAcceso(accesoFinalizado);

        actualizarTablasDesdeSistema();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
