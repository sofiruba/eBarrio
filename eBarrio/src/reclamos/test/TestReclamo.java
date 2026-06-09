package reclamos.test;

import reclamos.reclamo.Reclamo;

public class TestReclamo {
    public static void main(String[] args) {

        Reclamo r1 = new Reclamo("R01",
                "Iluminacion faltante en sector G",
                "La luminaria del sector G presenta deterioros considerables y requiere su arreglo."
        );


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
        Reclamo r2 = new Reclamo("R02",
                "Bache en sector paredon",
                "Se presenta un bache de gran magnitud en la calle del paredon. Se requiere su arreglo."
        );

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