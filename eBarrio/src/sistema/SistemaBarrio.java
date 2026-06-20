package sistema;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.Administrador;
import model.solicitud.SolicitudFactory;

import model.accesos.Acceso;
import model.accesos.Visitante;

import model.barrio.Barrio;
import model.barrio.Residente;
import model.barrio.Vivienda;

import model.notificaciones.Notificacion;

import model.solicitud.IObservador;
import model.solicitud.Solicitud;
import model.solicitud.estados.EstadoAsignado;
import model.solicitud.estados.EstadoCerrado;
import model.solicitud.estados.EstadoEnProceso;
import model.solicitud.estados.EstadoPendiente;
import model.solicitud.estados.EstadoResuelto;
import model.solicitud.personal.IncidenteSeguridad;
import model.solicitud.personal.TareaMantenimiento;
import model.solicitud.reclamo.Reclamo;

// Clase principal del sistema. Funciona como fachada/controlador.
// Centraliza las operaciones principales y mantiene los datos en memoria.
public class SistemaBarrio {

    public static class UsuarioSistema {
        private final String email;
        private final String password;
        private final String rol;
        private final Integer residenteId;

        public UsuarioSistema(String email, String password, String rol, Integer residenteId) {
            this.email = email;
            this.password = password;
            this.rol = rol;
            this.residenteId = residenteId;
        }

        public String getEmail() { return email; }
        public String getPassword() { return password; }
        public String getRol() { return rol; }
        public Integer getResidenteId() { return residenteId; }
        public boolean esResidente() { return "residente".equalsIgnoreCase(rol); }
    }

    // Listas en memoria
    private List<Barrio> barrios;
    private List<Vivienda> viviendas;
    private List<Residente> residentes;
    private List<Visitante> visitantes;

    private List<Solicitud> solicitudes;
    private List<Acceso> accesos;
    private List<Notificacion> notificaciones;
    private List<IObservador> observadoresGlobales;
    private List<Administrador> administradores;
    private List<UsuarioSistema> usuarios;
    private boolean persistenciaActiva = true;

    // Contadores de IDs
    private int contadorBarrios = 1;
    private int contadorViviendas = 1;
    private int contadorResidentes = 1;
    private int contadorVisitantes = 1;
    private int contadorSolicitudes = 1;
    private int contadorAccesos = 1;
    private int contadorNotificaciones = 1;

    public SistemaBarrio() {
        this.barrios = new ArrayList<>();
        this.viviendas = new ArrayList<>();
        this.residentes = new ArrayList<>();
        this.visitantes = new ArrayList<>();

        this.solicitudes = new ArrayList<>();
        this.accesos = new ArrayList<>();
        this.notificaciones = new ArrayList<>();
        this.observadoresGlobales = new ArrayList<>();
        this.administradores = new ArrayList<>();
        this.usuarios = new ArrayList<>();
    }

    // ─────────────────────────────────────────────
    // Barrio, viviendas, residentes y visitantes
    // ─────────────────────────────────────────────

    public Barrio registrarBarrio(String nombre, String direccion) {
        Barrio barrio = new Barrio(contadorBarrios++, nombre, direccion);
        barrios.add(barrio);
        guardarDatosSiCorresponde();
        return barrio;
    }

    public Vivienda registrarVivienda(Barrio barrio, String lote, String direccion) {
        Vivienda vivienda = new Vivienda(contadorViviendas++, lote, direccion);
        viviendas.add(vivienda);

        if (barrio != null) {
            barrio.agregarVivienda(vivienda);
        }

        guardarDatosSiCorresponde();
        return vivienda;
    }

    public Residente registrarResidente(
            String nombre,
            String apellido,
            String dni,
            String email,
            String telefono,
            Vivienda vivienda
    ) {
        Residente residente = new Residente(
                contadorResidentes++,
                nombre,
                apellido,
                dni,
                email,
                telefono
        );

        residentes.add(residente);

        if (vivienda != null) {
            vivienda.agregarResidente(residente);
        }

        guardarDatosSiCorresponde();
        return residente;
    }

    public Visitante registrarVisitante(
            Residente residente,
            String nombre,
            String dni,
            String patente,
            String motivoVisita
    ) {
        Visitante visitante = new Visitante(
                contadorVisitantes++,
                nombre,
                dni,
                patente,
                motivoVisita
        );

        visitantes.add(visitante);

        if (residente != null) {
            residente.registrarVisitante(visitante);
        }

        guardarDatosSiCorresponde();
        return visitante;
    }

    // ─────────────────────────────────────────────
    // Observadores globales
    // ─────────────────────────────────────────────

    public void registrarObservadorGlobal(IObservador observador) {
        observadoresGlobales.add(observador);
        System.out.println("Observador global registrado en el sistema.");
    }

    public void quitarObservadorGlobal(IObservador observador) {
        observadoresGlobales.remove(observador);
    }

    private void suscribirObservadoresGlobales(Solicitud solicitud) {
        for (IObservador observador : observadoresGlobales) {
            solicitud.agregarObservador(observador);
        }
    }

    // ─────────────────────────────────────────────
    // Creación de solicitudes con Factory
    // ─────────────────────────────────────────────

    public Reclamo crearReclamo(String nombre, String descripcion, String prioridad, String tipoReclamo) {
        Reclamo reclamo = (Reclamo) SolicitudFactory.crearSolicitud(
                "RECLAMO",
                contadorSolicitudes++,
                nombre,
                descripcion,
                prioridad,
                tipoReclamo
        );

        suscribirObservadoresGlobales(reclamo);
        solicitudes.add(reclamo);

        System.out.println("Reclamo creado: " + reclamo);
        notificar("Se creó el reclamo [" + reclamo.getId() + "]: " + descripcion, "Sistema");

        guardarDatosSiCorresponde();
        return reclamo;
    }

    public TareaMantenimiento crearTareaMantenimiento(
            String nombre,
            String descripcion,
            String prioridad,
            String sector
    ) {
        TareaMantenimiento tarea = (TareaMantenimiento) SolicitudFactory.crearSolicitud(
                "TAREA_MANTENIMIENTO",
                contadorSolicitudes++,
                nombre,
                descripcion,
                prioridad,
                sector
        );

        suscribirObservadoresGlobales(tarea);
        solicitudes.add(tarea);

        System.out.println("Tarea de mantenimiento creada: " + tarea);
        notificar("Se creó la tarea [" + tarea.getId() + "]: " + descripcion, "Sistema");

        guardarDatosSiCorresponde();
        return tarea;
    }

    public IncidenteSeguridad crearIncidenteSeguridad(
            String nombre,
            String descripcion,
            String prioridad,
            String nivelRiesgo
    ) {
        IncidenteSeguridad incidente = (IncidenteSeguridad) SolicitudFactory.crearSolicitud(
                "INCIDENTE_SEGURIDAD",
                contadorSolicitudes++,
                nombre,
                descripcion,
                prioridad,
                nivelRiesgo
        );

        suscribirObservadoresGlobales(incidente);
        solicitudes.add(incidente);

        System.out.println("Incidente de seguridad creado: " + incidente);
        notificar("Se creó el incidente [" + incidente.getId() + "]: " + descripcion, "Sistema");

        guardarDatosSiCorresponde();
        return incidente;
    }

    // ─────────────────────────────────────────────
    // Gestión de estados
    // ─────────────────────────────────────────────

    public void avanzarEstadoSolicitud(Solicitud solicitud) {
        if (solicitud == null) {
            System.out.println("No se puede avanzar una solicitud nula.");
            return;
        }

        System.out.println("Avanzando solicitud [" + solicitud.getId() + "]...");
        solicitud.avanzar();
        guardarDatosSiCorresponde();
    }

    public void cancelarSolicitud(Solicitud solicitud) {
        if (solicitud == null) {
            System.out.println("No se puede cancelar una solicitud nula.");
            return;
        }

        System.out.println("Cancelando solicitud [" + solicitud.getId() + "]...");
        solicitud.cancelar();
        guardarDatosSiCorresponde();
    }

    public Solicitud buscarSolicitudPorId(int id) {
        for (Solicitud solicitud : solicitudes) {
            if (solicitud.getId() == id) {
                return solicitud;
            }
        }

        System.out.println("No se encontró solicitud con ID: " + id);
        return null;
    }

    // ─────────────────────────────────────────────
    // Accesos
    // ─────────────────────────────────────────────

    public Acceso registrarAcceso(String nombreVisitante, String dniVisitante) {
        Acceso acceso = new Acceso(contadorAccesos++, nombreVisitante, dniVisitante);
        acceso.registrarIngreso();

        accesos.add(acceso);

        notificar("Ingreso registrado: " + nombreVisitante, "Seguridad");

        guardarDatosSiCorresponde();
        return acceso;
    }

    public void registrarEgresoAcceso(Acceso acceso) {
        if (acceso == null) {
            System.out.println("No se puede registrar egreso de un acceso nulo.");
            return;
        }

        acceso.registrarEgreso();
        notificar("Egreso registrado: " + acceso.getNombreVisitante(), "Seguridad");
        guardarDatosSiCorresponde();
    }

    // ─────────────────────────────────────────────
    // Notificaciones
    // ─────────────────────────────────────────────

    public Notificacion notificar(String mensaje, String destinatario) {
        Notificacion notificacion = new Notificacion(contadorNotificaciones++, mensaje, destinatario);
        notificacion.enviar();

        notificaciones.add(notificacion);

        return notificacion;
    }

    // ─────────────────────────────────────────────
    // Persistencia JSON y usuarios
    // ─────────────────────────────────────────────

    public void cargarDesdeJson() {
        persistenciaActiva = false;
        cargarDatosDesdeJson();
        cargarUsuariosDesdeJson();
        persistenciaActiva = true;
    }

    public void cargarUsuariosDesdeJson() {
        usuarios.clear();
        String json = leerArchivoJson("usuarios.json");
        if (json == null) {
            return;
        }

        for (String objeto : extraerObjetosJson(json, "usuarios")) {
            String email = extraerCampoJson(objeto, "email");
            String password = extraerCampoJson(objeto, "password");
            String rol = extraerCampoJson(objeto, "rol");
            String residenteId = extraerCampoJson(objeto, "residenteId");
            if (email != null && password != null && rol != null) {
                usuarios.add(new UsuarioSistema(
                        email,
                        password,
                        rol,
                        residenteId == null || residenteId.isBlank() ? null : Integer.parseInt(residenteId)
                ));
            }
        }
    }

    public UsuarioSistema autenticarUsuario(String email, String password) {
        for (UsuarioSistema usuario : usuarios) {
            if (usuario.getEmail().equalsIgnoreCase(email.trim()) && usuario.getPassword().equals(password)) {
                return usuario;
            }
        }
        return null;
    }

    public void crearCuentaResidenteSiNoExiste(Residente residente, String claveInicial) {
        for (UsuarioSistema usuario : usuarios) {
            if (usuario.getEmail().equalsIgnoreCase(residente.getEmail())) {
                return;
            }
        }
        usuarios.add(new UsuarioSistema(residente.getEmail(), claveInicial, "residente", residente.getId()));
        guardarUsuariosJson();
    }

    private void guardarDatosSiCorresponde() {
        if (persistenciaActiva) {
            guardarDatosJson();
        }
    }

    public void guardarUsuariosJson() {
        StringBuilder json = new StringBuilder();
        json.append("{\n  \"usuarios\": [\n");
        for (int i = 0; i < usuarios.size(); i++) {
            UsuarioSistema usuario = usuarios.get(i);
            json.append("    {\n");
            json.append("      \"email\": \"").append(escaparJson(usuario.getEmail())).append("\",\n");
            json.append("      \"password\": \"").append(escaparJson(usuario.getPassword())).append("\",\n");
            json.append("      \"rol\": \"").append(escaparJson(usuario.getRol())).append("\"");
            if (usuario.getResidenteId() != null) {
                json.append(",\n      \"residenteId\": ").append(usuario.getResidenteId()).append("\n");
            } else {
                json.append("\n");
            }
            json.append("    }").append(i == usuarios.size() - 1 ? "\n" : ",\n");
        }
        json.append("  ]\n}\n");
        escribirArchivoJson("usuarios.json", json.toString());
    }

    public void guardarDatosJson() {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        agregarBarriosJson(json);
        json.append(",\n");
        agregarAdministradoresJson(json);
        json.append(",\n");
        agregarViviendasJson(json);
        json.append(",\n");
        agregarResidentesJson(json);
        json.append(",\n");
        agregarVisitantesJson(json);
        json.append(",\n");
        agregarSolicitudesJson(json);
        json.append(",\n");
        agregarAccesosJson(json);
        json.append("\n}\n");
        escribirArchivoJson("datos.json", json.toString());
    }

    private void cargarDatosDesdeJson() {
        String json = leerArchivoJson("datos.json");
        if (json == null) {
            return;
        }

        Map<Integer, Vivienda> viviendasPorId = new HashMap<>();
        Map<Integer, Residente> residentesPorId = new HashMap<>();

        for (String barrioJson : extraerObjetosJson(json, "barrios")) {
            registrarBarrio(extraerCampoJson(barrioJson, "nombre"), extraerCampoJson(barrioJson, "direccion"));
        }

        for (String adminJson : extraerObjetosJson(json, "administradores")) {
            Administrador admin = new Administrador(
                    Integer.parseInt(extraerCampoJson(adminJson, "id")),
                    extraerCampoJson(adminJson, "nombre"),
                    extraerCampoJson(adminJson, "email")
            );
            administradores.add(admin);
            registrarObservadorGlobal(admin);
        }

        Barrio barrioPrincipal = barrios.isEmpty() ? null : barrios.get(0);
        for (String viviendaJson : extraerObjetosJson(json, "viviendas")) {
            Vivienda vivienda = registrarVivienda(
                    barrioPrincipal,
                    extraerCampoJson(viviendaJson, "lote"),
                    extraerCampoJson(viviendaJson, "direccion")
            );
            viviendasPorId.put(Integer.parseInt(extraerCampoJson(viviendaJson, "id")), vivienda);
        }

        for (String residenteJson : extraerObjetosJson(json, "residentes")) {
            Integer viviendaId = Integer.parseInt(extraerCampoJson(residenteJson, "viviendaId"));
            Residente residente = registrarResidente(
                    extraerCampoJson(residenteJson, "nombre"),
                    extraerCampoJson(residenteJson, "apellido"),
                    extraerCampoJson(residenteJson, "dni"),
                    extraerCampoJson(residenteJson, "email"),
                    extraerCampoJson(residenteJson, "telefono"),
                    viviendasPorId.get(viviendaId)
            );
            residentesPorId.put(Integer.parseInt(extraerCampoJson(residenteJson, "id")), residente);
        }

        for (String visitanteJson : extraerObjetosJson(json, "visitantes")) {
            Integer residenteId = Integer.parseInt(extraerCampoJson(visitanteJson, "residenteId"));
            registrarVisitante(
                    residentesPorId.get(residenteId),
                    extraerCampoJson(visitanteJson, "nombre"),
                    extraerCampoJson(visitanteJson, "dni"),
                    extraerCampoJson(visitanteJson, "patente"),
                    extraerCampoJson(visitanteJson, "motivoVisita")
            );
        }

        for (String solicitudJson : extraerObjetosJson(json, "solicitudes")) {
            Solicitud solicitud = crearSolicitudDesdeJson(solicitudJson);
            aplicarEstadoGuardado(solicitud, extraerCampoJson(solicitudJson, "estado"));
        }

        for (String accesoJson : extraerObjetosJson(json, "accesos")) {
            Acceso acceso = registrarAcceso(
                    extraerCampoJson(accesoJson, "nombreVisitante"),
                    extraerCampoJson(accesoJson, "dniVisitante")
            );
            if (extraerBooleanoJson(accesoJson, "finalizado")) {
                registrarEgresoAcceso(acceso);
            }
        }
    }

    private Solicitud crearSolicitudDesdeJson(String solicitudJson) {
        String tipo = extraerCampoJson(solicitudJson, "tipo");
        if ("reclamo".equalsIgnoreCase(tipo)) {
            return crearReclamo(
                    extraerCampoJson(solicitudJson, "nombre"),
                    extraerCampoJson(solicitudJson, "descripcion"),
                    extraerCampoJson(solicitudJson, "prioridad"),
                    extraerCampoJson(solicitudJson, "detalle")
            );
        }
        if ("tarea".equalsIgnoreCase(tipo)) {
            return crearTareaMantenimiento(
                    extraerCampoJson(solicitudJson, "nombre"),
                    extraerCampoJson(solicitudJson, "descripcion"),
                    extraerCampoJson(solicitudJson, "prioridad"),
                    extraerCampoJson(solicitudJson, "detalle")
            );
        }
        return crearIncidenteSeguridad(
                extraerCampoJson(solicitudJson, "nombre"),
                extraerCampoJson(solicitudJson, "descripcion"),
                extraerCampoJson(solicitudJson, "prioridad"),
                extraerCampoJson(solicitudJson, "detalle")
        );
    }

    private void aplicarEstadoGuardado(Solicitud solicitud, String estado) {
        if (solicitud == null || estado == null) {
            return;
        }
        if ("Asignado".equalsIgnoreCase(estado)) {
            solicitud.setEstado(new EstadoAsignado());
        } else if ("En proceso".equalsIgnoreCase(estado)) {
            solicitud.setEstado(new EstadoEnProceso());
        } else if ("Resuelto".equalsIgnoreCase(estado)) {
            solicitud.setEstado(new EstadoResuelto());
        } else if ("Cerrado".equalsIgnoreCase(estado)) {
            solicitud.setEstado(new EstadoCerrado());
        } else {
            solicitud.setEstado(new EstadoPendiente());
        }
    }

    private Path resolverRutaJson(String archivo) {
        List<Path> rutas = List.of(
                Path.of("eBarrio/src/data/" + archivo),
                Path.of("src/data/" + archivo),
                Path.of("data/" + archivo)
        );
        for (Path ruta : rutas) {
            if (Files.exists(ruta)) {
                return ruta;
            }
        }
        return rutas.get(0);
    }

    private String leerArchivoJson(String archivo) {
        Path ruta = resolverRutaJson(archivo);
        if (ruta == null || !Files.exists(ruta)) {
            return null;
        }
        try {
            return Files.readString(ruta, StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.out.println("No se pudo leer " + ruta + ": " + e.getMessage());
            return null;
        }
    }

    private void escribirArchivoJson(String archivo, String contenido) {
        Path ruta = resolverRutaJson(archivo);
        try {
            Files.createDirectories(ruta.getParent());
            Files.writeString(ruta, contenido, StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.out.println("No se pudo guardar " + ruta + ": " + e.getMessage());
        }
    }

    private List<String> extraerObjetosJson(String json, String seccion) {
        List<String> objetos = new ArrayList<>();
        Matcher inicioSeccion = Pattern.compile("\"" + seccion + "\"\\s*:\\s*\\[").matcher(json);
        if (!inicioSeccion.find()) {
            return objetos;
        }

        int inicio = inicioSeccion.end();
        int profundidad = 1;
        int fin = inicio;
        while (fin < json.length() && profundidad > 0) {
            char caracter = json.charAt(fin);
            if (caracter == '[') {
                profundidad++;
            } else if (caracter == ']') {
                profundidad--;
            }
            fin++;
        }

        if (profundidad != 0) {
            return objetos;
        }

        String contenido = json.substring(inicio, fin - 1);
        Matcher matcher = Pattern.compile("\\{([^{}]*)}").matcher(contenido);
        while (matcher.find()) {
            objetos.add(matcher.group(1));
        }
        return objetos;
    }

    private String extraerCampoJson(String objeto, String campo) {
        Matcher texto = Pattern.compile("\"" + campo + "\"\\s*:\\s*\"([^\"]*)\"").matcher(objeto);
        if (texto.find()) {
            return texto.group(1);
        }
        Matcher numero = Pattern.compile("\"" + campo + "\"\\s*:\\s*(\\d+)").matcher(objeto);
        return numero.find() ? numero.group(1) : null;
    }

    private boolean extraerBooleanoJson(String objeto, String campo) {
        Matcher valor = Pattern.compile("\"" + campo + "\"\\s*:\\s*(true|false)").matcher(objeto);
        return valor.find() && Boolean.parseBoolean(valor.group(1));
    }

    private String escaparJson(String valor) {
        if (valor == null) {
            return "";
        }
        return valor.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "");
    }

    private void agregarBarriosJson(StringBuilder json) {
        json.append("  \"barrios\": [\n");
        for (int i = 0; i < barrios.size(); i++) {
            Barrio barrio = barrios.get(i);
            json.append("    {\n");
            json.append("      \"id\": ").append(barrio.getId()).append(",\n");
            json.append("      \"nombre\": \"").append(escaparJson(barrio.getNombre())).append("\",\n");
            json.append("      \"direccion\": \"").append(escaparJson(barrio.getDireccion())).append("\"\n");
            json.append("    }").append(i == barrios.size() - 1 ? "\n" : ",\n");
        }
        json.append("  ]");
    }

    private void agregarAdministradoresJson(StringBuilder json) {
        json.append("  \"administradores\": [\n");
        for (int i = 0; i < administradores.size(); i++) {
            Administrador admin = administradores.get(i);
            json.append("    {\n");
            json.append("      \"id\": ").append(admin.getId()).append(",\n");
            json.append("      \"nombre\": \"").append(escaparJson(admin.getNombre())).append("\",\n");
            json.append("      \"email\": \"").append(escaparJson(admin.getEmail())).append("\"\n");
            json.append("    }").append(i == administradores.size() - 1 ? "\n" : ",\n");
        }
        json.append("  ]");
    }

    private void agregarViviendasJson(StringBuilder json) {
        json.append("  \"viviendas\": [\n");
        for (int i = 0; i < viviendas.size(); i++) {
            Vivienda vivienda = viviendas.get(i);
            json.append("    {\n");
            json.append("      \"id\": ").append(vivienda.getId()).append(",\n");
            json.append("      \"lote\": \"").append(escaparJson(vivienda.getLote())).append("\",\n");
            json.append("      \"direccion\": \"").append(escaparJson(vivienda.getDireccion())).append("\"\n");
            json.append("    }").append(i == viviendas.size() - 1 ? "\n" : ",\n");
        }
        json.append("  ]");
    }

    private void agregarResidentesJson(StringBuilder json) {
        json.append("  \"residentes\": [\n");
        for (int i = 0; i < residentes.size(); i++) {
            Residente residente = residentes.get(i);
            Vivienda vivienda = residente.getVivienda();
            json.append("    {\n");
            json.append("      \"id\": ").append(residente.getId()).append(",\n");
            json.append("      \"nombre\": \"").append(escaparJson(residente.getNombre())).append("\",\n");
            json.append("      \"apellido\": \"").append(escaparJson(residente.getApellido())).append("\",\n");
            json.append("      \"dni\": \"").append(escaparJson(residente.getDni())).append("\",\n");
            json.append("      \"email\": \"").append(escaparJson(residente.getEmail())).append("\",\n");
            json.append("      \"telefono\": \"").append(escaparJson(residente.getTelefono())).append("\",\n");
            json.append("      \"viviendaId\": ").append(vivienda == null ? 0 : vivienda.getId()).append("\n");
            json.append("    }").append(i == residentes.size() - 1 ? "\n" : ",\n");
        }
        json.append("  ]");
    }

    private void agregarVisitantesJson(StringBuilder json) {
        int total = 0;
        for (Residente residente : residentes) {
            total += residente.getVisitantes().size();
        }
        int escrito = 0;
        json.append("  \"visitantes\": [\n");
        for (Residente residente : residentes) {
            for (Visitante visitante : residente.getVisitantes()) {
                escrito++;
                json.append("    {\n");
                json.append("      \"id\": ").append(visitante.getId()).append(",\n");
                json.append("      \"residenteId\": ").append(residente.getId()).append(",\n");
                json.append("      \"nombre\": \"").append(escaparJson(visitante.getNombre())).append("\",\n");
                json.append("      \"dni\": \"").append(escaparJson(visitante.getDni())).append("\",\n");
                json.append("      \"patente\": \"").append(escaparJson(visitante.getPatente())).append("\",\n");
                json.append("      \"motivoVisita\": \"").append(escaparJson(visitante.getMotivoVisita())).append("\"\n");
                json.append("    }").append(escrito == total ? "\n" : ",\n");
            }
        }
        json.append("  ]");
    }

    private void agregarSolicitudesJson(StringBuilder json) {
        json.append("  \"solicitudes\": [\n");
        for (int i = 0; i < solicitudes.size(); i++) {
            Solicitud solicitud = solicitudes.get(i);
            json.append("    {\n");
            json.append("      \"id\": ").append(solicitud.getId()).append(",\n");
            json.append("      \"tipo\": \"").append(tipoSolicitud(solicitud)).append("\",\n");
            json.append("      \"nombre\": \"").append(escaparJson(solicitud.getNombre())).append("\",\n");
            json.append("      \"descripcion\": \"").append(escaparJson(solicitud.getDescripcion())).append("\",\n");
            json.append("      \"prioridad\": \"").append(escaparJson(solicitud.getPrioridad())).append("\",\n");
            json.append("      \"detalle\": \"").append(escaparJson(detalleSolicitud(solicitud))).append("\",\n");
            json.append("      \"estado\": \"").append(escaparJson(solicitud.getEstado().mostrarNombreEstado())).append("\"\n");
            json.append("    }").append(i == solicitudes.size() - 1 ? "\n" : ",\n");
        }
        json.append("  ]");
    }

    private String tipoSolicitud(Solicitud solicitud) {
        if (solicitud instanceof Reclamo) {
            return "reclamo";
        }
        if (solicitud instanceof TareaMantenimiento) {
            return "tarea";
        }
        return "incidente";
    }

    private String detalleSolicitud(Solicitud solicitud) {
        if (solicitud instanceof Reclamo) {
            return ((Reclamo) solicitud).getTipoReclamo();
        }
        if (solicitud instanceof TareaMantenimiento) {
            return ((TareaMantenimiento) solicitud).getSector();
        }
        if (solicitud instanceof IncidenteSeguridad) {
            return ((IncidenteSeguridad) solicitud).getNivelRiesgo();
        }
        return "";
    }

    private void agregarAccesosJson(StringBuilder json) {
        json.append("  \"accesos\": [\n");
        for (int i = 0; i < accesos.size(); i++) {
            Acceso acceso = accesos.get(i);
            json.append("    {\n");
            json.append("      \"id\": ").append(acceso.getId()).append(",\n");
            json.append("      \"nombreVisitante\": \"").append(escaparJson(acceso.getNombreVisitante())).append("\",\n");
            json.append("      \"dniVisitante\": \"").append(escaparJson(acceso.getDniVisitante())).append("\",\n");
            json.append("      \"finalizado\": ").append(!acceso.estaActivo()).append("\n");
            json.append("    }").append(i == accesos.size() - 1 ? "\n" : ",\n");
        }
        json.append("  ]");
    }

    // ─────────────────────────────────────────────
    // Consultas por consola
    // ─────────────────────────────────────────────

    public void listarSolicitudes() {
        System.out.println("\n=== SOLICITUDES ===");

        if (solicitudes.isEmpty()) {
            System.out.println("No hay solicitudes registradas.");
        } else {
            for (Solicitud solicitud : solicitudes) {
                System.out.println(solicitud);
            }
        }

        System.out.println("==================\n");
    }

    public void listarAccesos() {
        System.out.println("\n=== ACCESOS ===");

        if (accesos.isEmpty()) {
            System.out.println("No hay accesos registrados.");
        } else {
            for (Acceso acceso : accesos) {
                System.out.println(acceso);
            }
        }

        System.out.println("===============\n");
    }

    // ─────────────────────────────────────────────
    // Getters
    // ─────────────────────────────────────────────

    public List<Barrio> getBarrios() {
        return barrios;
    }

    public List<Vivienda> getViviendas() {
        return viviendas;
    }

    public List<Residente> getResidentes() {
        return residentes;
    }

    public List<Visitante> getVisitantes() {
        return visitantes;
    }

    public List<Solicitud> getSolicitudes() {
        return solicitudes;
    }

    public List<Acceso> getAccesos() {
        return accesos;
    }

    public List<Notificacion> getNotificaciones() {
        return notificaciones;
    }

    public List<UsuarioSistema> getUsuarios() {
        return usuarios;
    }
}
