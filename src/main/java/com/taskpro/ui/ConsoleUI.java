package com.taskpro.ui;

import com.taskpro.dao.ProyectoDAO;
import com.taskpro.dao.TareaDAO;
import com.taskpro.dao.UsuarioDAO;
import com.taskpro.model.Proyecto;
import com.taskpro.model.Tarea;
import com.taskpro.model.Usuario;
import com.taskpro.model.enums.EstadoProyecto;
import com.taskpro.model.enums.EstadoTarea;
import com.taskpro.service.AuthService;
import com.taskpro.service.ProyectoService;
import com.taskpro.service.TareaService;

import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private final Scanner scanner;
    private final UsuarioDAO usuarioDAO;
    private final ProyectoDAO proyectoDAO;
    private final TareaDAO tareaDAO;
    private final ProyectoService proyectoService;
    private final TareaService tareaService;
    private final AuthService authService;

    /**
     * Constructor que inicializa los servicios, DAOs y el lector de consola.
     */
    public ConsoleUI() {
        this.scanner = new Scanner(System.in);
        this.usuarioDAO = new UsuarioDAO();
        this.proyectoDAO = new ProyectoDAO();
        this.tareaDAO = new TareaDAO();
        this.proyectoService = new ProyectoService();
        this.tareaService = new TareaService();
        this.authService = new AuthService();
    }

    /**
     * Lanza el bucle principal de la aplicación (Menú de Bienvenida).
     */
    public void iniciar() {
        boolean salir = false;
        while (!salir) {
            System.out.println("\n==================================");
            System.out.println("       BIENVENIDO A TASK PRO       ");
            System.out.println("==================================");
            System.out.println("1. Iniciar Sesión");
            System.out.println("2. Salir");
            System.out.print("> Elige una opción: ");

            String opcion = scanner.nextLine();

            switch (opcion) {
                case "1":
                    login();
                    break;
                case "2":
                    salir = true;
                    System.out.println("\nCerrando TaskPro... Hasta pronto.");
                    break;
                default:
                    System.out.println("\nOpción no valida. Por favor, teclea 1 o 2.");
            }
        }
        scanner.close();
    }

    /**
     * Gestiona el flujo de autenticación solicitando credenciales al usuario.
     */
    private void login() {
        System.out.println("\n--- INICIO DE SESIÓN ---");
        System.out.print("Introduce tu email: ");
        String email = scanner.nextLine();
        System.out.print("Introduce tu contraseña: ");
        String password = scanner.nextLine();

        Usuario usuarioActual = authService.login(email, password);

        if (usuarioActual != null) {
            System.out.println("\n¡Bienvenido/a, " + usuarioActual.getUsername() + "!");

            if (usuarioActual.esAdmin()) {
                menuGestor(usuarioActual);
            } else {
                menuUsuarioEstandar(usuarioActual);
            }
        } else {
            System.out.println("\nError: Credenciales incorrectas.");
        }
    }

    /**
     * Muestra las opciones exclusivas para usuarios con rol Administrador/Gestor.
     * @param usuarioActual Usuario autenticado que navega por el menú.
     */
    private void menuGestor(Usuario usuarioActual) {
        boolean cerrarSesion = false;
        while (!cerrarSesion) {
            System.out.println("\n==================================");
            System.out.println(" PANEL DE GESTIÓN: " + usuarioActual.getUsername().toUpperCase());
            System.out.println("==================================");
            System.out.println("1. Ver todos los Proyectos");
            System.out.println("2. Ver todas las Tareas");
            System.out.println("3. Crear Nuevo Proyecto");
            System.out.println("4. Crear Nueva Tarea");
            System.out.println("5. Asignar Tarea a Usuario");
            System.out.println("6. Ver Historial de Auditoría");
            System.out.println("7. Actualizar Estado de Proyecto");
            System.out.println("0. Cerrar Sesión");
            System.out.print("> Elige una opción: ");

            String opcion = scanner.nextLine();
            switch (opcion) {
                case "1": mostrarTodosLosProyectos(); break;
                case "2": mostrarTodasLasTareas(); break;
                case "3": menuCrearProyecto(usuarioActual); break;
                case "4": menuCrearTarea(usuarioActual); break;
                case "5": menuAsignarTarea(usuarioActual); break;
                case "6": menuVerHistorial(usuarioActual); break;
                case "7": menuCambiarEstadoProyecto(usuarioActual); break;
                case "0": cerrarSesion = true; break;
                default: System.out.println("Opción no válida.");
            }
        }
    }

    /**
     * Muestra las opciones para usuarios operativos (ver y actualizar sus tareas).
     * @param usuarioActual Usuario autenticado que navega por el menú.
     */
    private void menuUsuarioEstandar(Usuario usuarioActual) {
        boolean cerrarSesion = false;
        while (!cerrarSesion) {
            System.out.println("\n==================================");
            System.out.println(" MIS TAREAS: " + usuarioActual.getUsername().toUpperCase());
            System.out.println("==================================");
            System.out.println("1. Ver mis Tareas Asignadas");
            System.out.println("2. Actualizar Estado de Tarea");
            System.out.println("0. Cerrar Sesión");
            System.out.print("> Elige una opción: ");

            String opcion = scanner.nextLine();
            switch (opcion) {
                case "1": mostrarTareas(usuarioActual); break;
                case "2": menuCambiarEstadoTarea(usuarioActual); break;
                case "0": cerrarSesion = true; break;
                default: System.out.println("Opción no válida.");
            }
        }
    }

    private void mostrarProyectos(Usuario usuarioActual) {
        System.out.println("\n--- TUS PROYECTOS ---");
        List<Proyecto> proyectos = proyectoDAO.listarPorUsuario(usuarioActual.getId());
        if (proyectos.isEmpty()) {
            System.out.println("Aun no has creado ningún proyecto.");
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

    /**
     * Formulario interactivo para registrar un nuevo proyecto en el sistema.
     */
    private void menuCrearProyecto(Usuario usuarioActual) {
        System.out.println("\n--- CREAR NUEVO PROYECTO ---");

        System.out.print("Introduce el nombre del proyecto: ");
        String nombre = scanner.nextLine();

        System.out.print("Introduce una descripción: ");
        String descripcion = scanner.nextLine();

        Proyecto nuevoProyecto = new Proyecto(nombre, descripcion, usuarioActual.getId());

        String resultado = proyectoService.crearNuevoProyecto(nuevoProyecto, usuarioActual);

        System.out.println("\n" + resultado);
    }

    /**
     * Formulario para crear tareas vinculándolas a un proyecto existente.
     */
    private void menuCrearTarea(Usuario usuarioActual) {
        System.out.println("\n--- CREAR NUEVA TAREA ---");
        System.out.println("Primero, necesitas elegir a que proyecto pertenecerá la tarea.");
        mostrarProyectos(usuarioActual);

        System.out.print("\n> Introduce el ID del Proyecto: ");
        long proyectoId;
        try {
            proyectoId = Long.parseLong(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ERROR: El ID debe ser un numero.");
            return;
        }

        System.out.print("> Introduce el titulo de la tarea: ");
        String titulo = scanner.nextLine();

        System.out.print("> Introduce una descripción: ");
        String descripcion = scanner.nextLine();

        System.out.print("> Fecha limite (YYYY-MM-DD): ");
        String fechaInput = scanner.nextLine();
        java.time.LocalDate fechaLimite;
        try {
            fechaLimite = java.time.LocalDate.parse(fechaInput);
        } catch (Exception e) {
            System.out.println("ERROR: Formato de fecha incorrecto. Cancelando creación.");
            return;
        }

        Tarea nuevaTarea = new Tarea(proyectoId, titulo, descripcion, fechaLimite);

        String resultado = tareaService.crearNuevaTarea(nuevaTarea, usuarioActual);
        System.out.println("\n" + resultado);
    }

    /**
     * Permite a un administrador asignar una tarea específica a un usuario del sistema.
     */
    private void menuAsignarTarea(Usuario usuarioActual) {
        System.out.println("\n--- ASIGNAR TAREA A USUARIO ---");
        mostrarTodosLosProyectos();
        System.out.print("\n> Introduce el ID del Proyecto: ");
        long proyectoId = Long.parseLong(scanner.nextLine());

        List<Tarea> tareas = tareaDAO.listarPorProyecto(proyectoId);
        if (tareas.isEmpty()) {
            System.out.println("No hay tareas en este proyecto.");
            return;
        }
        for (Tarea t : tareas) {
            System.out.println("ID: " + t.getId() + " | " + t.getTitulo());
        }

        System.out.print("\n> ID de la TAREA a asignar: ");
        long tareaId = Long.parseLong(scanner.nextLine());

        mostrarUsuariosSistema();

        System.out.print("\n> ID del USUARIO encargado: ");
        long userAsignadoId = Long.parseLong(scanner.nextLine());

        String resultado = tareaService.asignarUsuarioATarea(tareaId, userAsignadoId, usuarioActual);
        System.out.println("\n" + resultado);
    }

    /**
     * Gestión de cambio de estado de tareas con validación de asignación.
     */
    private void menuCambiarEstadoTarea(Usuario usuarioActual) {
        System.out.println("\n--- CAMBIAR ESTADO DE TAREA ---");
        mostrarTareas(usuarioActual);

        System.out.print("\n> Introduce el ID de la tarea a actualizar: ");
        long tareaId;
        try {
            tareaId = Long.parseLong(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ERROR: El ID debe ser un formato numérico valido.");
            return;
        }

        System.out.println("Estados disponibles: " + java.util.Arrays.toString(EstadoTarea.values()));
        System.out.print("> Escribe el nuevo estado exacto: ");
        String estadoInput = scanner.nextLine().toUpperCase().trim();

        EstadoTarea nuevoEstado;
        try {
            nuevoEstado = EstadoTarea.valueOf(estadoInput);
        } catch (IllegalArgumentException e) {
            System.out.println("ERROR: El estado '" + estadoInput + "' no es valido. Operación abortada.");
            return;
        }

        String resultado = tareaService.cambiarEstadoTarea(tareaId, nuevoEstado, usuarioActual);
        System.out.println("\n" + resultado);
    }

    private void menuCambiarEstadoProyecto(Usuario usuarioActual) {
        System.out.println("\n--- CAMBIAR ESTADO DE PROYECTO ---");
        mostrarTodosLosProyectos();

        System.out.print("\n> Introduce el ID del proyecto a actualizar: ");
        long proyectoId;
        try {
            proyectoId = Long.parseLong(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ERROR: El ID debe ser un formato numérico válido.");
            return;
        }

        System.out.println("Estados disponibles: " + java.util.Arrays.toString(EstadoProyecto.values()));
        System.out.print("> Escribe el nuevo estado exacto: ");
        String estadoInput = scanner.nextLine().toUpperCase().trim();

        EstadoProyecto nuevoEstado;
        try {
            nuevoEstado = EstadoProyecto.valueOf(estadoInput);
        } catch (IllegalArgumentException e) {
            System.out.println("ERROR: El estado '" + estadoInput + "' no es válido. Operación abortada.");
            return;
        }

        String resultado = proyectoService.cambiarEstadoProyecto(proyectoId, nuevoEstado, usuarioActual);
        System.out.println("\n" + resultado);
    }

    /**
     * Módulo de auditoría para visualizar el histórico de cambios de una tarea.
     */
    private void menuVerHistorial(Usuario usuarioActual) {
        System.out.println("\n--- MÓDULO DE AUDITORÍA ---");
        mostrarTodosLosProyectos();
        System.out.print("\n> Introduce el ID del Proyecto para ver sus tareas: ");
        long proyectoId;
        try {
            proyectoId = Long.parseLong(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ERROR: ID no válido.");
            return;
        }

        System.out.println("\n--- TAREAS DEL PROYECTO " + proyectoId + " ---");
        List<Tarea> tareas = tareaDAO.listarPorProyecto(proyectoId);

        if (tareas.isEmpty()) {
            System.out.println("Este proyecto no tiene tareas registradas.");
            return;
        }

        for (Tarea t : tareas) {
            System.out.println("ID: " + t.getId() + " | Título: " + t.getTitulo() + " | Estado: " + t.getEstado());
        }

        System.out.print("\n> Introduce el ID de la Tarea para ver su historial: ");
        long tareaId;
        try {
            tareaId = Long.parseLong(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ERROR: ID no válido.");
            return;
        }

        List<String> logs = tareaService.consultarHistorialTarea(tareaId, usuarioActual);

        if (logs == null) {
            System.out.println("ACCESO DENEGADO: No tienes permisos de auditor.");
        } else if (logs.isEmpty()) {
            System.out.println("No hay registros de cambios para la tarea #" + tareaId);
        } else {
            System.out.println("\n=== REGISTROS DE ACTIVIDAD (Tarea #" + tareaId + ") ===");
            for (String log : logs) {
                System.out.println(log);
            }
            System.out.println("====================================================");
        }
    }

    /**
     * Imprime en consola todos los proyectos registrados.
     */
    private void mostrarTodosLosProyectos() {
        System.out.println("\n--- VISIÓN GLOBAL DE PROYECTOS ---");
        List<Proyecto> proyectos = proyectoDAO.listarTodos();

        if (proyectos.isEmpty()) {
            System.out.println("No hay proyectos registrados en el sistema.");
        } else {
            for (Proyecto p : proyectos) {
                System.out.println("ID: " + p.getId() +
                        " | " + String.format("%-20s", p.getNombre()) +
                        " | Estado: " + p.getEstado() +
                        " | Creador ID: " + p.getCreadorId());
            }
        }
    }

    /**
     * Imprime todas las tareas del sistema con formato de tabla simple.
     */
    private void mostrarTodasLasTareas() {
        System.out.println("\n--- VISIÓN GLOBAL DE TAREAS ---");
        List<Tarea> tareas = tareaDAO.listarTodas();

        if (tareas.isEmpty()) {
            System.out.println("No hay tareas registradas en el sistema.");
        } else {
            for (Tarea t : tareas) {
                System.out.println("ID: " + t.getId() +
                        " | " + String.format("%-25s", t.getTitulo()) +
                        " | Estado: " + String.format("%-12s", t.getEstado()) +
                        " | Prioridad: " + String.format("%-10s", t.getPrioridad()) +
                        " | Proyecto ID: " + t.getProyectoId());
            }
        }
    }

    /**
     * Lista los usuarios del sistema para facilitar la selección por ID.
     */
    private void mostrarUsuariosSistema() {
        System.out.println("\n--- EQUIPO DISPONIBLE ---");
        List<Usuario> usuarios = usuarioDAO.listarTodos();

        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios registrados.");
        } else {
            System.out.printf("%-5s | %-15s | %-20s%n", "ID", "USUARIO", "EMAIL");
            System.out.println("----------------------------------------------");
            for (Usuario u : usuarios) {
                System.out.printf("%-5d | %-15s | %-20s%n",
                        u.getId(), u.getUsername(), u.getEmail());
            }
        }
    }
}