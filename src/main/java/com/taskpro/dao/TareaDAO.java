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

    public boolean guardar(Tarea tarea) {
        // Mencionamos las columnas exactamente como se llaman en tu base de datos.
        String sql = "INSERT INTO tareas (proyecto_id, titulo, descripcion, prioridad, estado, " +
                "fecha_limite)  VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Cambiamos cada "?", por el dato correspondiente de la tarea.
            stmt.setLong(1, tarea.getProyectoId());
            stmt.setString(2, tarea.getTitulo());
            stmt.setString(3, tarea.getDescripcion());
            stmt.setString(4, tarea.getPrioridad().name());
            stmt.setString(5, tarea.getEstado().name());
            // Java usa LocalDate, pero SQL necesita java.sql.Date.
            stmt.setDate(6, java.sql.Date.valueOf(tarea.getFechaLimite()));
            // executeUpdate() envía la orden y devuelve cuántas filas se han creado (debería ser 1).
            int filasAfectadas = stmt.executeUpdate();
            // Si filasAfectadas es mayor que 0, devuelve true (éxito). Si es 0, devuelve false.
            return filasAfectadas > 0;
        } catch (SQLException e) {
            // Si el SQL está mal escrito o la base de datos está apagada, entramos aquí.
            System.err.println("Error al intentar guardar la tarea: " + e.getMessage());
            return false;
        }
    }

    public boolean asignarUsuario(long tareaId, long usuarioId) {
        String sql = "INSERT INTO tarea_asignaciones (tarea_id, usuario_id) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, tareaId);
            stmt.setLong(2, usuarioId);

            // Si devuelve más de 0, es que se ha insertado correctamente
            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println("Error al asignar usuario a la tarea: " + e.getMessage());
            return false;
        }
    }

    public boolean desasignarUsuario(long tareaId, long usuarioId) {
        String sql = "DELETE FROM tarea_asignaciones WHERE tarea_id = ? AND usuario_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, tareaId);
            stmt.setLong(2, usuarioId);

            // Si devuelve > 0, es que ha borrado la asignación con éxito
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al desasignar usuario: " + e.getMessage());
            return false;
        }
    }

    public boolean registrarHistorial(long tareaId, String mensaje) {
        String sql = "INSERT INTO historial_tareas (tarea_id, mensaje) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, tareaId);
            stmt.setString(2, mensaje);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al registrar el historial: " + e.getMessage());
            return false;
        }
    }

    public List<Tarea> listarTodas() {
        // Aquí guardaremos las tareas a medida que las vayamos leyendo de la base de datos.
        List<Tarea> listaDeTareas = new ArrayList<>();
        // Queremos todas las columnas (*) de todas las filas de la tabla.
        String sql = "SELECT * FROM tareas ORDER BY id";
        // Usamos executeQuery() en lugar de executeUpdate() porque esta vez
        // no estamos modificando la base de datos, solo estamos preguntando.
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            // Mientras haya una fila siguiente (rs.next() sea true)...
            while (rs.next()) {
                // A. Extraemos los datos de la fila actual usando el nombre de la columna en MySQL
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

    public List<Tarea> listarPorProyecto(long idBusqueda) {
        List<Tarea> listaDeTareas = new ArrayList<>();
        //Buscamos por la columna correcta
        String sql = "SELECT * FROM tareas WHERE proyecto_id = ? ORDER BY id";
        // El try principal solo abre la conexión y el statement
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            //Asignamos el valor al interrogante ANTES de ejecutar
            stmt.setLong(1, idBusqueda);
            //Ejecutamos la consulta y metemos el ResultSet en su propio try-with-resources
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

    public Tarea buscarPorId(long idBusqueda) {
        Tarea tareaEncontrada = null;
        String sql = "SELECT * FROM tareas WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idBusqueda);

            try (ResultSet rs = stmt.executeQuery()) {
                // Usamos 'if' porque el ID es Primary Key (solo habrá 1 o ninguno)
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

    public boolean eliminar(long id) {
        // Borra de 'tareas' donde la columna 'id' sea igual a la interrogación.
        String sql = "DELETE FROM tareas WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Cambiamos la interrogación por el número de ID que nos pasen.
            stmt.setLong(1, id);
            // executeUpdate() no solo ejecuta la orden, sino que nos devuelve un número entero (int)
            // con la cantidad de filas que han sido eliminadas físicamente de la base de datos.
            int filasAfectadas = stmt.executeUpdate();
            // Si borró 1 fila, devolverá true. Si el ID no existía y borró 0 filas, devolverá false.
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println("Error al intentar eliminar la tarea: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizar(Tarea tarea) {
        //Si olvidas el WHERE aquí, actualizarás TODAS las tareas de la base de datos de golpe.
        String sql = "UPDATE tareas SET proyecto_id = ?, titulo = ?, descripcion = ?, prioridad =" +
                " ?, estado = ?, fecha_limite = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, tarea.getProyectoId());
            stmt.setString(2, tarea.getTitulo());
            stmt.setString(3, tarea.getDescripcion());
            stmt.setString(4, tarea.getPrioridad().name());
            stmt.setString(5, tarea.getEstado().name());
            stmt.setDate(6, java.sql.Date.valueOf(tarea.getFechaLimite()));
            // El hueco 7 es el ID de la tarea que queremos modificar
            stmt.setLong(7, tarea.getId());
    
            int filasAfectadas = stmt.executeUpdate();
            // Devolverá true si encontró el ID y lo modificó, o false si el ID no existía.
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println("Error al intentar actualizar la tarea: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarEstado(long tareaId, com.taskpro.model.enums.EstadoTarea nuevoEstado) {
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

    public List<String> obtenerHistorial(long tareaId) {
        List<String> historial = new ArrayList<>();
        // Traemos el mensaje y la fecha (si tienes columna de fecha, si no, solo el mensaje)
        String sql = "SELECT mensaje, fecha FROM historial_tareas WHERE tarea_id = ? " +
                "ORDER BY fecha DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, tareaId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // Formateamos una línea bonita para la consola
                String entrada = "[" + rs.getTimestamp("fecha") + "] " + rs.getString(
                        "mensaje");
                historial.add(entrada);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener el historial: " + e.getMessage());
        }
        return historial;
    }
}
