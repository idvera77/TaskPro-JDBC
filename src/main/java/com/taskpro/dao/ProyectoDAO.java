package com.taskpro.dao;

import com.taskpro.config.DatabaseConnection;
import com.taskpro.model.Proyecto;
import com.taskpro.model.enums.EstadoProyecto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ProyectoDAO {

    public boolean guardar(Proyecto proyecto) {
        String sql = "INSERT INTO proyectos (nombre, descripcion, estado, " +
                "creador_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, proyecto.getNombre());
            stmt.setString(2, proyecto.getDescripcion());
            stmt.setString(3, proyecto.getEstado().name());
            stmt.setLong(4, proyecto.getCreadorId());

            int filasAfectadas = stmt.executeUpdate();

            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al intentar guardar el proyecto: " + e.getMessage());
            return false;
        }
    }

    public ArrayList<Proyecto> listarTodos() {
        ArrayList<Proyecto> listaDeProyectos = new ArrayList<>();

        String sql = "SELECT * FROM proyectos";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                long id = rs.getLong("id");
                String nombre = rs.getString("nombre");
                String descripcion = rs.getString("descripcion");
                EstadoProyecto estado = EstadoProyecto.valueOf(rs.getString(
                        "estado"));
                long creadorId = rs.getLong("creador_id");

                Proyecto proyectoLeido = new Proyecto(id, nombre, descripcion,
                        estado, creadorId);
                listaDeProyectos.add(proyectoLeido);
            }

        } catch (SQLException e) {
            System.err.println("Error al intentar leer los proyectos: " + e.getMessage());
        }

        return listaDeProyectos;
    }

    public boolean eliminar(long id) {
        String sql = "DELETE FROM proyectos WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            int filasAfectadas = stmt.executeUpdate();

            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al intentar eliminar el proyecto: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizar(Proyecto proyecto) {
        String sql = "UPDATE proyectos SET nombre = ?, descripcion = ?, " +
                "estado = ?, creador_id = ? WHERE id = ? ";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, proyecto.getNombre());
            stmt.setString(2, proyecto.getDescripcion());
            stmt.setString(3, proyecto.getEstado().name());
            stmt.setLong(4, proyecto.getCreadorId());

            stmt.setLong(5, proyecto.getId());

            int filasAfectadas = stmt.executeUpdate();

            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al intentar actualizar el proyecto: " + e.getMessage());
            return false;
        }
    }
}
