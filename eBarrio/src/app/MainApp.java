package app;

import app.views.AdministradorView;
import app.views.LoginView;
import app.views.ResidenteView;
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
import javafx.scene.control.CheckBox;
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
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import model.accesos.Acceso;
import model.accesos.Visitante;
import model.barrio.Barrio;
import model.barrio.Residente;
import model.barrio.Vivienda;
import model.notificaciones.Notificacion;
import model.solicitud.Solicitud;
import model.solicitud.personal.IncidenteSeguridad;
import model.solicitud.personal.PersonalMantenimiento;
import model.solicitud.personal.PersonalSeguridad;
import model.solicitud.personal.TareaMantenimiento;
import sistema.SistemaBarrio;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class MainApp extends Application {

    private enum ModoVista {
        ADMIN,
        RESIDENTE
    }

    private final SistemaBarrio sistemaBarrio = new SistemaBarrio();
    private static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final ObservableList<Residente> residentes = FXCollections.observableArrayList();
    private final ObservableList<Visitante> visitantes = FXCollections.observableArrayList();
    private final ObservableList<Solicitud> solicitudes = FXCollections.observableArrayList();
    private final ObservableList<Acceso> accesos = FXCollections.observableArrayList();
    private final PersonalSeguridad personalSeguridad = new PersonalSeguridad(1, "Equipo de seguridad", "Turno actual");
    private final PersonalMantenimiento personalMantenimiento = new PersonalMantenimiento(1, "Equipo de mantenimiento", "Areas comunes");

    private BorderPane root;
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
        root.setCenter(crearVistaLogin());

        Scene scene = new Scene(root, 1180, 720);
        cargarEstilos(scene);

        stage.setTitle("eBarrio - Gestion de Barrios Cerrados");
        stage.setMinWidth(900);
        stage.setMinHeight(560);
        stage.setScene(scene);
        stage.show();
    }

    private void cargarEstilos(Scene scene) {
        URL css = getClass().getResource("/styles/styles.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }
    }

    private void mostrarNotificaciones() {
        Dialog<ButtonType> dialog = crearDialogo("Notificaciones", "Novedades generadas por el sistema");

        TableView<Notificacion> tabla = new TableView<>();
        tabla.setItems(FXCollections.observableArrayList(notificacionesVisibles()));
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Notificacion, String> colFecha = new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(data -> new ReadOnlyStringWrapper(formatearFechaHora(data.getValue().getFecha())));
        colFecha.setMinWidth(150);

        TableColumn<Notificacion, String> colDestino = new TableColumn<>("Destino");
        colDestino.setCellValueFactory(new PropertyValueFactory<>("destinatario"));
        colDestino.setMinWidth(120);

        TableColumn<Notificacion, String> colMensaje = new TableColumn<>("Mensaje");
        colMensaje.setCellValueFactory(new PropertyValueFactory<>("mensaje"));
        colMensaje.setMinWidth(380);

        tabla.getColumns().add(colFecha);
        tabla.getColumns().add(colDestino);
        tabla.getColumns().add(colMensaje);
        configurarTabla(tabla);
        tabla.setPrefHeight(360);

        VBox contenido = new VBox(12, tabla);
        contenido.setPadding(new Insets(12));
        dialog.getDialogPane().setContent(contenido);
        dialog.getDialogPane().setPrefWidth(760);
        dialog.showAndWait();
    }

    private void abrirAppSegunSesion() {
        if (modoActual == ModoVista.ADMIN) {
            AdministradorView vista = new AdministradorView(
                    notificacionesVisibles().size(),
                    this::mostrarNotificaciones,
                    this::cerrarSesion,
                    this::mostrarInicio,
                    this::mostrarResidentes,
                    this::mostrarAccesos,
                    this::mostrarSolicitudes,
                    this::mostrarSeguridad,
                    this::mostrarMantenimiento
            );
            root.setLeft(vista.crearMenuLateral());
            root.setTop(vista.crearBarraSuperior());
            mostrarInicio();
        } else {
            asegurarResidenteActual();
            ResidenteView vista = new ResidenteView(
                    nombreResidenteActual(),
                    notificacionesVisibles().size(),
                    this::mostrarNotificaciones,
                    this::cerrarSesion,
                    this::mostrarInicioResidente,
                    this::mostrarMisVisitantes,
                    this::mostrarMisSolicitudes,
                    this::mostrarFormularioCrearReclamo
            );
            root.setLeft(vista.crearMenuLateral());
            root.setTop(vista.crearBarraSuperior());
            mostrarInicioResidente();
        }
    }

    private void cerrarSesion() {
        usuarioActual = null;
        modoActual = ModoVista.ADMIN;
        residenteActual = null;
        root.setTop(null);
        root.setLeft(null);
        root.setCenter(crearVistaLogin());
    }

    private VBox crearVistaLogin() {
        return new LoginView(this::intentarLogin).crear();
    }

    private void intentarLogin(String email, String password) {
        SistemaBarrio.UsuarioSistema usuario = sistemaBarrio.autenticarUsuario(email, password);
        if (usuario != null) {
            usuarioActual = usuario;
            modoActual = usuario.esResidente() ? ModoVista.RESIDENTE : ModoVista.ADMIN;
            residenteActual = modoActual == ModoVista.RESIDENTE ? buscarResidentePorId(usuario.getResidenteId()) : null;
            if (modoActual == ModoVista.RESIDENTE && residenteActual == null) {
                mostrarAlerta("No pudimos abrir tu perfil", "La cuenta no tiene un residente asignado. Contacta a administracion.");
                return;
            }
            abrirAppSegunSesion();
            return;
        }

        mostrarAlerta("Datos incorrectos", "Ingresa bien el email y la clave para continuar.");
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
                crearMetrica("Notificaciones", String.valueOf(notificacionesVisibles().size()))
        );
        metricas.getStyleClass().add("metric-row");

        Label viviendaTitulo = crearTituloCard("Datos de vivienda");
        VBox datosVivienda = new VBox(
                8,
                crearMiniDato("Lote", vivienda == null ? "No asignada" : vivienda.getLote()),
                crearMiniDato("Direccion", vivienda == null ? "No cargada" : vivienda.getDireccion()),
                crearMiniDato("Contacto", residenteActual.getTelefono().isBlank() ? "No cargado" : residenteActual.getTelefono())
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
        FlowPane acciones = crearFilaAcciones(nuevoVisitante, nuevoReclamo, verReclamos);
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
        FlowPane acciones = crearFilaAcciones(
                crearBotonPrimario("Nuevo residente", this::mostrarFormularioAgregarResidente),
                crearBotonSecundario("Editar residente", this::mostrarFormularioEditarResidente)
        );
        contenido.getChildren().addAll(acciones, crearCard("Listado de residentes", tablaResidentes));
        root.setCenter(crearScroll(contenido));
    }

    private void mostrarMisVisitantes() {
        asegurarResidenteActual();
        tablaVisitantes = crearTablaVisitantes();
        visitantes.setAll(residenteActual.getVisitantes());

        VBox contenido = crearContenidoBase("Mis visitantes", "Personas autorizadas para ingresar a tu vivienda");
        ComboBox<String> filtroTipo = combo("Todos", "Todos", "Visitantes", "Proveedores");
        filtroTipo.setPrefWidth(190);
        filtroTipo.setOnAction(e -> aplicarFiltroAutorizados(filtroTipo.getValue()));
        HBox filtroAutorizados = new HBox(10, new Label("Ver"), filtroTipo);
        filtroAutorizados.setAlignment(Pos.CENTER_LEFT);
        FlowPane acciones = crearFilaAcciones(
                crearBotonPrimario("Autorizar visitante/proveedor", this::mostrarFormularioRegistrarVisitante),
                crearBotonSecundario("Editar", this::mostrarFormularioEditarVisitante),
                crearBotonSecundario("Eliminar", this::eliminarVisitanteSeleccionado)
        );
        contenido.getChildren().addAll(filtroAutorizados, acciones, crearCard("Visitantes autorizados", tablaVisitantes));
        root.setCenter(crearScroll(contenido));
    }

    private void mostrarAccesos() {
        actualizarTablasDesdeSistema();
        tablaVisitantes = crearTablaVisitantes();
        tablaAccesos = crearTablaAccesos();

        VBox contenido = crearContenidoBase("Accesos y visitantes", "Visitantes autorizados, ingresos y egresos del barrio");
        ComboBox<String> filtroTipo = combo("Todos", "Todos", "Visitantes", "Proveedores");
        filtroTipo.setPrefWidth(190);
        filtroTipo.setOnAction(e -> aplicarFiltroAutorizados(filtroTipo.getValue()));
        HBox filtroAutorizados = new HBox(10, new Label("Ver"), filtroTipo);
        filtroAutorizados.setAlignment(Pos.CENTER_LEFT);
        FlowPane accionesVisitantes = crearFilaAcciones(
                crearBotonPrimario("Nuevo visitante/proveedor", this::mostrarFormularioRegistrarVisitante),
                crearBotonSecundario("Editar", this::mostrarFormularioEditarVisitante),
                crearBotonSecundario("Eliminar", this::eliminarVisitanteSeleccionado)
        );
        FlowPane accionesAccesos = crearFilaAcciones(
                crearBotonPrimario("Registrar ingreso", this::registrarAccesoVisitanteSeleccionado),
                crearBotonSecundario("Registrar egreso", this::registrarEgresoAccesoSeleccionado)
        );

        VBox visitantesCard = crearCard("Visitantes autorizados", tablaVisitantes);
        VBox accesosCard = crearCard("Ingresos y egresos", tablaAccesos);

        contenido.getChildren().addAll(filtroAutorizados, accionesVisitantes, visitantesCard, accionesAccesos, accesosCard);
        root.setCenter(crearScroll(contenido));
    }

    private void aplicarFiltroAutorizados(String filtro) {
        Iterable<Visitante> fuente = modoActual == ModoVista.RESIDENTE && residenteActual != null
                ? residenteActual.getVisitantes()
                : sistemaBarrio.getVisitantes();

        if ("Todos".equalsIgnoreCase(filtro)) {
            ObservableList<Visitante> todos = FXCollections.observableArrayList();
            for (Visitante visitante : fuente) {
                todos.add(visitante);
            }
            visitantes.setAll(todos);
            return;
        }

        String tipoBuscado = "Proveedores".equalsIgnoreCase(filtro) ? "Proveedor" : "Visitante";
        ObservableList<Visitante> filtrados = FXCollections.observableArrayList();
        for (Visitante visitante : fuente) {
            if (tipoBuscado.equalsIgnoreCase(visitante.getTipo())) {
                filtrados.add(visitante);
            }
        }
        visitantes.setAll(filtrados);
    }

    private void mostrarSolicitudes() {
        actualizarTablasDesdeSistema();
        tablaSolicitudes = crearTablaSolicitudes();

        VBox contenido = crearContenidoBase("Solicitudes", "Reclamos, tareas e incidentes");
        FlowPane acciones = crearFilaAcciones(
                crearBotonPrimario("Nuevo reclamo", this::mostrarFormularioCrearReclamo),
                crearBotonSecundario("Nueva tarea", this::mostrarFormularioCrearTarea),
                crearBotonSecundario("Nuevo incidente", this::mostrarFormularioCrearIncidente),
                crearBotonSecundario("Avanzar estado", this::avanzarEstadoSolicitudSeleccionada),
                crearBotonSecundario("Cancelar", this::cancelarSolicitudSeleccionada)
        );
        contenido.getChildren().addAll(acciones, crearCard("Seguimiento de solicitudes", tablaSolicitudes));
        root.setCenter(crearScroll(contenido));
    }

    private void mostrarSeguridad() {
        actualizarTablasDesdeSistema();
        tablaVisitantes = crearTablaVisitantes();
        tablaAccesos = crearTablaAccesos();
        tablaSolicitudes = crearTablaSolicitudes();
        solicitudes.setAll(solicitudesDeTipo(IncidenteSeguridad.class));

        VBox contenido = crearContenidoBase("Seguridad", personalSeguridad.getNombre() + " - " + personalSeguridad.getTurno());
        FlowPane acciones = crearFilaAcciones(
                crearBotonPrimario("Registrar ingreso", this::registrarAccesoVisitanteSeleccionado),
                crearBotonSecundario("Registrar egreso", this::registrarEgresoAccesoSeleccionado),
                crearBotonSecundario("Reportar incidente", this::mostrarFormularioCrearIncidente)
        );

        contenido.getChildren().addAll(
                acciones,
                crearCard("Visitantes autorizados", tablaVisitantes),
                crearCard("Ingresos y egresos", tablaAccesos),
                crearCard("Incidentes reportados", tablaSolicitudes)
        );
        root.setCenter(crearScroll(contenido));
    }

    private void mostrarMantenimiento() {
        actualizarTablasDesdeSistema();
        tablaSolicitudes = crearTablaSolicitudes();
        solicitudes.setAll(solicitudesDeTipo(TareaMantenimiento.class));

        VBox contenido = crearContenidoBase("Mantenimiento", personalMantenimiento.getNombre() + " - " + personalMantenimiento.getSector());
        FlowPane acciones = crearFilaAcciones(
                crearBotonPrimario("Nueva tarea", this::mostrarFormularioCrearTarea),
                crearBotonSecundario("Avanzar tarea", this::avanzarTareaMantenimientoSeleccionada)
        );

        contenido.getChildren().addAll(acciones, crearCard("Tareas de mantenimiento", tablaSolicitudes));
        root.setCenter(crearScroll(contenido));
    }

    private void mostrarMisSolicitudes() {
        asegurarResidenteActual();
        tablaSolicitudes = crearTablaSolicitudes();
        solicitudes.setAll(solicitudesDelResidente());

        VBox contenido = crearContenidoBase("Mis reclamos", "Seguimiento de reclamos cargados por tu usuario");
        FlowPane acciones = crearFilaAcciones(crearBotonPrimario("Nuevo reclamo", this::mostrarFormularioCrearReclamo));
        contenido.getChildren().addAll(acciones, crearCard("Mis solicitudes", tablaSolicitudes));
        root.setCenter(crearScroll(contenido));
    }

    private ObservableList<Solicitud> solicitudesDeTipo(Class<? extends Solicitud> tipo) {
        ObservableList<Solicitud> resultado = FXCollections.observableArrayList();
        for (Solicitud solicitud : sistemaBarrio.getSolicitudes()) {
            if (tipo.isInstance(solicitud)) {
                resultado.add(solicitud);
            }
        }
        return resultado;
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

    private FlowPane crearFilaAcciones(Node... acciones) {
        FlowPane fila = new FlowPane(10, 10, acciones);
        fila.setAlignment(Pos.CENTER_LEFT);
        return fila;
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
            return new ReadOnlyStringWrapper(vivienda == null ? "No asignada" : vivienda.getLote());
        });

        TableColumn<Residente, String> colTelefono = new TableColumn<>("Telefono");
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));

        tabla.getColumns().add(colNombre);
        tabla.getColumns().add(colDni);
        tabla.getColumns().add(colVivienda);
        tabla.getColumns().add(colTelefono);
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

        TableColumn<Visitante, String> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));

        TableColumn<Visitante, String> colFrecuencia = new TableColumn<>("Frecuencia");
        colFrecuencia.setCellValueFactory(new PropertyValueFactory<>("frecuencia"));

        TableColumn<Visitante, String> colDni = new TableColumn<>("DNI");
        colDni.setCellValueFactory(new PropertyValueFactory<>("dni"));

        TableColumn<Visitante, String> colPatente = new TableColumn<>("Patente");
        colPatente.setCellValueFactory(new PropertyValueFactory<>("patente"));

        TableColumn<Visitante, String> colMotivo = new TableColumn<>("Motivo");
        colMotivo.setCellValueFactory(new PropertyValueFactory<>("motivoVisita"));

        tabla.getColumns().add(colTipo);
        tabla.getColumns().add(colNombre);
        tabla.getColumns().add(colDni);
        tabla.getColumns().add(colPatente);
        tabla.getColumns().add(colFrecuencia);
        tabla.getColumns().add(colMotivo);
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

        tabla.getColumns().add(colId);
        tabla.getColumns().add(colDescripcion);
        tabla.getColumns().add(colSolicitante);
        tabla.getColumns().add(colPrioridad);
        tabla.getColumns().add(colEstado);
        tabla.getColumns().add(colFecha);
        configurarTabla(tabla);
        tabla.setPrefHeight(330);
        return tabla;
    }

    private TableView<Acceso> crearTablaAccesos() {
        TableView<Acceso> tabla = new TableView<>();
        tabla.setItems(accesos);
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Acceso, String> colIngreso = new TableColumn<>("Ingreso");
        colIngreso.setCellValueFactory(data -> new ReadOnlyStringWrapper(formatearFechaHora(data.getValue().getFechaIngreso())));
        colIngreso.setMinWidth(150);

        TableColumn<Acceso, String> colEgreso = new TableColumn<>("Egreso");
        colEgreso.setCellValueFactory(data -> new ReadOnlyStringWrapper(formatearFechaHora(data.getValue().getFechaEgreso())));
        colEgreso.setMinWidth(150);

        TableColumn<Acceso, String> colVisitante = new TableColumn<>("Visitante");
        colVisitante.setCellValueFactory(new PropertyValueFactory<>("nombreVisitante"));

        TableColumn<Acceso, String> colDni = new TableColumn<>("DNI");
        colDni.setCellValueFactory(new PropertyValueFactory<>("dniVisitante"));

        TableColumn<Acceso, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().estaActivo() ? "Activo" : "Finalizado"));

        tabla.getColumns().add(colIngreso);
        tabla.getColumns().add(colEgreso);
        tabla.getColumns().add(colVisitante);
        tabla.getColumns().add(colDni);
        tabla.getColumns().add(colEstado);
        configurarTabla(tabla);
        tabla.setPrefHeight(330);
        return tabla;
    }

    private <T> void configurarTabla(TableView<T> tabla) {
        tabla.setFixedCellSize(42);
        tabla.setMinHeight(220);
    }

    private String formatearFechaHora(LocalDateTime fechaHora) {
        return fechaHora == null ? "-" : fechaHora.format(FORMATO_FECHA_HORA);
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
            if (nombre.getText().isBlank() || apellido.getText().isBlank() || dni.getText().isBlank()
                    || email.getText().isBlank() || lote.getText().isBlank() || direccion.getText().isBlank()) {
                mostrarAlerta("Faltan datos", "Completa nombre, apellido, DNI, email, lote y direccion.");
                return;
            }
            if (!validarDocumento(dni.getText()) || !validarEmail(email.getText()) || !validarTelefonoOpcional(telefono.getText())) {
                return;
            }

            Vivienda vivienda = sistemaBarrio.registrarVivienda(
                    barrioPrincipal,
                    lote.getText().trim(),
                    direccion.getText().trim()
            );
            Residente residente = sistemaBarrio.registrarResidente(
                    nombre.getText().trim(),
                    apellido.getText().trim(),
                    dni.getText().trim(),
                    email.getText().trim(),
                    telefono.getText().trim(),
                    vivienda
            );
            sistemaBarrio.crearCuentaResidenteSiNoExiste(residente, dni.getText().trim());
            mostrarAlerta("Cuenta creada", "Usuario: " + residente.getEmail() + "\nClave inicial: " + dni.getText().trim());
            actualizarYVolverAResidentes();
        }
    }

    private void mostrarFormularioEditarResidente() {
        Residente residente = obtenerResidenteSeleccionado();
        if (residente == null) {
            mostrarAlerta("Elegir residente", "Primero selecciona un residente de la tabla.");
            return;
        }

        Vivienda vivienda = residente.getVivienda();
        Dialog<ButtonType> dialog = crearDialogo("Editar residente", "Modificar datos de " + residente.getNombre() + " " + residente.getApellido());

        TextField nombre = campo("Nombre");
        nombre.setText(residente.getNombre());
        TextField apellido = campo("Apellido");
        apellido.setText(residente.getApellido());
        TextField dni = campo("DNI");
        dni.setText(residente.getDni());
        TextField email = campo("Email");
        email.setText(residente.getEmail());
        TextField telefono = campo("Telefono");
        telefono.setText(residente.getTelefono());
        TextField lote = campo("Lote / vivienda");
        lote.setText(vivienda == null ? "" : vivienda.getLote());
        TextField direccion = campo("Direccion de vivienda");
        direccion.setText(vivienda == null ? "" : vivienda.getDireccion());

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
            if (nombre.getText().isBlank() || apellido.getText().isBlank() || dni.getText().isBlank()
                    || email.getText().isBlank() || lote.getText().isBlank() || direccion.getText().isBlank()) {
                mostrarAlerta("Faltan datos", "Completa nombre, apellido, DNI, email, lote y direccion.");
                return;
            }
            if (!validarDocumento(dni.getText()) || !validarEmail(email.getText()) || !validarTelefonoOpcional(telefono.getText())) {
                return;
            }

            sistemaBarrio.actualizarResidente(
                    residente,
                    nombre.getText().trim(),
                    apellido.getText().trim(),
                    dni.getText().trim(),
                    email.getText().trim(),
                    telefono.getText().trim(),
                    lote.getText().trim(),
                    direccion.getText().trim()
            );
            actualizarYVolverAResidentes();
        }
    }

    private void mostrarFormularioRegistrarVisitante() {
        Residente residente = resolverResidenteParaOperacion("Seleccionar residente", "Elige a quien autoriza el visitante");
        if (residente == null) {
            return;
        }

        Dialog<ButtonType> dialog = crearDialogo("Registrar visitante/proveedor", "Autorizado por " + residente.getNombre() + " " + residente.getApellido());

        ComboBox<String> tipo = combo("Visitante", "Visitante", "Proveedor");
        ComboBox<String> frecuencia = combo("Unica vez", "Unica vez", "Semanal", "Mensual");
        TextField nombre = campo("Nombre del visitante");
        TextField dni = campo("DNI");
        TextField patente = campo("Patente opcional");
        TextField motivo = campo("Motivo de visita");
        CheckBox registrarAccesoAhora = new CheckBox("Desea registrar acceso ahora");

        GridPane grid = crearGridFormulario();
        agregarFila(grid, 0, "Tipo", tipo);
        agregarFila(grid, 1, "Nombre", nombre);
        agregarFila(grid, 2, "DNI", dni);
        agregarFila(grid, 3, "Patente opcional", patente);
        agregarFila(grid, 4, "Frecuencia", frecuencia);
        agregarFila(grid, 5, "Motivo", motivo);
        agregarFila(grid, 6, "Ingreso", registrarAccesoAhora);

        dialog.getDialogPane().setContent(grid);
        Optional<ButtonType> resultado = dialog.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            if (nombre.getText().isBlank() || dni.getText().isBlank()) {
                mostrarAlerta("Faltan datos", "Completa nombre y DNI.");
                return;
            }
            if (!validarDocumento(dni.getText()) || !validarPatenteOpcional(patente.getText())) {
                return;
            }

            Visitante visitante = sistemaBarrio.registrarVisitante(residente, nombre.getText().trim(), dni.getText().trim(), patente.getText().trim(), motivo.getText().trim(), tipo.getValue(), frecuencia.getValue());
            if (registrarAccesoAhora.isSelected()) {
                sistemaBarrio.registrarAcceso(visitante.getNombre(), visitante.getDni());
            }
            if (modoActual == ModoVista.RESIDENTE) {
                actualizarYVolverAMisVisitantes();
            } else {
                actualizarYVolverAAccesos();
            }
        }
    }

    private void mostrarFormularioEditarVisitante() {
        Visitante visitante = obtenerVisitanteSeleccionado();
        if (visitante == null) {
            mostrarAlerta("Elegir persona", "Primero selecciona un visitante o proveedor de la tabla.");
            return;
        }

        Dialog<ButtonType> dialog = crearDialogo("Editar visitante/proveedor", "Modificar autorizado");

        ComboBox<String> tipo = combo(visitante.getTipo() == null ? "Visitante" : visitante.getTipo(), "Visitante", "Proveedor");
        ComboBox<String> frecuencia = combo(visitante.getFrecuencia() == null ? "Unica vez" : visitante.getFrecuencia(), "Unica vez", "Semanal", "Mensual");
        TextField nombre = campo("Nombre");
        nombre.setText(visitante.getNombre());
        TextField dni = campo("DNI");
        dni.setText(visitante.getDni());
        TextField patente = campo("Patente opcional");
        patente.setText(visitante.getPatente());
        TextField motivo = campo("Motivo");
        motivo.setText(visitante.getMotivoVisita());

        GridPane grid = crearGridFormulario();
        agregarFila(grid, 0, "Tipo", tipo);
        agregarFila(grid, 1, "Nombre", nombre);
        agregarFila(grid, 2, "DNI", dni);
        agregarFila(grid, 3, "Patente opcional", patente);
        agregarFila(grid, 4, "Frecuencia", frecuencia);
        agregarFila(grid, 5, "Motivo", motivo);

        dialog.getDialogPane().setContent(grid);
        Optional<ButtonType> resultado = dialog.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            if (nombre.getText().isBlank() || dni.getText().isBlank()) {
                mostrarAlerta("Faltan datos", "Completa nombre y DNI.");
                return;
            }
            if (!validarDocumento(dni.getText()) || !validarPatenteOpcional(patente.getText())) {
                return;
            }

            sistemaBarrio.actualizarVisitante(visitante, nombre.getText().trim(), dni.getText().trim(), patente.getText().trim(), motivo.getText().trim(), tipo.getValue(), frecuencia.getValue());
            if (modoActual == ModoVista.RESIDENTE) {
                actualizarYVolverAMisVisitantes();
            } else {
                actualizarYVolverAAccesos();
            }
        }
    }

    private void eliminarVisitanteSeleccionado() {
        Visitante visitante = obtenerVisitanteSeleccionado();
        if (visitante == null) {
            mostrarAlerta("Elegir persona", "Primero selecciona un visitante o proveedor de la tabla.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Eliminar visitante/proveedor");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("Eliminar a " + visitante.getNombre() + " de autorizados?");
        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            sistemaBarrio.eliminarVisitante(visitante);
            if (modoActual == ModoVista.RESIDENTE) {
                actualizarYVolverAMisVisitantes();
            } else {
                actualizarYVolverAAccesos();
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
                mostrarAlerta("Falta descripcion", "Escribe una descripcion para continuar.");
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
                mostrarAlerta("Falta descripcion", "Escribe una descripcion para continuar.");
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
                mostrarAlerta("Falta descripcion", "Escribe una descripcion para continuar.");
                return;
            }

            IncidenteSeguridad incidente = sistemaBarrio.crearIncidenteSeguridad("Seguridad", descripcion.getText(), prioridad.getValue(), riesgo.getValue());
            personalSeguridad.reportarIncidente(incidente);
            actualizarYVolverASolicitudes();
        }
    }

    private void registrarAccesoVisitanteSeleccionado() {
        Visitante visitante = seleccionarVisitanteParaAcceso();
        if (visitante == null) {
            return;
        }

        Acceso accesoActivo = sistemaBarrio.buscarAccesoActivoPorDni(visitante.getDni());
        if (accesoActivo != null) {
            mostrarAlerta("Ingreso ya registrado", visitante.getNombre() + " ya tiene un acceso activo.");
            return;
        }

        sistemaBarrio.registrarAcceso(visitante.getNombre(), visitante.getDni());
        actualizarYVolverAAccesos();
    }

    private void registrarEgresoAccesoSeleccionado() {
        Acceso acceso = tablaAccesos == null ? null : tablaAccesos.getSelectionModel().getSelectedItem();
        if (acceso == null) {
            mostrarAlerta("Elegir acceso", "Selecciona un ingreso activo para registrar el egreso.");
            return;
        }
        if (!acceso.estaActivo()) {
            mostrarAlerta("Acceso finalizado", "Ese acceso ya tiene egreso registrado.");
            return;
        }

        sistemaBarrio.registrarEgresoAcceso(acceso);
        actualizarYVolverAAccesos();
    }

    private Visitante seleccionarVisitanteParaAcceso() {
        Dialog<Visitante> dialog = new Dialog<>();
        dialog.setTitle("Registrar ingreso");
        dialog.setHeaderText("Selecciona un visitante autorizado");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.getDialogPane().getStyleClass().add("dialog-pane");

        ComboBox<Visitante> comboVisitantes = new ComboBox<>();
        comboVisitantes.getItems().addAll(sistemaBarrio.getVisitantes());
        comboVisitantes.setMaxWidth(Double.MAX_VALUE);
        comboVisitantes.setConverter(new StringConverter<>() {
            @Override
            public String toString(Visitante visitante) {
                if (visitante == null) {
                    return "";
                }
                return visitante.getNombre() + " - DNI " + visitante.getDni() + " - " + visitante.getMotivoVisita();
            }

            @Override
            public Visitante fromString(String string) {
                return null;
            }
        });

        if (!comboVisitantes.getItems().isEmpty()) {
            comboVisitantes.setValue(comboVisitantes.getItems().get(0));
        }

        VBox contenido = new VBox(10, new Label("Visitante"), comboVisitantes);
        contenido.setPadding(new Insets(18));
        dialog.getDialogPane().setContent(contenido);
        dialog.setResultConverter(button -> button == ButtonType.OK ? comboVisitantes.getValue() : null);

        if (comboVisitantes.getItems().isEmpty()) {
            mostrarAlerta("No hay personas autorizadas", "Primero registra un visitante o proveedor.");
            return null;
        }

        return dialog.showAndWait().orElse(null);
    }

    private void avanzarEstadoSolicitudSeleccionada() {
        Solicitud solicitud = obtenerSolicitudSeleccionada();
        if (solicitud == null) {
            mostrarAlerta("Elegir solicitud", "Primero selecciona una solicitud de la tabla.");
            return;
        }

        sistemaBarrio.avanzarEstadoSolicitud(solicitud);
        actualizarYVolverASolicitudes();
    }

    private void avanzarTareaMantenimientoSeleccionada() {
        Solicitud solicitud = obtenerSolicitudSeleccionada();
        if (!(solicitud instanceof TareaMantenimiento)) {
            mostrarAlerta("Elegir tarea", "Selecciona una tarea de mantenimiento para avanzar su estado.");
            return;
        }

        sistemaBarrio.avanzarTareaMantenimiento(personalMantenimiento, (TareaMantenimiento) solicitud);
        mostrarMantenimiento();
    }

    private void cancelarSolicitudSeleccionada() {
        Solicitud solicitud = obtenerSolicitudSeleccionada();
        if (solicitud == null) {
            mostrarAlerta("Elegir solicitud", "Primero selecciona una solicitud de la tabla.");
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

        if (root != null && usuarioActual != null) {
            root.setTop(crearBarraSuperiorSesion());
        }
    }

    private HBox crearBarraSuperiorSesion() {
        if (modoActual == ModoVista.ADMIN) {
            return new AdministradorView(
                    notificacionesVisibles().size(),
                    this::mostrarNotificaciones,
                    this::cerrarSesion,
                    this::mostrarInicio,
                    this::mostrarResidentes,
                    this::mostrarAccesos,
                    this::mostrarSolicitudes,
                    this::mostrarSeguridad,
                    this::mostrarMantenimiento
            ).crearBarraSuperior();
        }

        return new ResidenteView(
                nombreResidenteActual(),
                notificacionesVisibles().size(),
                this::mostrarNotificaciones,
                this::cerrarSesion,
                this::mostrarInicioResidente,
                this::mostrarMisVisitantes,
                this::mostrarMisSolicitudes,
                this::mostrarFormularioCrearReclamo
        ).crearBarraSuperior();
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

    private boolean validarDocumento(String valor) {
        String documento = valor.trim();
        if (!documento.matches("\\d{7,11}")) {
            mostrarAlerta("Revisa el documento", "Ingresa un DNI o CUIT con numeros, entre 7 y 11 digitos.");
            return false;
        }
        return true;
    }

    private boolean validarTelefonoOpcional(String valor) {
        String telefono = valor.trim();
        if (!telefono.isEmpty() && !telefono.matches("\\d{8,15}")) {
            mostrarAlerta("Revisa el telefono", "Ingresa un telefono con numeros, entre 8 y 15 digitos.");
            return false;
        }
        return true;
    }

    private boolean validarEmail(String valor) {
        String email = valor.trim();
        if (!email.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) {
            mostrarAlerta("Revisa el email", "Ingresa un email valido para poder crear la cuenta.");
            return false;
        }
        return true;
    }

    private boolean validarPatenteOpcional(String valor) {
        String patente = valor.trim();
        if (!patente.isEmpty() && !patente.matches("[A-Za-z0-9]{6,8}")) {
            mostrarAlerta("Revisa la patente", "La patente es opcional. Si la cargas, usa entre 6 y 8 letras o numeros.");
            return false;
        }
        return true;
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

    private ObservableList<Notificacion> notificacionesVisibles() {
        ObservableList<Notificacion> resultado = FXCollections.observableArrayList();
        if (modoActual == ModoVista.ADMIN) {
            resultado.addAll(sistemaBarrio.getNotificaciones());
            return resultado;
        }

        String nombre = nombreResidenteActual();
        String email = usuarioActual == null ? "" : usuarioActual.getEmail();
        for (Notificacion notificacion : sistemaBarrio.getNotificaciones()) {
            String destinatario = notificacion.getDestinatario();
            if ("Residente".equalsIgnoreCase(destinatario)
                    || nombre.equalsIgnoreCase(destinatario)
                    || email.equalsIgnoreCase(destinatario)) {
                resultado.add(notificacion);
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
