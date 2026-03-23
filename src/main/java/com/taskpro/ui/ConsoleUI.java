package com.taskpro.ui;

import com.taskpro.dao.*;
import com.taskpro.exception.*;
import com.taskpro.model.*;
import com.taskpro.model.enums.*;
import com.taskpro.service.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
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
            String email = leerInput("Introduce tu email");
            String password = leerInput("Introduce tu contraseña");

            Usuario usuarioActual = authService.login(email, password);

            System.out.println("\n¡Bienvenido/a, " + usuarioActual.getUsername() + "!");

            if (usuarioActual.esAdmin()) {
                menuGestor(usuarioActual);
            } else {
                menuUsuarioEstandar(usuarioActual);
            }

        } catch (OperationCancelledException e) {
            System.out.println("\n[INFO]: Volviendo al menú principal...");
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
            if (proyectos.isEmpty()) System.out.println("Aun no has creado ningún proyecto.");
            else proyectos.forEach(p -> System.out.println("ID: " + p.getId() + " | "
                    + p.getNombre() + " | Estado: " + p.getEstado()));
        } catch (DatabaseException e) {
            System.out.println("\n[ERROR]: " + e.getMessage());
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
            if (tareas.isEmpty()) System.out.println("Sin tareas asignadas.");
            else tareas.forEach(t -> System.out.println("ID: " + t.getId() + " | "
                    + t.getTitulo() + " | Estado: " + t.getEstado()));
        } catch (DatabaseException e) {
            System.out.println("\n[ERROR]: " + e.getMessage());
        }
    }

    /**
     * Recupera y presenta una visión global de todos los proyectos en el sistema.
     */
    private void mostrarTodosLosProyectos() {
        try {
            System.out.println("\n--- VISIÓN GLOBAL DE PROYECTOS ---");
            List<Proyecto> proyectos = proyectoDAO.listarTodos();
            if (proyectos.isEmpty()) System.out.println("No hay proyectos.");
            else proyectos.forEach(p -> System.out.printf("ID: %d | %-20s | Estado: %s%n",
                    p.getId(), p.getNombre(), p.getEstado()));
        } catch (DatabaseException e) {
            System.out.println("\n[ERROR DE SISTEMA]: " + e.getMessage());
        }
    }

    /**
     * Imprime todas las tareas del sistema con formato de tabla simple.
     */
    private void mostrarTodasLasTareas() {
        System.out.println("\n--- VISIÓN GLOBAL DE TAREAS ---");
        List<Tarea> tareas = tareaDAO.listarTodas();
        if (tareas.isEmpty()) System.out.println("No hay tareas.");
        else tareas.forEach(t -> System.out.printf("ID: %d | %-25s | Estado: %s%n",
                t.getId(), t.getTitulo(), t.getEstado()));
    }

    /**
     * Lista los usuarios del sistema para facilitar la selección por ID.
     */
    private void mostrarUsuariosSistema() {
        System.out.println("\n--- EQUIPO DISPONIBLE ---");
        List<Usuario> usuarios = usuarioDAO.listarTodos();
        System.out.printf("%-5s | %-15s | %-20s%n", "ID", "USUARIO", "EMAIL");
        System.out.println("----------------------------------------------");
        usuarios.forEach(u -> System.out.printf("%-5d | %-15s | %-20s%n",
                u.getId(), u.getUsername(), u.getEmail()));
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
            String nombre = leerInput("Nombre del proyecto");
            String descripcion = leerInput("Descripción");

            Proyecto nuevoProyecto = new Proyecto(nombre, descripcion, usuarioActual.getId());
            String mensaje = proyectoService.crearNuevoProyecto(nuevoProyecto, usuarioActual);
            System.out.println("\n" + mensaje);

        } catch (OperationCancelledException e) {
            System.out.println("\n[CANCELADO]: " + e.getMessage());
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

            long proyectoId = Long.parseLong(leerInput("\n> Introduce el ID del Proyecto"));
            String titulo = leerInput("> Título de la tarea");
            String descripcion = leerInput("> Descripción");
            LocalDate fechaLimite = LocalDate.parse(leerInput("> Fecha límite (YYYY-MM-DD)"));

            Tarea nuevaTarea = new Tarea(proyectoId, titulo, descripcion, fechaLimite);
            String mensaje = tareaService.crearNuevaTarea(nuevaTarea, usuarioActual);
            System.out.println("\n" + mensaje);

        } catch (OperationCancelledException e) {
            System.out.println("\n[CANCELADO]: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("\n[ERROR]: El ID debe ser un número.");
        } catch (DateTimeParseException e) {
            System.out.println("\n[ERROR]: Formato de fecha inválido (AAAA-MM-DD).");
        } catch (ValidationException | AccessDeniedException e) {
            System.out.println("\n[AVISO]: " + e.getMessage());
        } catch (DatabaseException e) {
            System.out.println("\n[SISTEMA]: " + e.getMessage());
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

            long proyectoId = Long.parseLong(leerInput("\n> ID del Proyecto"));

            List<Tarea> tareas = tareaDAO.listarPorProyecto(proyectoId);
            if (tareas.isEmpty()) {
                System.out.println("No hay tareas en este proyecto.");
                return; // Volvemos al menú
            }

            tareas.forEach(t -> System.out.println("ID: " + t.getId() + " | " + t.getTitulo()));

            long tareaId = Long.parseLong(leerInput("\n> ID de la TAREA a asignar"));
            mostrarUsuariosSistema();
            long userAsignadoId = Long.parseLong(leerInput("\n> ID del USUARIO encargado"));

            String resultado = tareaService.asignarUsuarioATarea(tareaId, userAsignadoId, usuarioActual);
            System.out.println("\n" + resultado);

        } catch (OperationCancelledException e) {
            System.out.println("\n[CANCELADO]: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("\n[ERROR]: Los IDs deben ser numéricos.");
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

            long tareaId = Long.parseLong(leerInput("\n> ID de la tarea a actualizar"));

            System.out.println("Estados: " + Arrays.toString(EstadoTarea.values()));
            String estadoInput = leerInput("> Nuevo estado").toUpperCase();
            EstadoTarea nuevoEstado = EstadoTarea.valueOf(estadoInput);

            String resultado = tareaService.cambiarEstadoTarea(tareaId, nuevoEstado, usuarioActual);
            System.out.println("\n" + resultado);

        } catch (OperationCancelledException e) {
            System.out.println("\n[CANCELADO]: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("\n[ERROR]: El ID debe ser un número.");
        } catch (IllegalArgumentException e) {
            System.out.println("\n[ERROR]: Estado no válido.");
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

            long proyectoId = Long.parseLong(leerInput("\n> ID del proyecto"));

            System.out.println("Estados: " + Arrays.toString(EstadoProyecto.values()));
            String estadoInput = leerInput("> Nuevo estado").toUpperCase();
            EstadoProyecto nuevoEstado = EstadoProyecto.valueOf(estadoInput);

            String resultado = proyectoService.cambiarEstadoProyecto(proyectoId, nuevoEstado, usuarioActual);
            System.out.println("\n" + resultado);

        } catch (OperationCancelledException e) {
            System.out.println("\n[CANCELADO]: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("\n[ERROR]: El ID debe ser numérico.");
        } catch (IllegalArgumentException e) {
            System.out.println("\n[ERROR]: Estado no válido.");
        } catch (ResourceNotFoundException | AccessDeniedException e) {
            System.out.println("\n[AVISO]: " + e.getMessage());
        } catch (DatabaseException e) {
            System.out.println("\n[SISTEMA]: " + e.getMessage());
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

            long proyectoId = Long.parseLong(leerInput("\n> ID del Proyecto"));

            List<Tarea> tareas = tareaDAO.listarPorProyecto(proyectoId);
            if (tareas.isEmpty()) {
                System.out.println("Este proyecto no tiene tareas.");
                return;
            }

            tareas.forEach(t -> System.out.println("ID: " + t.getId() + " | " + t.getTitulo()));

            long tareaId = Long.parseLong(leerInput("\n> ID de la Tarea para ver historial"));

            List<String> logs = tareaService.consultarHistorialTarea(tareaId, usuarioActual);

            if (logs.isEmpty()) {
                System.out.println("Sin registros de cambios para #" + tareaId);
            } else {
                System.out.println("\n=== REGISTROS (Tarea #" + tareaId + ") ===");
                logs.forEach(System.out::println);
                System.out.println("==========================================");
            }

        } catch (OperationCancelledException e) {
            System.out.println("\n[CANCELADO]: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("\n[ERROR]: El ID debe ser numérico.");
        } catch (AccessDeniedException e) {
            System.out.println("\n[SEGURIDAD]: " + e.getMessage());
        } catch (DatabaseException e) {
            System.out.println("\n[SISTEMA]: " + e.getMessage());
        }
    }

    /**
     * Solicita una entrada al usuario.
     * Si escribe '0', cancela la operación.
     * Si deja el campo vacío, insiste hasta obtener un valor.
     */
    private String leerInput(String mensaje) {
        String entrada;
        while (true) {
            System.out.print(mensaje + " (o '0' para atrás): ");
            entrada = scanner.nextLine().trim();

            if ("0".equals(entrada)) throw new OperationCancelledException();
            if (!entrada.isEmpty()) return entrada;

            System.out.println("El campo no puede estar vacío.");
        }
    }
}