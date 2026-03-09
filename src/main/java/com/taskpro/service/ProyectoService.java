package com.taskpro.service;

import com.taskpro.dao.ProyectoDAO;
import com.taskpro.model.Proyecto;
import com.taskpro.model.Usuario;

public class ProyectoService {
    private final ProyectoDAO proyectoDAO;

    public ProyectoService() {
        this.proyectoDAO = new ProyectoDAO();
    }

    /**
     * Intenta crear un proyecto, pero primero valida si el usuario tiene permisos.
     * Retorna un mensaje (String) con el resultado de la operación para que la UI lo imprima.
     */
    public String crearNuevoProyecto(Proyecto nuevoProyecto, Usuario usuarioActual) {
        // 1. REGLA DE NEGOCIO: Solo ADMIN (1) y GESTOR (2) pueden crear proyectos.
        if (!usuarioActual.tienePermisoGestion()) {
            return "ACCESO DENEGADO: Tu rol no tiene permisos para crear proyectos.";
        }

        // 2. REGLA DE NEGOCIO: El nombre del proyecto no puede estar vacío
        if (nuevoProyecto.getNombre() == null || nuevoProyecto.getNombre().trim().isEmpty()) {
            return "ERROR: El nombre del proyecto no puede estar vacío.";
        }

        // 3. Si pasa las validaciones, le damos la orden al DAO
        boolean exito = proyectoDAO.guardar(nuevoProyecto);

        if (exito) {
            return "¡ÉXITO! El proyecto '" + nuevoProyecto.getNombre() + "' ha sido creado correctamente.";
        } else {
            return "ERROR INTERNO: No se pudo guardar el proyecto en la base de datos.";
        }
    }
}