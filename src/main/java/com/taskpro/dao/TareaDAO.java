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
        String sql = "INSERT INTO tareas (proyecto_id, titulo, descripcion, prioridad, estado, fecha_limite) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Cambiamos cada "?" por el dato correspondiente de la tarea.
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

    public List<Tarea> listarTodas() {
        // Aquí guardaremos las tareas a medida que las vayamos leyendo de la base de datos.
        List<Tarea> listaDeTareas = new ArrayList<>();

        // Queremos todas las columnas (*) de todas las filas de la tabla.
        String sql = "SELECT * FROM tareas";

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

                Tarea tareaLeida = new Tarea(id, proyectoId, titulo, descripcion, prioridad, estado, fechaLimite);
                listaDeTareas.add(tareaLeida);
            }

        } catch (SQLException e) {
            System.err.println("Error al intentar leer las tareas: " + e.getMessage());
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
        String sql = "UPDATE tareas SET proyecto_id = ?, titulo = ?, descripcion = ?, " +
                "prioridad = ?, estado = ?, fecha_limite = ? WHERE id = ?";

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
}
