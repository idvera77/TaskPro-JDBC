package com.taskpro.dao;

import com.taskpro.config.DatabaseConnection;
import com.taskpro.exception.DatabaseException;
import com.taskpro.model.Proyecto;
import com.taskpro.model.enums.EstadoProyecto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProyectoDAO {

    /**
     * Inserta un nuevo proyecto en la base de datos.
     *
     * @param proyecto Objeto Proyecto con los datos a persistir.
     * @return true si el registro fue exitoso.
     * @throws DatabaseException si ocurre un error en la base de datos.
     */
    public boolean guardar(Proyecto proyecto) {
        String sql = "INSERT INTO proyectos (nombre, descripcion, creador_id) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, proyecto.getNombre());
            stmt.setString(2, proyecto.getDescripcion());
            stmt.setLong(3, proyecto.getCreadorId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            // Envolvemos el error de SQL en nuestra excepción personalizada
            throw new DatabaseException("No se pudo guardar el proyecto: " + proyecto.getNombre(), e);
        }
    }

    /**
     * Recupera la lista completa de todos los proyectos registrados.
     *
     * @return List con todos los objetos Proyecto encontrados.
     */
    public List<Proyecto> listarTodos() {
        List<Proyecto> listaDeProyectos = new ArrayList<>();
        String sql = "SELECT * FROM proyectos";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                listaDeProyectos.add(mapearProyecto(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error al listar todos los proyectos", e);
        }
        return listaDeProyectos;
    }

    /**
     * Recupera todos los proyectos asociados a un usuario creador específico.
     *
     * @param idBusqueda Identificador único del usuario creador.
     * @return List de objetos Proyecto pertenecientes al usuario.
     * @throws DatabaseException Si ocurre un error técnico al ejecutar la consulta SQL.
     */
    public List<Proyecto> listarPorUsuario(long idBusqueda) {
        List<Proyecto> listaDeProyectos = new ArrayList<>();
        String sql = "SELECT * FROM proyectos WHERE creador_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idBusqueda);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    listaDeProyectos.add(mapearProyecto(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error al listar proyectos del usuario: " + idBusqueda, e);
        }
        return listaDeProyectos;
    }

    /**
     * Localiza un proyecto único en la base de datos mediante su ID.
     *
     * @param idBusqueda Identificador del proyecto a buscar.
     * @return El objeto Proyecto encontrado, o null si no existe ningún registro con ese ID.
     * @throws DatabaseException Si falla la conexión o la comunicación con la base de datos.
     */
    public Proyecto buscarPorId(long idBusqueda) {
        String sql = "SELECT * FROM proyectos WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idBusqueda);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearProyecto(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error al buscar proyecto por ID: " + idBusqueda, e);
        }
        return null;
    }

    /**
     * Actualiza el estado de un proyecto identificado por su ID.
     *
     * @param id Identificador del proyecto a modificar.
     * @param nuevoEstado Nuevo valor del estado (Enum EstadoProyecto).
     * @return true si el registro fue actualizado (al menos una fila afectada).
     * @throws DatabaseException Si hay un error de sintaxis SQL o problemas de persistencia.
     */
    public boolean actualizarEstado(long id, EstadoProyecto nuevoEstado) {
        String sql = "UPDATE proyectos SET estado = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nuevoEstado.name());
            stmt.setLong(2, id);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Error al actualizar estado del proyecto ID: " + id, e);
        }
    }

    /**
     * Utilidad que transforma una fila del ResultSet en un objeto Proyecto.
     * Centraliza la lógica de conversión para facilitar el mantenimiento.
     *
     * @param rs El conjunto de resultados posicionado en la fila actual.
     * @return Un nuevo objeto Proyecto con los datos cargados desde la DB.
     * @throws SQLException Si ocurre un error al acceder a las columnas del ResultSet.
     */
    private Proyecto mapearProyecto(ResultSet rs) throws SQLException {
        return new Proyecto(
                rs.getLong("id"),
                rs.getString("nombre"),
                rs.getString("descripcion"),
                EstadoProyecto.valueOf(rs.getString("estado")),
                rs.getLong("creador_id")
        );
    }
}
