package com.taskpro.dao;

import com.taskpro.config.DatabaseConnection;
import com.taskpro.model.Usuario;
import com.taskpro.model.enums.NombreRol;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    /**
     * Recupera la lista completa de todos los usuarios registrados en el sistema.
     *
     * @return List de objetos Usuario con sus datos, roles y fechas de creación.
     */
    public List<Usuario> listarTodos() {
        List<Usuario> listaDeUsuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                long id = rs.getLong("id");
                String username = rs.getString("username");
                String email = rs.getString("email");
                String password = rs.getString("password_hash");

                long rolId = rs.getLong("rol_id");
                NombreRol rol = (rolId == 1) ? NombreRol.ADMINISTRADOR : NombreRol.USUARIO;

                java.sql.Timestamp ts = rs.getTimestamp("fecha_creacion");
                LocalDateTime fechaCreacion = (ts != null) ? ts.toLocalDateTime() : null;

                Usuario usuarioLeido = new Usuario(id, username, email, password, rol, fechaCreacion);
                listaDeUsuarios.add(usuarioLeido);
            }
        } catch (SQLException e) {
            System.err.println("Error al intentar leer los Usuarios: " + e.getMessage());
        }
        return listaDeUsuarios;
    }

    /**
     * Busca un usuario específico utilizando su dirección de correo electrónico.
     *
     * @param emailBusqueda El email del usuario que se desea localizar.
     * @return Un objeto Usuario si se encuentra una coincidencia, o null si no existe.
     */
    public Usuario buscarPorEmail(String emailBusqueda) {
        Usuario usuarioEncontrado = null;
        String sql = "SELECT * FROM usuarios WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, emailBusqueda);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    long id = rs.getLong("id");
                    String username = rs.getString("username");
                    String email = rs.getString("email");
                    String password = rs.getString("password_hash");

                    long rolId = rs.getLong("rol_id");
                    NombreRol rol = (rolId == 1) ? NombreRol.ADMINISTRADOR : NombreRol.USUARIO;

                    java.sql.Timestamp ts = rs.getTimestamp("fecha_creacion");
                    LocalDateTime fechaCreacion = (ts != null) ? ts.toLocalDateTime() : null;

                    usuarioEncontrado = new Usuario(id, username, email, password, rol, fechaCreacion);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar usuario por email: " + e.getMessage());
        }
        return usuarioEncontrado;
    }
}