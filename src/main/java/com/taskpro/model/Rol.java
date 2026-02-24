package com.taskpro.model;

import com.taskpro.model.enums.NombreRol;

public class Rol {
    private long id;
    private NombreRol rol;

    // 1. Constructor para traer Roles que ya existen
    public Rol(long id, NombreRol rol) {
        this.id = id;
        this.rol = rol;
    }

    public NombreRol getRol() {
        return rol;
    }

    public void setRol(NombreRol rol) {
        this.rol = rol;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Rol{" +
                "id=" + id +
                ", rol=" + rol +
                '}';
    }
}
