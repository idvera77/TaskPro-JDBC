package com.taskpro.service;

import com.taskpro.dao.ProyectoDAO;
import com.taskpro.exception.*;
import com.taskpro.model.*;
import com.taskpro.model.enums.EstadoProyecto;

public class ProyectoService {
    private final ProyectoDAO proyectoDAO;

    /**
     * Inicializa el servicio de proyectos y su acceso a datos.
     */
    public ProyectoService() {
        this.proyectoDAO = new ProyectoDAO();
    }

    /**
     * Valida y procesa la creación de un nuevo proyecto.
     *
     * @param nuevoProyecto Objeto con la información del proyecto a crear.
     * @param usuarioActual Usuario que intenta realizar la operación.
     * @return Mensaje de confirmación o descripción del error encontrado.
     */
    public String crearNuevoProyecto(Proyecto nuevoProyecto, Usuario usuarioActual) {
        if (!usuarioActual.esAdmin()) {
            throw new AccessDeniedException("Tu rol no tiene permisos para crear proyectos.");
        }

        if (nuevoProyecto.getNombre() == null || nuevoProyecto.getNombre().trim().isEmpty()) {
            throw new ValidationException("El nombre del proyecto no puede estar vacío.");
        }

        boolean exito = proyectoDAO.guardar(nuevoProyecto);

        if (!exito) {
            throw new DatabaseException("Error técnico: No se pudo guardar el proyecto.");
        }

        return "¡ÉXITO! El proyecto '" + nuevoProyecto.getNombre() + "' ha sido creado.";
    }

    /**
     * Gestiona el cambio de estado de un proyecto existente previa validación de permisos.
     *
     * @param proyectoId Identificador del proyecto a modificar.
     * @param nuevoEstado Estado de destino para el proyecto.
     * @param usuarioActual Usuario que solicita el cambio.
     * @return Mensaje indicando el éxito de la operación o la causa del fallo.
     */
    public String cambiarEstadoProyecto(long proyectoId, EstadoProyecto nuevoEstado,
                                        Usuario usuarioActual) {
        Proyecto proyectoExistente = proyectoDAO.buscarPorId(proyectoId);
        if (proyectoExistente == null) {
            throw new ResourceNotFoundException("No se encontró el proyecto con ID " + proyectoId);
        }

        if (usuarioActual.esUsuarioEstandar()) {
            throw new AccessDeniedException("Solo los administradores pueden cambiar el estado de un proyecto.");
        }

        boolean exito = proyectoDAO.actualizarEstado(proyectoId, nuevoEstado);

        if (!exito) {
            throw new DatabaseException("Error técnico al actualizar el proyecto.");
        }

        return "EXITO: El proyecto '" + proyectoExistente.getNombre() + "' ahora está " + nuevoEstado.name() + ".";
    }
}