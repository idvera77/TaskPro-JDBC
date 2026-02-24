package com.taskpro;

import com.taskpro.dao.RolDAO;
import com.taskpro.dao.TareaDAO;
import com.taskpro.dao.UsuarioDAO;
import com.taskpro.model.Rol;
import com.taskpro.model.Tarea;
import com.taskpro.model.Usuario;
import com.taskpro.model.enums.EstadoTarea;
import com.taskpro.model.enums.Prioridad;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        UsuarioDAO usuarioDAO = new UsuarioDAO();

        // 1. Creamos un usuario nuevo. Le asignamos el rolId = 1 (ADMINISTRADOR)
        Usuario nuevoUsuario = new Usuario(
                "SuperPepe",
                "pepe@taskpro.com",
                "passwordSecreta123",
                1, // ID del Rol (asegúrate de que existe el 1 en tu tabla roles)
                LocalDateTime.now()
        );

        // 2. Lo guardamos en la base de datos
        if (usuarioDAO.guardar(nuevoUsuario)) {
            System.out.println("✅ ¡Usuario guardado con éxito en MySQL!");
        } else {
            System.out.println("❌ Error al guardar el usuario.");
        }

        // 3. Leemos todos los usuarios para comprobar
        System.out.println("\n--- LISTA DE USUARIOS EN BD ---");
        List<Usuario> usuarios = usuarioDAO.listarTodos();
        for (Usuario u : usuarios) {
            System.out.println(u); // Gracias a tu toString(), esto se verá precioso
        }
    }
}