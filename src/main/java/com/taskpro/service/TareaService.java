package com.taskpro.service;

import com.taskpro.dao.TareaDAO;
import com.taskpro.exception.*;
import com.taskpro.model.*;
import com.taskpro.model.enums.EstadoTarea;

import java.util.List;

public class TareaService {
    private final TareaDAO tareaDAO;

    /**
     * Inicializa el servicio de tareas y su acceso a datos.
     */
    public TareaService() {
        this.tareaDAO = new TareaDAO();
    }

    /**
     * Valida y registra una nueva tarea asociada a un proyecto.
     *
     * @param nuevaTarea Objeto con los datos de la tarea a persistir.
     * @param usuarioActual Usuario que intenta realizar la creación.
     * @return Mensaje de éxito o descripción del error de validación.
     */
    public String crearNuevaTarea(Tarea nuevaTarea, Usuario usuarioActual) {
        if (!usuarioActual.esAdmin()) {
            throw new AccessDeniedException("No tienes permisos para crear tareas.");
        }

        if (nuevaTarea.getTitulo() == null || nuevaTarea.getTitulo().trim().isEmpty()) {
            throw new ValidationException("El título de la tarea no puede estar vacío.");
        }

        if (nuevaTarea.getFechaLimite() == null) {
            throw new ValidationException("La fecha límite es obligatoria.");
        }

        boolean exito = tareaDAO.guardar(nuevaTarea);

        if (!exito) {
            throw new DatabaseException("No se pudo guardar la tarea en la base de datos.");
        }

        return "EXITO: La tarea '" + nuevaTarea.getTitulo() + "' ha sido creada correctamente.";
    }

    /**
     * Vincula un usuario operativo con una tarea específica previa validación de permisos.
     *
     * @param tareaId ID de la tarea a asignar.
     * @param usuarioAsignadoId ID del usuario que recibirá la tarea.
     * @param usuarioActual Usuario administrador que realiza la asignación.
     * @return Mensaje indicando el resultado de la operación.
     */
    public String asignarUsuarioATarea(long tareaId, long usuarioAsignadoId, Usuario usuarioActual) {
        if (!usuarioActual.esAdmin()) {
            throw new AccessDeniedException("Solo los administradores pueden asignar tareas.");
        }

        boolean exito = tareaDAO.asignarUsuario(tareaId, usuarioAsignadoId);

        if (!exito) {
            throw new DatabaseException("No se pudo asignar la tarea. Verifica que los IDs sean correctos.");
        }

        return "EXITO: Tarea " + tareaId + " asignada correctamente al usuario " + usuarioAsignadoId + ".";
    }

    /**
     * Gestiona la actualización de progreso de una tarea y registra el cambio en el historial.
     *
     * @param tareaId Identificador de la tarea a modificar.
     * @param nuevoEstado Estado de destino (Enum EstadoTarea).
     * @param usuarioActual Usuario que solicita el cambio (valida si es administrador o asignado).
     * @return Mensaje de confirmación o error de acceso/técnico.
     */
    public String cambiarEstadoTarea(long tareaId, EstadoTarea nuevoEstado, Usuario usuarioActual) {
        Tarea tareaExistente = tareaDAO.buscarPorId(tareaId);
        if (tareaExistente == null) {
            throw new ResourceNotFoundException("No se encontró la tarea con ID " + tareaId);
        }

        if (usuarioActual.esUsuarioEstandar()) {
            boolean estaAsignado = tareaDAO.esUsuarioAsignado(tareaId, usuarioActual.getId());
            if (!estaAsignado) {
                throw new AccessDeniedException("Acceso denegado: No estás asignado a esta tarea.");
            }
        }

        String estadoAnterior = tareaExistente.getEstado().name();

        boolean exito = tareaDAO.actualizarEstado(tareaId, nuevoEstado);

        if (!exito) {
            throw new DatabaseException("Error técnico al actualizar el estado de la tarea.");
        }

        String descripcionCambio = "El usuario " + usuarioActual.getUsername() +
                " cambió el estado: " + estadoAnterior + " -> " + nuevoEstado.name();
        tareaDAO.registrarHistorial(tareaId, descripcionCambio);

        return "EXITO: El estado de la tarea '" + tareaExistente.getTitulo() + "' ahora es " + nuevoEstado.name() + ".";
    }

    /**
     * Recupera el historial de auditoría de una tarea, restringido a administradores.
     *
     * @param tareaId ID de la tarea de la cual se desea ver el historial.
     * @param usuarioActual Usuario que realiza la consulta.
     * @return Lista de mensajes del historial o null si el acceso es denegado.
     */
    public List<String> consultarHistorialTarea(long tareaId, Usuario usuarioActual) {
        if (!usuarioActual.esAdmin()) return null;
        return tareaDAO.obtenerHistorial(tareaId);
    }
}