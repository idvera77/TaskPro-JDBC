package com.taskpro.service;

import com.taskpro.dao.ProyectoDAO;
import com.taskpro.model.Proyecto;
import com.taskpro.model.Usuario;
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
            return "ACCESO DENEGADO: Tu rol no tiene permisos para crear proyectos.";
        }

        if (nuevoProyecto.getNombre() == null || nuevoProyecto.getNombre().trim().isEmpty()) {
            return "ERROR: El nombre del proyecto no puede estar vacío.";
        }

        boolean exito = proyectoDAO.guardar(nuevoProyecto);

        if (exito) {
            return "¡ÉXITO! El proyecto '" + nuevoProyecto.getNombre() + "' ha sido creado correctamente.";
        } else {
            return "ERROR INTERNO: No se pudo guardar el proyecto en la base de datos.";
        }
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
            return "ERROR: No se encontró el proyecto con ID " + proyectoId + ".";
        }

        if (usuarioActual.esUsuarioEstandar()) {
            return "ACCESO DENEGADO: Solo los administradores pueden cambiar el estado de un proyecto.";
        }

        boolean exito = proyectoDAO.actualizarEstado(proyectoId, nuevoEstado);

        if (exito) {
            return "EXITO: El proyecto '" + proyectoExistente.getNombre() + "' ahora está " + nuevoEstado.name() + ".";
        } else {
            return "ERROR: Fallo técnico al actualizar el proyecto en la base de datos.";
        }
    }
}