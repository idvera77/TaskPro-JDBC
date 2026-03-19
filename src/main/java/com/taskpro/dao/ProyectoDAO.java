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

    /**
     * Inserta un nuevo proyecto en la base de datos.
     *
     * @param proyecto Objeto Proyecto con los datos a persistir.
     * @return true si el registro fue exitoso, false si hubo un error.
     */
    public boolean guardar(Proyecto proyecto) {
        String sql = "INSERT INTO proyectos (nombre, descripcion, creador_id) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, proyecto.getNombre());
            stmt.setString(2, proyecto.getDescripcion());
            stmt.setLong(3, proyecto.getCreadorId());

            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println("Error al intentar guardar el proyecto: " + e.getMessage());
            return false;
        }
    }

    /**
     * Recupera la lista completa de todos los proyectos registrados.
     *
     * @return ArrayList con todos los objetos Proyecto encontrados.
     */
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
                EstadoProyecto estado = EstadoProyecto.valueOf(rs.getString("estado"));
                long creadorId = rs.getLong("creador_id");

                Proyecto proyectoLeido = new Proyecto(id, nombre, descripcion, estado, creadorId);
                listaDeProyectos.add(proyectoLeido);
            }
        } catch (SQLException e) {
            System.err.println("Error al intentar leer los proyectos: " + e.getMessage());
        }
        return listaDeProyectos;
    }

    /**
     * Obtiene la lista de proyectos creados por un usuario específico.
     *
     * @param idBusqueda ID del usuario creador de los proyectos.
     * @return ArrayList de proyectos asociados al ID proporcionado.
     */
    public ArrayList<Proyecto> listarPorUsuario(long idBusqueda) {
        ArrayList<Proyecto> listaDeProyectos = new ArrayList<>();
        String sql = "SELECT * FROM proyectos WHERE creador_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idBusqueda);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    long id = rs.getLong("id");
                    String nombre = rs.getString("nombre");
                    String descripcion = rs.getString("descripcion");
                    EstadoProyecto estado = EstadoProyecto.valueOf(rs.getString("estado"));
                    long creadorId = rs.getLong("creador_id");

                    Proyecto proyectoLeido = new Proyecto(id, nombre, descripcion, estado, creadorId);
                    listaDeProyectos.add(proyectoLeido);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al intentar leer los proyectos: " + e.getMessage());
        }
        return listaDeProyectos;
    }

    /**
     * Busca un proyecto específico a partir de su identificador único.
     *
     * @param idBusqueda ID del proyecto a localizar.
     * @return El objeto Proyecto si existe, o null si no se encuentra.
     */
    public Proyecto buscarPorId(long idBusqueda) {
        Proyecto proyectoEncontrado = null;
        String sql = "SELECT * FROM proyectos WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idBusqueda);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    long id = rs.getLong("id");
                    String nombre = rs.getString("nombre");
                    String descripcion = rs.getString("descripcion");
                    EstadoProyecto estado = EstadoProyecto.valueOf(rs.getString("estado"));
                    long creadorId = rs.getLong("creador_id");

                    proyectoEncontrado = new Proyecto(id, nombre, descripcion, estado, creadorId);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar el proyecto por ID: " + e.getMessage());
        }
        return proyectoEncontrado;
    }

    /**
     * Modifica el estado de un proyecto existente en la base de datos.
     *
     * @param id ID del proyecto a actualizar.
     * @param nuevoEstado El nuevo estado que se desea asignar.
     * @return true si el estado se actualizó correctamente, false en caso de error.
     */
    public boolean actualizarEstado(long id, EstadoProyecto nuevoEstado) {
        String sql = "UPDATE proyectos SET estado = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nuevoEstado.name());
            stmt.setLong(2, id);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar el estado del proyecto: " + e.getMessage());
            return false;
        }
    }
}
