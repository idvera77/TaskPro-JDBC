package com.taskpro.model;

import java.time.LocalDateTime;

public class Usuario {
    private long id;
    private String username;
    private String email;
    private String password;
    private long rolId;
    private LocalDateTime fechaCreacion;

    public Usuario(long id, String username, String email, String password, long rolId,
                   LocalDateTime fechaCreacion) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.rolId = rolId;
        this.fechaCreacion = fechaCreacion;
    }

    public Usuario(String username, String email, String password, long rolId,
                   LocalDateTime fechaCreacion) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.rolId = rolId;
        this.fechaCreacion = fechaCreacion;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getRolId() {
        return rolId;
    }

    public void setRolId(long rolId) {
        this.rolId = rolId;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", mail='" + email + '\'' +
                ", password='" + password + '\'' +
                ", rolId=" + rolId +
                ", fechaCreacion=" + fechaCreacion +
                '}';
    }

    public boolean tienePermisoGestion() {
        return this.rolId == 1 || this.rolId == 2;
    }

    public boolean esUsuarioOperativo() {
        // El usuario que solo puede tocar lo suyo es el 3
        return this.rolId == 3;
    }
}
