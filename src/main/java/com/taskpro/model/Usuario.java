package com.taskpro.model;

import com.taskpro.model.enums.NombreRol;
import java.time.LocalDateTime;

public class Usuario {
    private long id;
    private String username;
    private String email;
    private String password;
    private NombreRol rol;
    private LocalDateTime fechaCreacion;

    /**
     * Constructor completo para representar un usuario cargado desde la base de datos.
     *
     * @param id Identificador único del usuario.
     * @param username Nombre de usuario para el sistema.
     * @param email Correo electrónico único.
     * @param password Contraseña (hash) del usuario.
     * @param rol Rol asignado (Enum NombreRol).
     * @param fechaCreacion Fecha y hora de registro en el sistema.
     */
    public Usuario(long id, String username, String email, String password, NombreRol rol,
                   LocalDateTime fechaCreacion) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.rol = rol;
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

    public NombreRol getRol() {
        return rol;
    }

    public void setRol(NombreRol rol) {
        this.rol = rol;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public boolean esAdmin() {
        return this.rol == NombreRol.ADMINISTRADOR;
    }

    public boolean esUsuarioEstandar() {
        return this.rol == NombreRol.USUARIO;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", rol=" + rol +
                ", fechaCreacion=" + fechaCreacion +
                '}';
    }
}