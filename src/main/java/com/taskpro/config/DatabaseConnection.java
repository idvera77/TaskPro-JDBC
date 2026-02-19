package com.taskpro.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // 1. Definimos las constantes
    private static final String URL = "jdbc:mysql://localhost:3306/task_pro_db";
    private static final String USER = "root";
    private static final String PASSWORD = "1234";

    /**
     * Abre y devuelve una conexión activa a la base de datos.
     * @return Connection objeto de conexión.
     * @throws SQLException sí hay un error de red o credenciales.
     */
    public static Connection getConnection() throws SQLException {
        try {
            // El DriverManager intenta buscar un driver que entienda la URL (el de MySQL que bajamos)
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            // Aquí capturamos el error para darle un mensaje más humano si falla
            System.err.println("¡Error! Asegúrate de que MySQL esté encendido y la contraseña sea correcta.");
            throw e;
        }
    }
}
