package com.taskpro.dao;

import com.taskpro.config.DatabaseConnection;
import com.taskpro.model.Tarea;
import com.taskpro.model.enums.EstadoTarea;
import com.taskpro.model.enums.Prioridad;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TareaDAO {

    /**
     * Inserta una nueva tarea en la base de datos.
     *
     * @param tarea Objeto Tarea con la información a registrar.
     * @return true si la tarea se guardó correctamente, false en caso contrario.
     */
    public boolean guardar(Tarea tarea) {
        String sql = "INSERT INTO tareas (proyecto_id, titulo, descripcion, fecha_limite) VALUES " +
                "(?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, tarea.getProyectoId());
            stmt.setString(2, tarea.getTitulo());
            stmt.setString(3, tarea.getDescripcion());
            stmt.setDate(4, java.sql.Date.valueOf(tarea.getFechaLimite()));

            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println("Error al intentar guardar la tarea: " + e.getMessage());
            return false;
        }
    }

    /**
     * Crea un registro en la tabla intermedia para asignar una tarea a un usuario.
     *
     * @param tareaId ID de la tarea a asignar.
     * @param usuarioId ID del usuario que recibirá la asignación.
     * @return true si la asignación fue exitosa, false si hubo un error.
     */
    public boolean asignarUsuario(long tareaId, long usuarioId) {
        String sql = "INSERT INTO tarea_asignaciones (tarea_id, usuario_id) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, tareaId);
            stmt.setLong(2, usuarioId);

            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println("Error al asignar usuario a la tarea: " + e.getMessage());
            return false;
        }
    }

    /**
     * Verifica si existe una relación de asignación entre una tarea y un usuario.
     *
     * @param tareaId ID de la tarea a comprobar.
     * @param usuarioId ID del usuario a comprobar.
     * @return true si el usuario está asignado a la tarea, false de lo contrario.
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
            System.err.println("Error al verificar asignación: " + e.getMessage());
        }
        return false;
    }

    /**
     * Obtiene la lista completa de todas las tareas del sistema.
     *
     * @return List con todos los objetos Tarea encontrados.
     */
    public List<Tarea> listarTodas() {
        List<Tarea> listaDeTareas = new ArrayList<>();
        String sql = "SELECT * FROM tareas ORDER BY id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                long id = rs.getLong("id");
                long proyectoId = rs.getLong("proyecto_id");
                String titulo = rs.getString("titulo");
                String descripcion = rs.getString("descripcion");
                Prioridad prioridad = Prioridad.valueOf(rs.getString("prioridad"));
                EstadoTarea estado = EstadoTarea.valueOf(rs.getString("estado"));
                LocalDate fechaLimite = rs.getDate("fecha_limite").toLocalDate();

                Tarea tareaLeida = new Tarea(id, proyectoId, titulo, descripcion, prioridad,
                        estado, fechaLimite);
                listaDeTareas.add(tareaLeida);
            }
        } catch (SQLException e) {
            System.err.println("Error al intentar leer las tareas: " + e.getMessage());
        }
        return listaDeTareas;
    }

    /**
     * Filtra y devuelve las tareas que pertenecen a un proyecto específico.
     *
     * @param idBusqueda ID del proyecto cuyas tareas se desean listar.
     * @return List de tareas asociadas al proyecto indicado.
     */
    public List<Tarea> listarPorProyecto(long idBusqueda) {
        List<Tarea> listaDeTareas = new ArrayList<>();
        String sql = "SELECT * FROM tareas WHERE proyecto_id = ? ORDER BY id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idBusqueda);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    long id = rs.getLong("id");
                    long proyectoId = rs.getLong("proyecto_id");
                    String titulo = rs.getString("titulo");
                    String descripcion = rs.getString("descripcion");
                    Prioridad prioridad = Prioridad.valueOf(rs.getString("prioridad"));
                    EstadoTarea estado = EstadoTarea.valueOf(rs.getString("estado"));
                    LocalDate fechaLimite = rs.getDate("fecha_limite").toLocalDate();

                    Tarea tareaLeida = new Tarea(id, proyectoId, titulo, descripcion, prioridad,
                            estado, fechaLimite);
                    listaDeTareas.add(tareaLeida);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al intentar leer las tareas del proyecto: " + e.getMessage());
        }
        return listaDeTareas;
    }

    /**
     * Localiza una tarea única en la base de datos mediante su identificador.
     *
     * @param idBusqueda ID de la tarea a buscar.
     * @return El objeto Tarea si existe, o null si no se encuentra.
     */
    public Tarea buscarPorId(long idBusqueda) {
        Tarea tareaEncontrada = null;
        String sql = "SELECT * FROM tareas WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idBusqueda);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    long id = rs.getLong("id");
                    long proyectoId = rs.getLong("proyecto_id");
                    String titulo = rs.getString("titulo");
                    String descripcion = rs.getString("descripcion");
                    Prioridad prioridad = Prioridad.valueOf(rs.getString("prioridad"));
                    EstadoTarea estado = EstadoTarea.valueOf(rs.getString("estado"));
                    LocalDate fechaLimite = rs.getDate("fecha_limite").toLocalDate();

                    tareaEncontrada = new Tarea(id, proyectoId, titulo, descripcion, prioridad,
                            estado, fechaLimite);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar la tarea por ID: " + e.getMessage());
        }
        return tareaEncontrada;
    }

    /**
     * Recupera las tareas asignadas a un usuario a través de la tabla intermedia.
     *
     * @param usuarioId ID del usuario para filtrar sus tareas asignadas.
     * @return List de tareas donde el usuario figura como asignado.
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
                    long id = rs.getLong("id");
                    long proyectoId = rs.getLong("proyecto_id");
                    String titulo = rs.getString("titulo");
                    String descripcion = rs.getString("descripcion");
                    Prioridad prioridad =
                            Prioridad.valueOf(rs.getString("prioridad").toUpperCase().trim());
                    EstadoTarea estado =
                            EstadoTarea.valueOf(rs.getString("estado").toUpperCase().trim());
                    LocalDate fechaLimite = rs.getDate("fecha_limite").toLocalDate();

                    Tarea tareaLeida = new Tarea(id, proyectoId, titulo, descripcion,
                            prioridad, estado, fechaLimite);
                    listaDeTareas.add(tareaLeida);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al leer las tareas del usuario: " + e.getMessage());
        }
        return listaDeTareas;
    }

    /**
     * Actualiza el estado actual de una tarea en la base de datos.
     *
     * @param tareaId ID de la tarea a modificar.
     * @param nuevoEstado El valor del Enum EstadoTarea que se desea aplicar.
     * @return true si la actualización fue exitosa, false en caso de error.
     */
    public boolean actualizarEstado(long tareaId, EstadoTarea nuevoEstado) {
        String sql = "UPDATE tareas SET estado = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nuevoEstado.name());
            stmt.setLong(2, tareaId);

            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (java.sql.SQLException e) {
            System.err.println("Error al actualizar el estado: " + e.getMessage());
            return false;
        }
    }

    /**
     * Recupera el registro histórico de cambios de una tarea específica.
     *
     * @param tareaId ID de la tarea de la cual se desea obtener el historial.
     * @return List de cadenas de texto con la fecha y descripción de cada cambio.
     */
    public List<String> obtenerHistorial(long tareaId) {
        List<String> historial = new ArrayList<>();
        String sql = "SELECT mensaje, fecha FROM historial_tareas WHERE tarea_id = ? " +
                "ORDER BY fecha DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, tareaId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String entrada = "[" + rs.getTimestamp("fecha") + "] " + rs.getString(
                        "mensaje");
                historial.add(entrada);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener el historial: " + e.getMessage());
        }
        return historial;
    }

    /**
     * Registra una entrada en el historial de auditoría de una tarea.
     *
     * @param tareaId ID de la tarea asociada al cambio.
     * @param mensaje Descripción detallada del evento o cambio realizado.
     */
    public void registrarHistorial(long tareaId, String mensaje) {
        String sql = "INSERT INTO historial_tareas (tarea_id, mensaje) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, tareaId);
            stmt.setString(2, mensaje);

            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al registrar el historial: " + e.getMessage());
        }
    }
}
