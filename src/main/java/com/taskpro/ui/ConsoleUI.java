package com.taskpro.ui;

import com.taskpro.dao.*;
import com.taskpro.exception.*;
import com.taskpro.model.*;
import com.taskpro.model.enums.*;
import com.taskpro.service.*;

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
     * Lanza el bucle principal de la aplicación.
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
        try {
            System.out.println("\n--- INICIO DE SESIÓN ---");
            System.out.print("Introduce tu email: ");
            String email = scanner.nextLine();
            System.out.print("Introduce tu contraseña: ");
            String password = scanner.nextLine();

            Usuario usuarioActual = authService.login(email, password);

            System.out.println("\n¡Bienvenido/a, " + usuarioActual.getUsername() + "!");

            if (usuarioActual.esAdmin()) {
                menuGestor(usuarioActual);
            } else {
                menuUsuarioEstandar(usuarioActual);
            }

        } catch (AuthenticationException e) {
            System.out.println("\n[LOGIN FALLIDO]: " + e.getMessage());
        } catch (DatabaseException e) {
            System.out.println("\n[ERROR DE SISTEMA]: " + e.getMessage());
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

    /**
     * Recupera y muestra por consola los proyectos creados por el usuario actual.
     * Maneja posibles errores técnicos de la base de datos.
     *
     * @param usuarioActual El usuario que realiza la consulta.
     */
    private void mostrarProyectos(Usuario usuarioActual) {
        try {
            System.out.println("\n--- TUS PROYECTOS ---");
            List<Proyecto> proyectos = proyectoDAO.listarPorUsuario(usuarioActual.getId());

            if (proyectos.isEmpty()) {
                System.out.println("Aun no has creado ningún proyecto.");
            } else {
                for (Proyecto p : proyectos) {
                    System.out.println("ID: " + p.getId() + " | " + p.getNombre() + " | Estado: " + p.getEstado());
                }
            }
        } catch (DatabaseException e) {
            System.out.println("\n[ERROR DE SISTEMA]: No pudimos cargar tus proyectos. " + e.getMessage());
        }
    }

    /**
     * Recupera y muestra por consola las tareas asignadas al usuario actual.
     * Valida la conexión con la persistencia durante el listado.
     *
     * @param usuarioActual El usuario que desea ver sus responsabilidades.
     */
    private void mostrarTareas(Usuario usuarioActual) {
        try {
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
        } catch (DatabaseException e) {
            System.out.println("\n[ERROR DE SISTEMA]: Error al conectar con la tabla de tareas. " + e.getMessage());
        }
    }

    /**
     * Formulario interactivo para registrar un nuevo proyecto.
     * Captura excepciones de validación, permisos y base de datos.
     *
     * @param usuarioActual Usuario que intenta realizar la operación (debe ser ADMINISTRADOR)
     */
    private void menuCrearProyecto(Usuario usuarioActual) {
        try {
            System.out.println("\n--- CREAR NUEVO PROYECTO ---");
            System.out.print("Nombre del proyecto: ");
            String nombre = scanner.nextLine();
            System.out.print("Descripción: ");
            String descripcion = scanner.nextLine();

            Proyecto nuevoProyecto = new Proyecto(nombre, descripcion, usuarioActual.getId());

            String mensajeExito = proyectoService.crearNuevoProyecto(nuevoProyecto, usuarioActual);
            System.out.println("\n" + mensajeExito);

        } catch (ValidationException | AccessDeniedException e) {
            System.out.println("\n[AVISO]: " + e.getMessage());
        } catch (DatabaseException e) {
            System.out.println("\n[ERROR CRÍTICO]: " + e.getMessage());
        }
    }

    /**
     * Guía al usuario para crear una tarea vinculada a un proyecto.
     * Gestiona errores de formato de fecha, IDs no numéricos y reglas de negocio.
     *
     * @param usuarioActual Usuario autenticado que solicita la creación de la tarea.
     */
    private void menuCrearTarea(Usuario usuarioActual) {
        try {
            System.out.println("\n--- CREAR NUEVA TAREA ---");
            mostrarTodosLosProyectos();

            System.out.print("\n> Introduce el ID del Proyecto: ");
            long proyectoId = Long.parseLong(scanner.nextLine());

            System.out.print("> Título de la tarea: ");
            String titulo = scanner.nextLine();
            System.out.print("> Descripción: ");
            String descripcion = scanner.nextLine();

            System.out.print("> Fecha límite (YYYY-MM-DD): ");
            java.time.LocalDate fechaLimite = java.time.LocalDate.parse(scanner.nextLine());

            Tarea nuevaTarea = new Tarea(proyectoId, titulo, descripcion, fechaLimite);

            String mensajeExito = tareaService.crearNuevaTarea(nuevaTarea, usuarioActual);
            System.out.println("\n" + mensajeExito);

        } catch (NumberFormatException e) {
            System.out.println("\n[ERROR]: El ID del proyecto debe ser un número.");
        } catch (java.time.format.DateTimeParseException e) {
            System.out.println("\n[ERROR]: Formato de fecha inválido. Usa AAAA-MM-DD.");
        } catch (ValidationException | AccessDeniedException e) {
            System.out.println("\n[AVISO]: " + e.getMessage());
        } catch (DatabaseException e) {
            System.out.println("\n[ERROR CRÍTICO]: " + e.getMessage());
        }
    }

    /**
     * Interfaz para vincular una tarea con un usuario responsable.
     * Valida la existencia de los recursos y los permisos del gestor.
     *
     * @param usuarioActual Usuario administrador con permisos para realizar asignaciones.
     */
    private void menuAsignarTarea(Usuario usuarioActual) {
        try {
            System.out.println("\n--- ASIGNAR TAREA A USUARIO ---");
            mostrarTodosLosProyectos();

            System.out.print("\n> ID del Proyecto: ");
            long proyectoId = Long.parseLong(scanner.nextLine());

            List<Tarea> tareas = tareaDAO.listarPorProyecto(proyectoId);
            if (tareas.isEmpty()) {
                System.out.println("No hay tareas en este proyecto.");
                return;
            }

            tareas.forEach(t -> System.out.println("ID: " + t.getId() + " | " + t.getTitulo()));

            System.out.print("\n> ID de la TAREA a asignar: ");
            long tareaId = Long.parseLong(scanner.nextLine());

            mostrarUsuariosSistema();

            System.out.print("\n> ID del USUARIO encargado: ");
            long userAsignadoId = Long.parseLong(scanner.nextLine());

            String mensajeExito = tareaService.asignarUsuarioATarea(tareaId, userAsignadoId, usuarioActual);
            System.out.println("\n" + mensajeExito);

        } catch (NumberFormatException e) {
            System.out.println("\n[ERROR]: Los IDs deben ser valores numéricos.");
        } catch (AccessDeniedException e) {
            System.out.println("\n[SEGURIDAD]: " + e.getMessage());
        } catch (DatabaseException e) {
            System.out.println("\n[SISTEMA]: " + e.getMessage());
        }
    }

    /**
     * Interfaz de consola para que un usuario actualice el progreso de sus tareas.
     * Lista las tareas del usuario y permite cambiar su estado.
     *
     * @param usuarioActual Usuario que intenta realizar la modificación.
     */
    private void menuCambiarEstadoTarea(Usuario usuarioActual) {
        try {
            System.out.println("\n--- CAMBIAR ESTADO DE TAREA ---");
            mostrarTareas(usuarioActual);

            System.out.print("\n> Introduce el ID de la tarea a actualizar: ");
            long tareaId = Long.parseLong(scanner.nextLine());

            System.out.println("Estados disponibles: " + java.util.Arrays.toString(EstadoTarea.values()));
            System.out.print("> Escribe el nuevo estado exacto: ");
            String estadoInput = scanner.nextLine().toUpperCase().trim();

            EstadoTarea nuevoEstado = EstadoTarea.valueOf(estadoInput);

            String resultado = tareaService.cambiarEstadoTarea(tareaId, nuevoEstado, usuarioActual);
            System.out.println("\n" + resultado);

        } catch (NumberFormatException e) {
            System.out.println("\n[ERROR]: El ID de la tarea debe ser un número.");
        } catch (IllegalArgumentException e) {
            System.out.println("\n[ERROR]: El estado introducido no es válido.");
        } catch (ResourceNotFoundException | AccessDeniedException e) {
            System.out.println("\n[AVISO]: " + e.getMessage());
        } catch (DatabaseException e) {
            System.out.println("\n[SISTEMA]: " + e.getMessage());
        }
    }

    /**
     * Formulario para que un administrador modifique el estado global de un proyecto.
     * Permite seleccionar un proyecto del listado general y asignarle un nuevo estado.
     *
     * @param usuarioActual Usuario (Administrador) que solicita el cambio.
     */
    private void menuCambiarEstadoProyecto(Usuario usuarioActual) {
        try {
            System.out.println("\n--- CAMBIAR ESTADO DE PROYECTO ---");
            mostrarTodosLosProyectos();

            System.out.print("\n> Introduce el ID del proyecto a actualizar: ");
            long proyectoId = Long.parseLong(scanner.nextLine());

            System.out.println("Estados disponibles: " + java.util.Arrays.toString(EstadoProyecto.values()));
            System.out.print("> Escribe el nuevo estado exacto: ");
            String estadoInput = scanner.nextLine().toUpperCase().trim();

            EstadoProyecto nuevoEstado = EstadoProyecto.valueOf(estadoInput);

            String resultado = proyectoService.cambiarEstadoProyecto(proyectoId, nuevoEstado, usuarioActual);
            System.out.println("\n" + resultado);

        } catch (NumberFormatException e) {
            System.out.println("\n[ERROR]: El ID del proyecto debe ser numérico.");
        } catch (IllegalArgumentException e) {
            System.out.println("\n[ERROR]: Ese estado de proyecto no existe.");
        } catch (ResourceNotFoundException | AccessDeniedException e) {
            System.out.println("\n[AVISO]: " + e.getMessage());
        } catch (DatabaseException e) {
            System.out.println("\n[SISTEMA]: Error técnico: " + e.getMessage());
        }
    }

    /**
     * Módulo de auditoría que permite visualizar el rastro de cambios de una tarea.
     *
     * @param usuarioActual Usuario que consulta el historial (requiere permisos de gestor).
     */
    private void menuVerHistorial(Usuario usuarioActual) {
        try {
            System.out.println("\n--- MÓDULO DE AUDITORÍA ---");
            mostrarTodosLosProyectos();

            System.out.print("\n> Introduce el ID del Proyecto para ver sus tareas: ");
            long proyectoId = Long.parseLong(scanner.nextLine());

            System.out.println("\n--- TAREAS DEL PROYECTO " + proyectoId + " ---");
            List<Tarea> tareas = tareaDAO.listarPorProyecto(proyectoId);

            if (tareas.isEmpty()) {
                System.out.println("Este proyecto no tiene tareas registradas.");
                return;
            }

            for (Tarea t : tareas) {
                System.out.println("ID: " + t.getId() + " | Título: " + t.getTitulo() + " | " +
                        "Estado: " + t.getEstado());
            }

            System.out.print("\n> Introduce el ID de la Tarea para ver su historial: ");
            long tareaId = Long.parseLong(scanner.nextLine());

            List<String> logs = tareaService.consultarHistorialTarea(tareaId, usuarioActual);

            if (logs.isEmpty()) {
                System.out.println("No hay registros de cambios para la tarea #" + tareaId);
            } else {
                System.out.println("\n=== REGISTROS DE ACTIVIDAD (Tarea #" + tareaId + ") ===");
                logs.forEach(System.out::println);
                System.out.println("====================================================");
            }

        } catch (NumberFormatException e) {
            System.out.println("\n[ERROR]: Debes introducir un ID numérico válido.");
        } catch (AccessDeniedException e) {
            System.out.println("\n[SEGURIDAD]: " + e.getMessage());
        } catch (DatabaseException e) {
            System.out.println("\n[SISTEMA]: No se pudo cargar el historial: " + e.getMessage());
        }
    }

    /**
     * Recupera y presenta una visión global de todos los proyectos en el sistema.
     */
    private void mostrarTodosLosProyectos() {
        try {
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
        } catch (DatabaseException e) {
            System.out.println("\n[ERROR DE SISTEMA]: No se pueden listar los proyectos: " + e.getMessage());
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