package com.taskpro.service;

import com.taskpro.dao.TareaDAO;
import com.taskpro.model.Tarea;
import com.taskpro.model.Usuario;
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
            return "ERROR: No tienes permisos para crear tareas.";
        }

        if (nuevaTarea.getTitulo() == null || nuevaTarea.getTitulo().trim().isEmpty()) {
            return "ERROR: El titulo de la tarea no puede estar vacío.";
        }

        if (nuevaTarea.getFechaLimite() == null) {
            return "ERROR: La fecha limite es obligatoria.";
        }

        boolean exito = tareaDAO.guardar(nuevaTarea);

        if (exito) {
            return "EXITO: La tarea '" + nuevaTarea.getTitulo() + "' ha sido creada correctamente.";
        } else {
            return "ERROR INTERNO: No se pudo guardar la tarea en la base de datos.";
        }
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
            return "ERROR: No tienes permisos para crear tareas.";
        }

        boolean exito = tareaDAO.asignarUsuario(tareaId, usuarioAsignadoId);

        if (exito) {
            return "EXITO: Tarea " + tareaId + " asignada correctamente al usuario " + usuarioAsignadoId + ".";
        } else {
            return "ERROR: No se pudo asignar la tarea. Comprueba que los IDs existan.";
        }
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
            return "ERROR: No se encontró la tarea con ID " + tareaId + ".";
        }

        if (usuarioActual.esUsuarioEstandar()) {
            boolean estaAsignado = tareaDAO.esUsuarioAsignado(tareaId, usuarioActual.getId());

            if (!estaAsignado) {
                return "ACCESO DENEGADO: Solo los usuarios asignados pueden modificar esta tarea.";
            }
        }

        String estadoAnterior = tareaExistente.getEstado().name();

        boolean exito = tareaDAO.actualizarEstado(tareaId, nuevoEstado);

        if (exito) {
            String descripcionCambio = "El usuario " + usuarioActual.getUsername() +
                    " cambió el estado: " + estadoAnterior + " -> " + nuevoEstado.name();

            tareaDAO.registrarHistorial(tareaId, descripcionCambio);

            return "EXITO: El estado de la tarea '" + tareaExistente.getTitulo() + "' ahora es " + nuevoEstado.name() + ".";
        } else {
            return "ERROR: Fallo técnico al actualizar el estado en la base de datos.";
        }
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