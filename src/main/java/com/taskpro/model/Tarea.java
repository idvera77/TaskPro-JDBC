package com.taskpro.model;

import com.taskpro.model.enums.*;

import java.time.LocalDate;

public class Tarea {
    private long id;
    private long proyectoId;
    private String titulo;
    private String descripcion;
    private Prioridad prioridad;
    private EstadoTarea estado;
    private LocalDate fechaLimite;

    /**
     * Constructor completo para representar una tarea cargada desde la base de datos.
     *
     * @param id Identificador único de la tarea.
     * @param proyectoId ID del proyecto al que pertenece la tarea.
     * @param titulo Título o nombre de la tarea.
     * @param descripcion Detalle de las acciones a realizar.
     * @param prioridad Nivel de importancia (Enum Prioridad).
     * @param estado Estado actual de progreso (Enum EstadoTarea).
     * @param fechaLimite Fecha máxima para la finalización.
     */
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

    /**
     * Constructor para la creación de una nueva tarea antes de ser persistida.
     *
     * @param proyectoId ID del proyecto al que se asocia la tarea.
     * @param titulo Título descriptivo de la tarea.
     * @param descripcion Descripción de la tarea.
     * @param fechaLimite Fecha establecida como límite.
     */
    public Tarea(long proyectoId, String titulo, String descripcion, LocalDate fechaLimite) {
        this.proyectoId = proyectoId;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaLimite = fechaLimite;
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
