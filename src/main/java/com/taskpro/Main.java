package com.taskpro;

import com.taskpro.config.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("Funciona");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}