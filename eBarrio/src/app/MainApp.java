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
import javafx.scene.control.PasswordField;
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

    private enum ModoVista {
        ADMIN,
        RESIDENTE
    }

    private final SistemaBarrio sistemaBarrio = new SistemaBarrio();

    private final ObservableList<Residente> residentes = FXCollections.observableArrayList();
    private final ObservableList<Visitante> visitantes = FXCollections.observableArrayList();
    private final ObservableList<Solicitud> solicitudes = FXCollections.observableArrayList();
    private final ObservableList<Acceso> accesos = FXCollections.observableArrayList();

    private BorderPane root;
    private VBox sidebar;
    private Barrio barrioPrincipal;
    private ModoVista modoActual = ModoVista.ADMIN;
    private Residente residenteActual;
    private SistemaBarrio.UsuarioSistema usuarioActual;

    private TableView<Residente> tablaResidentes;
    private TableView<Visitante> tablaVisitantes;
    private TableView<Solicitud> tablaSolicitudes;
    private TableView<Acceso> tablaAccesos;

    @Override
    public void start(Stage stage) {
        sistemaBarrio.cargarDesdeJson();
        barrioPrincipal = sistemaBarrio.getBarrios().isEmpty() ? null : sistemaBarrio.getBarrios().get(0);
        actualizarTablasDesdeSistema();

        root = new BorderPane();
        root.getStyleClass().add("root-layout");
        root.setCenter(crearLogin());

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
        Label menu = new Label("eB");
        menu.getStyleClass().add("top-icon");

        Label marca = new Label("eBarrio");
        marca.getStyleClass().add("top-brand");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label alerta = new Label("Notificaciones: " + sistemaBarrio.getNotificaciones().size());
        alerta.getStyleClass().add("top-pill");

        Label usuario = new Label(modoActual == ModoVista.ADMIN ? "Administrador" : nombreResidenteActual());
        usuario.getStyleClass().add("top-user");

        Button cerrarSesion = new Button("Cerrar sesion");
        cerrarSesion.getStyleClass().add("view-switch");
        cerrarSesion.setOnAction(e -> cerrarSesion());

        HBox top = new HBox(14, menu, marca, spacer, alerta, usuario, cerrarSesion);
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

        Label modo = new Label(modoActual == ModoVista.ADMIN ? "Panel administrador" : "Panel residente");
        modo.getStyleClass().add("menu-section");

        sidebar = new VBox(10, logoMark, logo, subtitulo, modo);

        if (modoActual == ModoVista.ADMIN) {
            Button inicio = crearBotonMenu("Inicio");
            Button residentesBtn = crearBotonMenu("Residentes");
            Button visitantesBtn = crearBotonMenu("Visitantes");
            Button accesosBtn = crearBotonMenu("Accesos");
            Button reclamosBtn = crearBotonMenu("Solicitudes");

            inicio.setOnAction(e -> mostrarInicio());
            residentesBtn.setOnAction(e -> mostrarResidentes());
            visitantesBtn.setOnAction(e -> mostrarVisitantes());
            accesosBtn.setOnAction(e -> mostrarAccesos());
            reclamosBtn.setOnAction(e -> mostrarSolicitudes());
            sidebar.getChildren().addAll(inicio, residentesBtn, visitantesBtn, accesosBtn, reclamosBtn);
        } else {
            Button inicio = crearBotonMenu("Mi inicio");
            Button visitantesBtn = crearBotonMenu("Mis visitantes");
            Button reclamosBtn = crearBotonMenu("Mis reclamos");
            Button nuevoReclamoBtn = crearBotonMenu("Nuevo reclamo");

            inicio.setOnAction(e -> mostrarInicioResidente());
            visitantesBtn.setOnAction(e -> mostrarMisVisitantes());
            reclamosBtn.setOnAction(e -> mostrarMisSolicitudes());
            nuevoReclamoBtn.setOnAction(e -> mostrarFormularioCrearReclamo());
            sidebar.getChildren().addAll(inicio, visitantesBtn, reclamosBtn, nuevoReclamoBtn);
        }

        sidebar.getStyleClass().add("sidebar");
        sidebar.setPadding(new Insets(26, 18, 26, 18));
        sidebar.setPrefWidth(235);
        return sidebar;
    }

    private void abrirAppSegunSesion() {
        root.setLeft(crearMenuLateral());
        root.setTop(crearBarraSuperior());
        if (modoActual == ModoVista.ADMIN) {
            mostrarInicio();
        } else {
            asegurarResidenteActual();
            mostrarInicioResidente();
        }
    }

    private void cerrarSesion() {
        usuarioActual = null;
        modoActual = ModoVista.ADMIN;
        residenteActual = null;
        root.setTop(null);
        root.setLeft(null);
        root.setCenter(crearLogin());
    }

    private VBox crearLogin() {
        Label marca = new Label("eBarrio");
        marca.getStyleClass().add("login-brand");

        Label titulo = new Label("Iniciar sesion");
        titulo.getStyleClass().add("login-title");

        TextField email = campo("Email");
        PasswordField password = new PasswordField();
        password.setPromptText("Clave");
        password.getStyleClass().add("text-field");
        password.setPrefWidth(360);

        Label ayuda = new Label("Los usuarios se cargan desde src/data/usuarios.json");
        ayuda.getStyleClass().add("login-help");

        Button ingresar = crearBotonPrimario("Ingresar", () -> intentarLogin(email.getText(), password.getText()));
        ingresar.setMaxWidth(Double.MAX_VALUE);

        VBox form = new VBox(14, marca, titulo, email, password, ingresar, ayuda);
        form.getStyleClass().add("login-card");
        form.setAlignment(Pos.CENTER_LEFT);
        form.setMaxWidth(430);

        VBox wrapper = new VBox(form);
        wrapper.getStyleClass().add("login-page");
        wrapper.setAlignment(Pos.CENTER);
        return wrapper;
    }

    private void intentarLogin(String email, String password) {
        SistemaBarrio.UsuarioSistema usuario = sistemaBarrio.autenticarUsuario(email, password);
        if (usuario != null) {
            usuarioActual = usuario;
            modoActual = usuario.esResidente() ? ModoVista.RESIDENTE : ModoVista.ADMIN;
            residenteActual = modoActual == ModoVista.RESIDENTE ? buscarResidentePorId(usuario.getResidenteId()) : null;
            if (modoActual == ModoVista.RESIDENTE && residenteActual == null) {
                mostrarAlerta("Usuario sin residente", "El usuario existe, pero no tiene un residente asociado.");
                return;
            }
            abrirAppSegunSesion();
            return;
        }

        mostrarAlerta("No se pudo iniciar sesion", "Revisa el email y la clave cargados en usuarios.json.");
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

        VBox filaTablas = new VBox(16, reclamosCard, accesosCard);

        VBox tareas = crearResumenOperativo();

        contenido.getChildren().addAll(metricas, filaTablas, tareas);
        root.setCenter(crearScroll(contenido));
    }

    private void mostrarInicioResidente() {
        asegurarResidenteActual();
        actualizarTablasDesdeSistema();

        VBox contenido = crearContenidoBase("Hola, " + residenteActual.getNombre(), "Vista personal de vivienda, visitantes y reclamos");

        Vivienda vivienda = residenteActual.getVivienda();
        HBox metricas = new HBox(14,
                crearMetrica("Vivienda", vivienda == null ? "S/D" : vivienda.getLote()),
                crearMetrica("Visitantes", String.valueOf(residenteActual.getVisitantes().size())),
                crearMetrica("Mis reclamos", String.valueOf(contarSolicitudesDelResidente())),
                crearMetrica("Notificaciones", String.valueOf(sistemaBarrio.getNotificaciones().size()))
        );
        metricas.getStyleClass().add("metric-row");

        Label viviendaTitulo = crearTituloCard("Datos de vivienda");
        VBox datosVivienda = new VBox(
                8,
                crearMiniDato("Lote", vivienda == null ? "Sin vivienda" : vivienda.getLote()),
                crearMiniDato("Direccion", vivienda == null ? "Sin direccion" : vivienda.getDireccion()),
                crearMiniDato("Contacto", residenteActual.getTelefono().isBlank() ? "Sin telefono" : residenteActual.getTelefono())
        );
        VBox viviendaCard = new VBox(14, viviendaTitulo, datosVivienda);
        viviendaCard.getStyleClass().add("card");

        tablaVisitantes = crearTablaVisitantes();
        visitantes.setAll(residenteActual.getVisitantes());

        VBox visitantesCard = crearCardConAccion("Visitantes autorizados", "Agregar", this::mostrarFormularioRegistrarVisitante, tablaVisitantes);
        VBox fila = new VBox(16, viviendaCard, visitantesCard);

        contenido.getChildren().addAll(metricas, fila, crearAccionesResidente());
        root.setCenter(crearScroll(contenido));
    }

    private VBox crearAccionesResidente() {
        Button nuevoVisitante = crearBotonPrimario("Autorizar visitante", this::mostrarFormularioRegistrarVisitante);
        Button nuevoReclamo = crearBotonSecundario("Crear reclamo", this::mostrarFormularioCrearReclamo);
        Button verReclamos = crearBotonSecundario("Ver mis reclamos", this::mostrarMisSolicitudes);
        HBox acciones = new HBox(10, nuevoVisitante, nuevoReclamo, verReclamos);
        acciones.setAlignment(Pos.CENTER_LEFT);
        VBox card = new VBox(14, crearTituloCard("Acciones rapidas"), acciones);
        card.getStyleClass().add("card");
        return card;
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

    private void mostrarMisVisitantes() {
        asegurarResidenteActual();
        tablaVisitantes = crearTablaVisitantes();
        visitantes.setAll(residenteActual.getVisitantes());

        VBox contenido = crearContenidoBase("Mis visitantes", "Personas autorizadas para ingresar a tu vivienda");
        HBox acciones = new HBox(10, crearBotonPrimario("Autorizar visitante", this::mostrarFormularioRegistrarVisitante));
        contenido.getChildren().addAll(acciones, crearCard("Visitantes autorizados", tablaVisitantes));
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

    private void mostrarMisSolicitudes() {
        asegurarResidenteActual();
        tablaSolicitudes = crearTablaSolicitudes();
        solicitudes.setAll(solicitudesDelResidente());

        VBox contenido = crearContenidoBase("Mis reclamos", "Seguimiento de reclamos cargados por tu usuario");
        HBox acciones = new HBox(10, crearBotonPrimario("Nuevo reclamo", this::mostrarFormularioCrearReclamo));
        contenido.getChildren().addAll(acciones, crearCard("Mis solicitudes", tablaSolicitudes));
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
        configurarTabla(tabla);
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
        configurarTabla(tabla);
        tabla.setPrefHeight(420);
        return tabla;
    }

    private TableView<Solicitud> crearTablaSolicitudes() {
        TableView<Solicitud> tabla = new TableView<>();
        tabla.setItems(solicitudes);
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Solicitud, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(data -> new ReadOnlyStringWrapper("#" + data.getValue().getId()));
        colId.setMinWidth(72);

        TableColumn<Solicitud, String> colDescripcion = new TableColumn<>("Descripcion");
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colDescripcion.setMinWidth(280);

        TableColumn<Solicitud, String> colSolicitante = new TableColumn<>("Solicitante");
        colSolicitante.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colSolicitante.setMinWidth(160);

        TableColumn<Solicitud, String> colPrioridad = new TableColumn<>("Prioridad");
        colPrioridad.setCellValueFactory(new PropertyValueFactory<>("prioridad"));
        colPrioridad.setMinWidth(110);

        TableColumn<Solicitud, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(data -> new ReadOnlyStringWrapper(
                data.getValue().getEstado().mostrarNombreEstado()
        ));
        colEstado.setMinWidth(130);

        TableColumn<Solicitud, String> colFecha = new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getFechaCreacion().toString()));
        colFecha.setMinWidth(120);

        tabla.getColumns().addAll(colId, colDescripcion, colSolicitante, colPrioridad, colEstado, colFecha);
        configurarTabla(tabla);
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
        configurarTabla(tabla);
        tabla.setPrefHeight(330);
        return tabla;
    }

    private <T> void configurarTabla(TableView<T> tabla) {
        tabla.setFixedCellSize(42);
        tabla.setMinHeight(220);
    }

    private void mostrarFormularioAgregarResidente() {
        Dialog<ButtonType> dialog = crearDialogo("Agregar residente", "Cargar nuevo residente");

        TextField nombre = campo("Nombre");
        TextField apellido = campo("Apellido");
        TextField dni = campo("DNI");
        TextField email = campo("Email");
        TextField telefono = campo("Telefono");
        TextField lote = campo("Lote / vivienda");
        TextField direccion = campo("Direccion de vivienda");

        GridPane grid = crearGridFormulario();
        agregarFila(grid, 0, "Nombre", nombre);
        agregarFila(grid, 1, "Apellido", apellido);
        agregarFila(grid, 2, "DNI", dni);
        agregarFila(grid, 3, "Email", email);
        agregarFila(grid, 4, "Telefono", telefono);
        agregarFila(grid, 5, "Lote", lote);
        agregarFila(grid, 6, "Direccion", direccion);

        dialog.getDialogPane().setContent(grid);
        Optional<ButtonType> resultado = dialog.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            if (nombre.getText().isBlank() || apellido.getText().isBlank() || dni.getText().isBlank() || email.getText().isBlank()) {
                mostrarAlerta("Datos incompletos", "Nombre, apellido, DNI y email son obligatorios.");
                return;
            }

            Vivienda vivienda = sistemaBarrio.registrarVivienda(
                    barrioPrincipal,
                    textoOValor(lote, "Sin lote"),
                    textoOValor(direccion, "Sin direccion")
            );
            Residente residente = sistemaBarrio.registrarResidente(
                    nombre.getText(),
                    apellido.getText(),
                    dni.getText(),
                    email.getText(),
                    telefono.getText(),
                    vivienda
            );
            sistemaBarrio.crearCuentaResidenteSiNoExiste(residente, dni.getText());
            mostrarAlerta("Cuenta creada", "Usuario: " + residente.getEmail() + "\nClave inicial: " + dni.getText());
            actualizarYVolverAResidentes();
        }
    }

    private void mostrarFormularioRegistrarVisitante() {
        Residente residente = resolverResidenteParaOperacion("Seleccionar residente", "Elige a quien autoriza el visitante");
        if (residente == null) {
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
            if (modoActual == ModoVista.RESIDENTE) {
                actualizarYVolverAMisVisitantes();
            } else {
                actualizarYVolverAVisitantes();
            }
        }
    }

    private void mostrarFormularioCrearReclamo() {
        Residente residente = resolverResidenteParaOperacion("Seleccionar residente", "Elige quien carga el reclamo");
        if (residente == null) {
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
            if (modoActual == ModoVista.RESIDENTE) {
                actualizarYVolverAMisSolicitudes();
            } else {
                actualizarYVolverASolicitudes();
            }
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

    private Residente resolverResidenteParaOperacion(String titulo, String header) {
        asegurarResidenteActual();
        if (modoActual == ModoVista.RESIDENTE) {
            return residenteActual;
        }

        Residente seleccionado = obtenerResidenteSeleccionado();
        if (seleccionado != null) {
            return seleccionado;
        }

        Dialog<Residente> dialog = new Dialog<>();
        dialog.setTitle(titulo);
        dialog.setHeaderText(header);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.getDialogPane().getStyleClass().add("dialog-pane");

        ComboBox<Residente> comboResidentes = new ComboBox<>();
        comboResidentes.getItems().addAll(sistemaBarrio.getResidentes());
        if (!comboResidentes.getItems().isEmpty()) {
            comboResidentes.setValue(comboResidentes.getItems().get(0));
        }
        comboResidentes.setMaxWidth(Double.MAX_VALUE);

        VBox contenido = new VBox(10, new Label("Residente"), comboResidentes);
        contenido.setPadding(new Insets(18));
        dialog.getDialogPane().setContent(contenido);
        dialog.setResultConverter(button -> button == ButtonType.OK ? comboResidentes.getValue() : null);
        return dialog.showAndWait().orElse(null);
    }

    private void actualizarYVolverAResidentes() {
        actualizarTablasDesdeSistema();
        mostrarResidentes();
    }

    private void actualizarYVolverAVisitantes() {
        actualizarTablasDesdeSistema();
        mostrarVisitantes();
    }

    private void actualizarYVolverAMisVisitantes() {
        actualizarTablasDesdeSistema();
        mostrarMisVisitantes();
    }

    private void actualizarYVolverAAccesos() {
        actualizarTablasDesdeSistema();
        mostrarAccesos();
    }

    private void actualizarYVolverASolicitudes() {
        actualizarTablasDesdeSistema();
        mostrarSolicitudes();
    }

    private void actualizarYVolverAMisSolicitudes() {
        actualizarTablasDesdeSistema();
        mostrarMisSolicitudes();
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

    private void asegurarResidenteActual() {
        if (residenteActual == null && modoActual == ModoVista.RESIDENTE && !sistemaBarrio.getResidentes().isEmpty()) {
            residenteActual = sistemaBarrio.getResidentes().get(0);
        }
    }

    private String nombreResidenteActual() {
        asegurarResidenteActual();
        return residenteActual == null ? "Residente" : residenteActual.getNombre() + " " + residenteActual.getApellido();
    }

    private int contarSolicitudesDelResidente() {
        return solicitudesDelResidente().size();
    }

    private ObservableList<Solicitud> solicitudesDelResidente() {
        ObservableList<Solicitud> resultado = FXCollections.observableArrayList();
        if (residenteActual == null) {
            return resultado;
        }

        String nombreCompleto = residenteActual.getNombre() + " " + residenteActual.getApellido();
        for (Solicitud solicitud : sistemaBarrio.getSolicitudes()) {
            if (nombreCompleto.equalsIgnoreCase(solicitud.getNombre())) {
                resultado.add(solicitud);
            }
        }
        return resultado;
    }

    private Residente buscarResidentePorId(Integer id) {
        if (id == null) {
            return null;
        }
        for (Residente residente : sistemaBarrio.getResidentes()) {
            if (residente.getId() == id) {
                return residente;
            }
        }
        return null;
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
