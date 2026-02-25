package com.taskpro.model;

import com.taskpro.model.enums.EstadoProyecto;

public class Proyecto {
    private long id;
    private String nombre;
    private String descripcion;
    private EstadoProyecto estado;
    private long creadorId;

    public Proyecto(long id, String nombre, String descripcion,
                    EstadoProyecto estado, long creadorId) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estado = estado;
        this.creadorId = creadorId;
    }

    public Proyecto(String nombre, String descripcion, EstadoProyecto estado, long creadorId) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estado = estado;
        this.creadorId = creadorId;
    }

    public Proyecto(String nombre, String descripcion, long creadorId) {
        this(nombre, descripcion, EstadoProyecto.ACTIVO, creadorId);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public EstadoProyecto getEstado() {
        return estado;
    }

    public void setEstado(EstadoProyecto estado) {
        this.estado = estado;
    }

    public long getCreadorId() {
        return creadorId;
    }

    public void setCreadorId(long creadorId) {
        this.creadorId = creadorId;
    }

    @Override
    public String toString() {
        return "Proyecto{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", estado=" + estado +
                ", creadorId=" + creadorId +
                '}';
    }
}