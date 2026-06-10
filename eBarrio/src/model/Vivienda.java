package com.ebarrio.model;

import java.util.ArrayList;
import java.util.List;

public class Vivienda {
    private int id;
    private String lote;
    private String direccion;
    private List<Residente> residentes;

    public Vivienda(int id, String lote, String direccion) {
        this.id = id;
        this.lote = lote;
        this.direccion = direccion;
        this.residentes = new ArrayList<>();
    }

    public void agregarResidente(Residente residente) {
        if (residente != null) {
            residentes.add(residente);
            residente.setVivienda(this);
        }
    }

    public List<Residente> obtenerResidentes() {
        return residentes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public List<Residente> getResidentes() {
        return residentes;
    }

    public void setResidentes(List<Residente> residentes) {
        this.residentes = residentes;
    }

    @Override
    public String toString() {
        return "Vivienda{" +
                "id=" + id +
                ", lote='" + lote + '\'' +
                ", direccion='" + direccion + '\'' +
                ", residentes=" + residentes.size() +
                '}';
    }
}
