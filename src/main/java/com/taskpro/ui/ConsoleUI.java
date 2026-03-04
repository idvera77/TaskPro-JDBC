package com.taskpro.ui;

import com.taskpro.dao.ProyectoDAO;
import com.taskpro.dao.TareaDAO;
import com.taskpro.dao.UsuarioDAO;
import com.taskpro.model.Proyecto;
import com.taskpro.model.Tarea;
import com.taskpro.model.Usuario;

import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private final Scanner scanner;
    private final UsuarioDAO usuarioDAO;
    private final ProyectoDAO proyectoDAO;
    private final TareaDAO tareaDAO;

    public ConsoleUI() {
        this.scanner = new Scanner(System.in);
        this.usuarioDAO = new UsuarioDAO();
        this.proyectoDAO = new ProyectoDAO();
        this.tareaDAO = new TareaDAO();
    }

    public void iniciar() {
        boolean salir = false;
        while (!salir) {
            System.out.println("\n==================================");
            System.out.println(" BIENVENIDO A TASKPRO ");
            System.out.println("==================================");
            System.out.println("1. Iniciar Sesión");
            System.out.println("2. Salir");
            System.out.print("Elige una opción: ");

            String opcion = scanner.nextLine();

            switch (opcion) {
                case "1":
                    login();
                    break;
                case "2":
                    salir = true;
                    System.out.println("\nCerrando TaskPro... ¡Hasta pronto!");
                    break;
                default:
                    System.out.println("\nOpción no válida. Por favor, teclea 1 o 2.");
            }
        }
        scanner.close();
    }

    private void login() {
        System.out.println("\n--- INICIO DE SESIÓN ---");
        System.out.print("Introduce tu email: ");
        String email = scanner.nextLine();

        Usuario usuarioActual = usuarioDAO.buscarPorEmail(email);

        if (usuarioActual != null) {
            System.out.println("\n¡Bienvenido/a, " + usuarioActual.getUsername() + "!");
            menuDashboard(usuarioActual);
        } else {
            System.out.println("\nError: No existe ningún usuario con ese email.");
        }
    }

    private void menuDashboard(Usuario usuarioActual) {
        boolean cerrarSesion = false;
        while (!cerrarSesion) {
            System.out.println("\n==================================");
            System.out.println(" PANEL DE CONTROL: " + usuarioActual.getUsername().toUpperCase());
            System.out.println("==================================");
            System.out.println("1. Ver mis Proyectos");
            System.out.println("2. Ver mis Tareas Asignadas");
            System.out.println("3. Cerrar Sesión");
            System.out.print("Elige una opción: ");

            String opcion = scanner.nextLine();

            switch (opcion) {
                case "1":
                    mostrarProyectos(usuarioActual);
                    break;
                case "2":
                    mostrarTareas(usuarioActual);
                    break;
                case "3":
                    cerrarSesion = true;
                    System.out.println("\nCerrando sesión...");
                    break;
                default:
                    System.out.println("\nOpción no válida.");
            }
        }
    }

    private void mostrarProyectos(Usuario usuarioActual) {
        System.out.println("\n--- TUS PROYECTOS ---");
        List<Proyecto> proyectos = proyectoDAO.listarPorUsuario(usuarioActual.getId());
        if (proyectos.isEmpty()) {
            System.out.println("Aún no has creado ningún proyecto.");
        } else {
            for (Proyecto p : proyectos) {
                System.out.println("ID: " + p.getId() + " | " + p.getNombre() + " | Estado: " + p.getEstado());
            }
        }
    }

    private void mostrarTareas(Usuario usuarioActual) {
        System.out.println("\n--- TUS TAREAS ---");
        List<Tarea> tareas = tareaDAO.listarPorUsuario(usuarioActual.getId());
        if (tareas.isEmpty()) {
            System.out.println("No tienes ninguna tarea asignada en este momento.");
        } else {
            for (Tarea t : tareas) {
                System.out.println("ID: " + t.getId() + " | " + t.getTitulo() +
                        " | Prioridad: " + t.getPrioridad() + " | Estado: " + t.getEstado());
            }
        }
    }
}