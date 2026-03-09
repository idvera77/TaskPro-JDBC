package com.taskpro.service;

import com.taskpro.dao.TareaDAO;
import com.taskpro.model.Tarea;
import com.taskpro.model.Usuario;

import java.util.List;

public class TareaService {
    private final TareaDAO tareaDAO;

    public TareaService() {
        this.tareaDAO = new TareaDAO();
    }

    public String crearNuevaTarea(Tarea nuevaTarea, Usuario usuarioActual) {
        // REGLA 0: Validar permisos (Solo Admin y Gestor pueden crear tareas)
        if (!usuarioActual.tienePermisoGestion()) {
            return "ERROR: No tienes permisos para crear tareas.";
        }

        // REGLA 1: Validar que el título no este vacío
        if (nuevaTarea.getTitulo() == null || nuevaTarea.getTitulo().trim().isEmpty()) {
            return "ERROR: El titulo de la tarea no puede estar vacío.";
        }

        // REGLA 2: Validar que la fecha limite no sea nula
        if (nuevaTarea.getFechaLimite() == null) {
            return "ERROR: La fecha limite es obligatoria.";
        }

        // Si todo esta bien, mandamos a guardar
        boolean exito = tareaDAO.guardar(nuevaTarea);

        if (exito) {
            return "EXITO: La tarea '" + nuevaTarea.getTitulo() + "' ha sido creada correctamente.";
        } else {
            return "ERROR INTERNO: No se pudo guardar la tarea en la base de datos.";
        }
    }

    public String asignarUsuarioATarea(long tareaId, long usuarioAsignadoId, Usuario usuarioActual) {
        // Validamos que quien intenta asignar sea Admin (1) o Gestor (2)
        if (!usuarioActual.tienePermisoGestion()) {
            return "ERROR: No tienes permisos para crear tareas.";
        }

        // Llamamos al DAO que inserta en la tabla intermedia tarea_asignaciones
        boolean exito = tareaDAO.asignarUsuario(tareaId, usuarioAsignadoId);

        if (exito) {
            return "EXITO: Tarea " + tareaId + " asignada correctamente al usuario " + usuarioAsignadoId + ".";
        } else {
            return "ERROR: No se pudo asignar la tarea. Comprueba que los IDs existan.";
        }
    }

    public String cambiarEstadoTarea(long tareaId, com.taskpro.model.enums.EstadoTarea nuevoEstado,
                                     Usuario usuarioActual) {
        // 1. Verificamos que la tarea realmente existe
        Tarea tareaExistente = tareaDAO.buscarPorId(tareaId);
        if (tareaExistente == null) {
            return "ERROR: No se encontró ninguna tarea con el ID " + tareaId + ".";
        }

        // 2. Control de Propiedad para usuarios normales (Rol 3)
        if (usuarioActual.esUsuarioOperativo()) {
            java.util.List<Tarea> tareasDelUsuario = tareaDAO.listarPorUsuario(usuarioActual.getId());
            boolean esSuya = false;

            for (Tarea t : tareasDelUsuario) {
                if (t.getId() == tareaId) {
                    esSuya = true;
                    break;
                }
            }

            if (!esSuya) {
                return "ACCESO DENEGADO: Operación bloqueada. Solo puedes modificar las tareas que tienes asignadas.";
            }
        }

        // 3. Guardamos el estado anterior para el historial
        String estadoAnterior = tareaExistente.getEstado().name();

        // 4. Ejecutamos la actualización en la base de datos
        boolean exito = tareaDAO.actualizarEstado(tareaId, nuevoEstado);

        if (exito) {
            // 5. Generamos el texto y llamamos al DAO con los 2 argumentos que espera
            String descripcionCambio = "El usuario " + usuarioActual.getUsername() +
                    " cambio el estado: " + estadoAnterior + " -> " + nuevoEstado.name();

            tareaDAO.registrarHistorial(tareaId, descripcionCambio);

            return "EXITO: El estado de la tarea '" + tareaExistente.getTitulo() + "' ha cambiado" +
                    " a " + nuevoEstado.name() + ".";
        } else {
            return "ERROR INTERNO: Fallo de comunicación con la base de datos.";
        }
    }

    public List<String> consultarHistorialTarea(long tareaId, Usuario usuarioActual) {
        if (!usuarioActual.tienePermisoGestion()) return null;
        return tareaDAO.obtenerHistorial(tareaId);
    }
}