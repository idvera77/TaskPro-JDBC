package com.taskpro.dao;

import com.taskpro.config.DatabaseConnection;
import com.taskpro.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public boolean guardar(Usuario usuario) {
        String sql = "INSERT INTO usuarios (username, email, password_hash, " +
                "rol_id, fecha_creacion) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getUsername());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getPassword());
            stmt.setLong(4, usuario.getRolId());
            stmt.setTimestamp(5, java.sql.Timestamp.valueOf(usuario.getFechaCreacion()));

            int filasAfectadas =  stmt.executeUpdate();

            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al intentar guardar el usuario: " + e.getMessage());
            return false;
        }
    }

    public List<Usuario> listarTodos(){
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
                LocalDateTime fechaCreacion = rs.getTimestamp("fecha_creacion").toLocalDateTime();

                Usuario usuarioLeido = new Usuario(id, username, email,
                        password, rolId, fechaCreacion);

                listaDeUsuarios.add(usuarioLeido);
            }

        } catch (SQLException e) {
            System.err.println("Error al intentar leer los Usuarios: " + e.getMessage());
        }

        return listaDeUsuarios;
    }
}
