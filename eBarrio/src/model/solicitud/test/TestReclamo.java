package solicitud.test;

import solicitud.reclamo.Reclamo;
import solicitud.Solicitud;

public class TestReclamo {
    public static void main(String[] args) {

        Solicitud r1 = new Reclamo(1, "R01", 
        "Iluminacion faltante en sector G", 
        "Alta", "Iluminacion");

        System.out.println("Nombre del reclamo recien generado: " + r1.getNombre());
        System.out.println("ID: " + r1.getId());
        System.out.println("Descripcion: " + r1.getDescripcion());
        r1.mostrarEstado();

        System.out.println("--------------------------");

        System.out.println("Probando avanzar de estado:");
        r1.avanzar();
        r1.mostrarEstado();

        System.out.println("--------------------------");

        System.out.println("Probando avanzar de estado:");
        r1.avanzar();
        r1.mostrarEstado();

        System.out.println("--------------------------");

        System.out.println("Probando avanzar de estado:");
        r1.avanzar();
        r1.mostrarEstado();

        System.out.println("--------------------------");

        System.out.println("Probando avanzar de estado:");
        r1.avanzar();
        r1.mostrarEstado();

        System.out.println("--------------------------");

        System.out.println("Probando avanzar de estado:");
        r1.avanzar();
        r1.mostrarEstado();

        System.out.println("\n");
        System.out.println("--------------------------");
        System.out.println("--------------------------");
        System.out.println("\n");


        System.out.println("Creamos otro reclamo:");

        Solicitud r2 = new Reclamo(2, "R02", 
        "Reclamo de falta de agua en sector B", 
        "Alta", "Agua" );
        

        System.out.println("Nombre del reclamo recien generado: " + r2.getNombre());
        System.out.println("ID: " + r2.getId());
        System.out.println("Descripcion: " + r2.getDescripcion());
        r2.mostrarEstado();
        
        System.out.println("Probando cancelarlo desde pendiente:");
        r2.cancelar();

        System.out.println("Probando cancelarlo nuevamente:");
        r2.cancelar();
    }
}