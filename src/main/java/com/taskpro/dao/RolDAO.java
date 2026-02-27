package com.taskpro.dao;

import com.taskpro.config.DatabaseConnection;
import com.taskpro.model.Rol;
import com.taskpro.model.enums.NombreRol;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RolDAO {

    public List<Rol> listarTodos() {
        ArrayList<Rol> listaDeRoles = new ArrayList<>();
        String sql = "SELECT * FROM roles ORDER BY id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                long id = rs.getLong("id");
                NombreRol rol = NombreRol.valueOf(rs.getString("nombre"));

                Rol rolLeido = new Rol(id, rol);
                listaDeRoles.add(rolLeido);
            }
        } catch (SQLException e) {
            System.err.println("Error al intentar leer los roles: " + e.getMessage());
        }
        return listaDeRoles;
    }

}
