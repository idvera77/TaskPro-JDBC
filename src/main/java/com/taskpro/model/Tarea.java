package com.taskpro.model;

import java.time.LocalDate;

public class Tarea {
    private long id;
    private long proyectoId;
    private String titulo;
    private String descripcion;
    private String prioridad;
    private String estado;
    private LocalDate fechaLimite;

    // 1. Constructor para Tareas que ya existen (vienen de la BD)
    public Tarea(long id, long proyectoId, String titulo, String descripcion,
                 String prioridad, String estado, LocalDate fechaLimite) {
        this.id = id;
        this.proyectoId = proyectoId;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.prioridad = prioridad;
        this.estado = estado;
        this.fechaLimite = fechaLimite;
    }

    // 2. Constructor para Tareas nuevas
    public Tarea(long proyectoId, String titulo, String descripcion,
                 String prioridad, String estado, LocalDate fechaLimite) {
        this.proyectoId = proyectoId;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.prioridad = prioridad;
        this.estado = estado;
        this.fechaLimite = fechaLimite;
    }

    // 3. Constructor "Rápido" (Solo lo esencial)
    public Tarea(long proyectoId, String titulo, String descripcion, LocalDate fechaLimite) {
        this(proyectoId, titulo, descripcion, "MEDIA", "BACKLOG", fechaLimite);
    }

}
