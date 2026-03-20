package com.taskpro.dao;

import com.taskpro.config.DatabaseConnection;
import com.taskpro.exception.DatabaseException;
import com.taskpro.model.Tarea;
import com.taskpro.model.enums.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TareaDAO {

    /**
     * Inserta una nueva tarea en la base de datos.
     *
     * @param tarea Objeto Tarea con la información a registrar.
     * @return true si la fila fue insertada con éxito.
     * @throws DatabaseException Si ocurre un error técnico en la inserción o conexión.
     */
    public boolean guardar(Tarea tarea) {
        String sql = "INSERT INTO tareas (proyecto_id, titulo, descripcion, fecha_limite) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, tarea.getProyectoId());
            stmt.setString(2, tarea.getTitulo());
            stmt.setString(3, tarea.getDescripcion());
            stmt.setDate(4, Date.valueOf(tarea.getFechaLimite()));

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error al intentar guardar la tarea: " + tarea.getTitulo(), e);
        }
    }

    /**
     * Registra una asignación entre una tarea y un usuario en la tabla intermedia.
     *
     * @param tareaId Identificador de la tarea.
     * @param usuarioId Identificador del usuario.
     * @return true si la asignación se realizó correctamente.
     * @throws DatabaseException Si falla la restricción de claves foráneas o la comunicación con la DB.
     */
    public boolean asignarUsuario(long tareaId, long usuarioId) {
        String sql = "INSERT INTO tarea_asignaciones (tarea_id, usuario_id) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, tareaId);
            stmt.setLong(2, usuarioId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error al asignar usuario " + usuarioId + " a la tarea " + tareaId, e);
        }
    }

    /**
     * Comprueba en la base de datos si existe un registro de asignación específico.
     *
     * @param tareaId ID de la tarea a verificar.
     * @param usuarioId ID del usuario a verificar.
     * @return true si existe la relación, false en caso contrario.
     * @throws DatabaseException Si ocurre un error al ejecutar la consulta de conteo.
     */
    public boolean esUsuarioAsignado(long tareaId, long usuarioId) {
        String sql = "SELECT COUNT(*) FROM tarea_asignaciones WHERE tarea_id = ? AND usuario_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, tareaId);
            stmt.setLong(2, usuarioId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error al verificar asignación de la tarea " + tareaId, e);
        }
        return false;
    }

    /**
     * Recupera el listado global de todas las tareas, ordenadas por ID.
     *
     * @return List con todos los objetos Tarea del sistema.
     * @throws DatabaseException Si falla la lectura o el mapeo de los datos.
     */
    public List<Tarea> listarTodas() {
        List<Tarea> listaDeTareas = new ArrayList<>();
        String sql = "SELECT * FROM tareas ORDER BY id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                listaDeTareas.add(mapearTarea(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error al intentar leer todas las tareas", e);
        }
        return listaDeTareas;
    }

    /**
     * Obtiene todas las tareas vinculadas a un proyecto concreto.
     *
     * @param idBusqueda ID del proyecto para filtrar.
     * @return List de tareas que pertenecen al proyecto indicado.
     * @throws DatabaseException Si hay un error en la consulta filtrada.
     */
    public List<Tarea> listarPorProyecto(long idBusqueda) {
        List<Tarea> listaDeTareas = new ArrayList<>();
        String sql = "SELECT * FROM tareas WHERE proyecto_id = ? ORDER BY id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idBusqueda);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    listaDeTareas.add(mapearTarea(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error al leer tareas del proyecto: " + idBusqueda, e);
        }
        return listaDeTareas;
    }

    /**
     * Busca una tarea específica por su identificador único.
     *
     * @param idBusqueda ID de la tarea a localizar.
     * @return Objeto Tarea encontrado o null si no existe el registro.
     * @throws DatabaseException Si ocurre un error imprevisto en la búsqueda.
     */
    public Tarea buscarPorId(long idBusqueda) {
        String sql = "SELECT * FROM tareas WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idBusqueda);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearTarea(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error al buscar tarea por ID: " + idBusqueda, e);
        }
        return null;
    }

    /**
     * Recupera las tareas donde un usuario específico figura como asignado.
     *
     * @param usuarioId ID del usuario para filtrar asignaciones.
     * @return List de tareas asignadas al usuario.
     * @throws DatabaseException Si falla el JOIN o la consulta de asignaciones.
     */
    public List<Tarea> listarPorUsuario(long usuarioId) {
        List<Tarea> listaDeTareas = new ArrayList<>();
        String sql = "SELECT t.* FROM tareas t " +
                "INNER JOIN tarea_asignaciones ta ON t.id = ta.tarea_id " +
                "WHERE ta.usuario_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    listaDeTareas.add(mapearTarea(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error al leer tareas asignadas al usuario: " + usuarioId, e);
        }
        return listaDeTareas;
    }

    /**
     * Modifica el estado de progreso de una tarea existente.
     *
     * @param tareaId ID de la tarea a actualizar.
     * @param nuevoEstado El valor de Enum EstadoTarea que se desea aplicar.
     * @return true si se actualizó al menos una fila en la base de datos.
     * @throws DatabaseException Si falla la sentencia UPDATE.
     */
    public boolean actualizarEstado(long tareaId, EstadoTarea nuevoEstado) {
        String sql = "UPDATE tareas SET estado = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nuevoEstado.name());
            stmt.setLong(2, tareaId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error al actualizar estado de la tarea " + tareaId, e);
        }
    }

    /**
     * Consulta las entradas de historial registradas para una tarea.
     *
     * @param tareaId ID de la tarea.
     * @return List de String formateados con fecha y mensaje de cambio.
     * @throws DatabaseException Si ocurre un error al leer la tabla de historial.
     */
    public List<String> obtenerHistorial(long tareaId) {
        List<String> historial = new ArrayList<>();
        String sql = "SELECT mensaje, fecha FROM historial_tareas WHERE tarea_id = ? ORDER BY fecha DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, tareaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String entrada = "[" + rs.getTimestamp("fecha") + "] " + rs.getString("mensaje");
                    historial.add(entrada);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error al obtener historial de la tarea " + tareaId, e);
        }
        return historial;
    }

    /**
     * Crea un nuevo registro de auditoría en el historial de una tarea.
     *
     * @param tareaId ID de la tarea asociada.
     * @param mensaje Descripción del cambio o evento realizado.
     * @throws DatabaseException Si falla la inserción del log de auditoría.
     */
    public void registrarHistorial(long tareaId, String mensaje) {
        String sql = "INSERT INTO historial_tareas (tarea_id, mensaje) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, tareaId);
            stmt.setString(2, mensaje);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error al registrar auditoría para la tarea " + tareaId, e);
        }
    }

    /**
     * Transforma la fila actual de un ResultSet en un objeto de tipo Tarea.
     *
     * @param rs El ResultSet posicionado en la fila a leer.
     * @return Un objeto Tarea con los datos de la fila.
     * @throws SQLException Si ocurre un error al acceder a los nombres de columna o tipos de datos.
     */
    private Tarea mapearTarea(ResultSet rs) throws SQLException {
        return new Tarea(
                rs.getLong("id"),
                rs.getLong("proyecto_id"),
                rs.getString("titulo"),
                rs.getString("descripcion"),
                Prioridad.valueOf(rs.getString("prioridad").toUpperCase().trim()),
                EstadoTarea.valueOf(rs.getString("estado").toUpperCase().trim()),
                rs.getDate("fecha_limite").toLocalDate()
        );
    }
}

