package model.solicitud.personal;

public class PersonalMantenimiento {

    private int id;
    private String nombre;
    private String sector;

    public PersonalMantenimiento(int id, String nombre, String sector) {
        this.id = id;
        this.nombre = nombre;
        this.sector = sector;
    }

    public void consultarTarea(TareaMantenimiento tarea) {
        System.out.println("PersonalMantenimiento " + nombre +
            " consultando: " + tarea);
    }

    public void actualizarEstadoTarea(TareaMantenimiento tarea) {
        System.out.println("PersonalMantenimiento " + nombre +
            " avanzando tarea [" + tarea.getId() + "]");
        tarea.avanzar();
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getSector() { return sector; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setSector(String sector) { this.sector = sector; }

    @Override
    public String toString() {
        return "PersonalMantenimiento [" + id + "] " + nombre + " | Sector: " + sector;
    }
}
