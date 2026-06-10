package model.accesos;
public class Visitante {
    private int id;
    private String nombre;
    private String dni;
    private String patente;
    private String motivoVisita;

    public Visitante(int id, String nombre, String dni, String patente, String motivoVisita) {
        this.id = id;
        this.nombre = nombre;
        this.dni = dni;
        this.patente = patente;
        this.motivoVisita = motivoVisita;
    }

    public String mostrarDatos() {
        return nombre + " - DNI: " + dni + " - Motivo: " + motivoVisita;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getPatente() {
        return patente;
    }

    public void setPatente(String patente) {
        this.patente = patente;
    }

    public String getMotivoVisita() {
        return motivoVisita;
    }

    public void setMotivoVisita(String motivoVisita) {
        this.motivoVisita = motivoVisita;
    }

    @Override
    public String toString() {
        return "Visitante{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", dni='" + dni + '\'' +
                ", patente='" + patente + '\'' +
                ", motivoVisita='" + motivoVisita + '\'' +
                '}';
    }
}
