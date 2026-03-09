package com.taskpro.ui;

import com.taskpro.dao.ProyectoDAO;
import com.taskpro.dao.TareaDAO;
import com.taskpro.dao.UsuarioDAO;
import com.taskpro.model.Proyecto;
import com.taskpro.model.Tarea;
import com.taskpro.model.Usuario;
import com.taskpro.model.enums.EstadoProyecto;
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

    public ConsoleUI() {
        this.scanner = new Scanner(System.in);
        this.usuarioDAO = new UsuarioDAO();
        this.proyectoDAO = new ProyectoDAO();
        this.tareaDAO = new TareaDAO();
        this.proyectoService = new ProyectoService();
        this.tareaService = new TareaService();
    }

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

    private void login() {
        System.out.println("\n--- INICIO DE SESIÓN ---");
        System.out.print("Introduce tu email: ");
        String email = scanner.nextLine();

        Usuario usuarioActual = usuarioDAO.buscarPorEmail(email);

        if (usuarioActual != null) {
            System.out.println("\nBienvenido/a, " + usuarioActual.getUsername() + "!");

            // LÓGICA DE DERIVACIÓN PROFESIONAL
            if (usuarioActual.tienePermisoGestion()) {
                menuGestor(usuarioActual);
            } else {
                menuUsuarioEstandar(usuarioActual);
            }

        } else {
            System.out.println("\nError: No existe ningún usuario con ese email.");
        }
    }

    // --- MENÚ PARA ADMIN/GESTOR ---
    private void menuGestor(Usuario usuarioActual) {
        boolean cerrarSesion = false;
        while (!cerrarSesion) {
            System.out.println("\n==================================");
            System.out.println(" PANEL DE GESTIÓN: " + usuarioActual.getUsername().toUpperCase());
            System.out.println("==================================");
            System.out.println("1. Ver todos los Proyectos");
            System.out.println("2. Crear Nuevo Proyecto");
            System.out.println("3. Crear Nueva Tarea");
            System.out.println("4. Asignar Tarea a Usuario");
            System.out.println("5. Ver Historial de Auditoría");
            System.out.println("6. Cambiar Estado de una Tarea");
            System.out.println("0. Cerrar Sesión");
            System.out.print("> Elige una opción: ");

            String opcion = scanner.nextLine();
            switch (opcion) {
                case "1": mostrarTodosLosProyectos(); break;
                case "2": menuCrearProyecto(usuarioActual); break;
                case "3": menuCrearTarea(usuarioActual); break;
                case "4": menuAsignarTarea(usuarioActual); break;
                case "5": menuVerHistorial(usuarioActual); break;
                case "6": menuCambiarEstadoTarea(usuarioActual); break;
                case "0": cerrarSesion = true; break;
                default: System.out.println("Opción no válida.");
            }
        }
    }

    // --- MENÚ USUARIO OPERATIVO ---
    private void menuUsuarioEstandar(Usuario usuarioActual) {
        boolean cerrarSesion = false;
        while (!cerrarSesion) {
            System.out.println("\n==================================");
            System.out.println(" MIS TAREAS: " + usuarioActual.getUsername().toUpperCase());
            System.out.println("==================================");
            System.out.println("1. Ver mis Tareas Asignadas");
            System.out.println("2. Actualizar Estado de Tarea");
            System.out.println("3. Ver mis Proyectos");
            System.out.println("0. Cerrar Sesión");
            System.out.print("> Elige una opción: ");

            String opcion = scanner.nextLine();
            switch (opcion) {
                case "1": mostrarTareas(usuarioActual); break;
                case "2": menuCambiarEstadoTarea(usuarioActual); break;
                case "3": mostrarProyectos(usuarioActual); break;
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

    private void menuCrearProyecto(Usuario usuarioActual) {
        System.out.println("\n--- CREAR NUEVO PROYECTO ---");

        System.out.print("Introduce el nombre del proyecto: ");
        String nombre = scanner.nextLine();

        System.out.print("Introduce una descripción: ");
        String descripcion = scanner.nextLine();

        Proyecto nuevoProyecto = new Proyecto(0, nombre, descripcion, EstadoProyecto.ACTIVO, usuarioActual.getId());

        String resultado = proyectoService.crearNuevoProyecto(nuevoProyecto, usuarioActual);

        System.out.println("\n" + resultado);
    }

    private void menuCrearTarea(Usuario usuarioActual) {
        System.out.println("\n--- CREAR NUEVA TAREA ---");
        // 1. Mostramos los proyectos disponibles
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

        // 2. Pedimos los datos de la tarea
        System.out.print("> Introduce el titulo de la tarea: ");
        String titulo = scanner.nextLine();

        System.out.print("> Introduce una descripción: ");
        String descripcion = scanner.nextLine();

        System.out.print("> Prioridad (ALTA, MEDIA, BAJA): ");
        String prioridadInput = scanner.nextLine().toUpperCase().trim();
        // Por seguridad, si escribe mal, le ponemos MEDIA por defecto
        com.taskpro.model.enums.Prioridad prioridad = com.taskpro.model.enums.Prioridad.MEDIA;
        try {
            prioridad = com.taskpro.model.enums.Prioridad.valueOf(prioridadInput);
        } catch (IllegalArgumentException e) {
            System.out.println("Prioridad no reconocida. Se asignara MEDIA por defecto.");
        }

        System.out.print("> Fecha limite (YYYY-MM-DD): ");
        String fechaInput = scanner.nextLine();
        java.time.LocalDate fechaLimite;
        try {
            fechaLimite = java.time.LocalDate.parse(fechaInput);
        } catch (Exception e) {
            System.out.println("ERROR: Formato de fecha incorrecto. Cancelando creación.");
            return;
        }

        // 3. Creamos el objeto (Estado por defecto TODO)
        Tarea nuevaTarea = new Tarea(proyectoId, titulo, descripcion, prioridad,
                com.taskpro.model.enums.EstadoTarea.TODO, fechaLimite);

        // 4. Se lo pasamos al Servicio
        String resultado = tareaService.crearNuevaTarea(nuevaTarea, usuarioActual);

        System.out.println("\n" + resultado);
    }

    private void menuAsignarTarea(Usuario usuarioActual) {
        System.out.println("\n--- ASIGNAR TAREA A USUARIO ---");

        // 1. Mostrar proyectos para contextualizar
        mostrarTodosLosProyectos();
        System.out.print("\n> Introduce el ID del Proyecto: ");
        long proyectoId = Long.parseLong(scanner.nextLine());

        // 2. Mostrar tareas de ese proyecto
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

        // 4. Ejecutar asignación
        String resultado = tareaService.asignarUsuarioATarea(tareaId, userAsignadoId, usuarioActual);
        System.out.println("\n" + resultado);
    }

    private void menuCambiarEstadoTarea(Usuario usuarioActual) {
        System.out.println("\n--- CAMBIAR ESTADO DE TAREA ---");
        // Mostrar tareas actuales para facilitar la selección
        mostrarTareas(usuarioActual);

        System.out.print("\n> Introduce el ID de la tarea a actualizar: ");
        long tareaId;
        try {
            tareaId = Long.parseLong(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ERROR: El ID debe ser un formato numérico valido.");
            return;
        }

        System.out.println("Estados disponibles: TODO, IN_PROGRESS, DONE");
        System.out.print("> Escribe el nuevo estado exacto: ");
        String estadoInput = scanner.nextLine().toUpperCase().trim();

        com.taskpro.model.enums.EstadoTarea nuevoEstado;
        try {
            nuevoEstado = com.taskpro.model.enums.EstadoTarea.valueOf(estadoInput);
        } catch (IllegalArgumentException e) {
            System.out.println("ERROR: El estado '" + estadoInput + "' no es valido. Operación abortada.");
            return;
        }

        // Delegamos toda la responsabilidad de validación al Service
        String resultado = tareaService.cambiarEstadoTarea(tareaId, nuevoEstado, usuarioActual);
        System.out.println("\n" + resultado);
    }

    private void menuVerHistorial(Usuario usuarioActual) {
        System.out.println("\n--- MÓDULO DE AUDITORÍA ---");
        // 1. Mostrar todos los proyectos para que elija uno
        mostrarTodosLosProyectos();
        System.out.print("\n> Introduce el ID del Proyecto para ver sus tareas: ");
        long proyectoId;
        try {
            proyectoId = Long.parseLong(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ERROR: ID no válido.");
            return;
        }

        // 2. Mostrar todas las tareas de ese proyecto
        System.out.println("\n--- TAREAS DEL PROYECTO " + proyectoId + " ---");
        List<Tarea> tareas = tareaDAO.listarPorProyecto(proyectoId);

        if (tareas.isEmpty()) {
            System.out.println("Este proyecto no tiene tareas registradas.");
            return;
        }

        for (Tarea t : tareas) {
            System.out.println("ID: " + t.getId() + " | Título: " + t.getTitulo() + " | Estado: " + t.getEstado());
        }

        // 3. Ahora que ve las tareas, pedimos el ID para el historial
        System.out.print("\n> Introduce el ID de la Tarea para ver su historial: ");
        long tareaId;
        try {
            tareaId = Long.parseLong(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ERROR: ID no válido.");
            return;
        }

        // 4. Llamamos al servicio para obtener los logs
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

    private void mostrarUsuariosSistema() {
        System.out.println("\n--- EQUIPO DISPONIBLE ---");
        List<Usuario> usuarios = usuarioDAO.listarTodos();

        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios registrados.");
        } else {
            // Formateamos la salida para que parezca una tabla
            System.out.printf("%-5s | %-15s | %-20s%n", "ID", "USUARIO", "EMAIL");
            System.out.println("----------------------------------------------");
            for (Usuario u : usuarios) {
                System.out.printf("%-5d | %-15s | %-20s%n",
                        u.getId(), u.getUsername(), u.getEmail());
            }
        }
    }
}