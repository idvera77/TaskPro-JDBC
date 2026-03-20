package com.taskpro.dao;

import com.taskpro.config.DatabaseConnection;
import com.taskpro.exception.DatabaseException;
import com.taskpro.model.Usuario;
import com.taskpro.model.enums.NombreRol;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    /**
     * Recupera la lista completa de todos los usuarios registrados en el sistema.
     *
     * @return List de objetos Usuario con sus datos, roles y fechas de creación.
     * @throws DatabaseException Si ocurre un error técnico al leer de la tabla usuarios.
     */
    public List<Usuario> listarTodos() {
        List<Usuario> listaDeUsuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                listaDeUsuarios.add(mapearUsuario(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error al intentar obtener la lista global de usuarios.", e);
        }
        return listaDeUsuarios;
    }

    /**
     * Busca un usuario específico utilizando su dirección de correo electrónico.
     *
     * @param emailBusqueda El email del usuario que se desea localizar.
     * @return Un objeto Usuario si se encuentra una coincidencia, o null si no existe.
     * @throws DatabaseException Si falla la consulta o la conexión con la base de datos.
     */
    public Usuario buscarPorEmail(String emailBusqueda) {
        String sql = "SELECT * FROM usuarios WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, emailBusqueda);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error al buscar el usuario con email: " + emailBusqueda, e);
        }
        return null;
    }

    /**
     * Método auxiliar (Helper) para transformar una fila de la base de datos en un objeto Usuario.
     * Gestiona la conversión de tipos complejos como el rol y la fecha.
     *
     * @param rs El ResultSet posicionado en la fila actual.
     * @return Un nuevo objeto Usuario cargado con los datos de la fila.
     * @throws SQLException Si ocurre un error al acceder a las columnas.
     */
    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        String username = rs.getString("username");
        String email = rs.getString("email");
        String password = rs.getString("password_hash");

        // Lógica de conversión de Rol
        long rolId = rs.getLong("rol_id");
        NombreRol rol = (rolId == 1) ? NombreRol.ADMINISTRADOR : NombreRol.USUARIO;

        // Lógica de conversión de Fecha (Timestamp -> LocalDateTime)
        Timestamp ts = rs.getTimestamp("fecha_creacion");
        LocalDateTime fechaCreacion = (ts != null) ? ts.toLocalDateTime() : null;

        return new Usuario(id, username, email, password, rol, fechaCreacion);
    }
}