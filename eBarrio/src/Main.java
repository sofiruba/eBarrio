import sistema.SistemaBarrio;

public class Main {

    public static void main(String[] args) {
        System.out.println("============================================");
        System.out.println("       SISTEMA eBarrio - Estado actual      ");
        System.out.println("============================================\n");

        SistemaBarrio sistema = new SistemaBarrio();
        sistema.cargarDesdeJson();

        System.out.println("Barrios cargados: " + sistema.getBarrios().size());
        System.out.println("Viviendas cargadas: " + sistema.getViviendas().size());
        System.out.println("Residentes cargados: " + sistema.getResidentes().size());
        System.out.println("Visitantes cargados: " + sistema.getVisitantes().size());
        System.out.println("Usuarios cargados: " + sistema.getUsuarios().size());
        System.out.println("Accesos cargados: " + sistema.getAccesos().size());
        System.out.println("Notificaciones en memoria: " + sistema.getNotificaciones().size());

        sistema.listarSolicitudes();
        sistema.listarAccesos();

        System.out.println("============================================");
        System.out.println("     Datos leidos desde la base de datos    ");
        System.out.println("============================================");
    }
}
