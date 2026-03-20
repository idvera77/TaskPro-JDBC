package com.taskpro.config;

import com.taskpro.exception.DatabaseException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/task_pro_db";
    private static final String USER = "root";
    private static final String PASSWORD = "1234";

    /**
     * Abre y devuelve una conexión activa a la base de datos.
     *
     * @return Connection objeto de conexión.
     * @throws DatabaseException sí hay un error de red o credenciales.
     */
    public static Connection getConnection(){
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new DatabaseException("No se pudo establecer la conexión con MySQL. " +
                    "Verifica que el servicio esté activo y las credenciales sean correctas.", e);
        }
    }
}
