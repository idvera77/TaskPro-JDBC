package com.taskpro.model;

import com.taskpro.model.enums.EstadoTarea;
import com.taskpro.model.enums.Prioridad;

import java.time.LocalDate;

public class Tarea {
    private long id;
    private long proyectoId;
    private String titulo;
    private String descripcion;
    private Prioridad prioridad;
    private EstadoTarea estado;
    private LocalDate fechaLimite;

    // 1. Constructor para traer Tareas que ya existen
    public Tarea(long id, long proyectoId, String titulo, String descripcion, Prioridad prioridad,
                 EstadoTarea estado, LocalDate fechaLimite) {
        this.id = id;
        this.proyectoId = proyectoId;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.prioridad = prioridad;
        this.estado = estado;
        this.fechaLimite = fechaLimite;
    }

    // 2. Constructor para crear Tareas nuevas
    public Tarea(long proyectoId, String titulo, String descripcion, Prioridad prioridad,
                 EstadoTarea estado, LocalDate fechaLimite) {
        this.proyectoId = proyectoId;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.prioridad = prioridad;
        this.estado = estado;
        this.fechaLimite = fechaLimite;
    }

    // 3. Constructor "Rápido" (Solo lo esencial)
    public Tarea(long proyectoId, String titulo, String descripcion, LocalDate fechaLimite) {
        this(proyectoId, titulo, descripcion, Prioridad.MEDIA, EstadoTarea.BACKLOG, fechaLimite);
    }

    public long getProyectoId() {
        return proyectoId;
    }

    public void setProyectoId(long proyectoId) {
        this.proyectoId = proyectoId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Prioridad getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(Prioridad prioridad) {
        this.prioridad = prioridad;
    }

    public EstadoTarea getEstado() {
        return estado;
    }

    public void setEstado(EstadoTarea estado) {
        this.estado = estado;
    }

    public LocalDate getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(LocalDate fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    @Override
    public String toString() {
        return "Tarea{" +
                "id=" + id +
                ", proyectoId=" + proyectoId +
                ", titulo='" + titulo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", prioridad=" + prioridad +
                ", estado=" + estado +
                ", fechaLimite=" + fechaLimite +
                '}';
    }
}
