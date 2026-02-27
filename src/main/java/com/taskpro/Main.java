package com.taskpro;

import com.taskpro.dao.ProyectoDAO;
import com.taskpro.dao.TareaDAO;
import com.taskpro.dao.UsuarioDAO;
import com.taskpro.model.Proyecto;
import com.taskpro.model.Tarea;
import com.taskpro.model.Usuario;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        TareaDAO tareaDAO = new TareaDAO();
        List<Tarea> tareas = tareaDAO.listarTodas();
        tareas.forEach(System.out::println);

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        List<Usuario> usuarios = usuarioDAO.listarTodos();
        usuarios.forEach(System.out::println);

        ProyectoDAO proyectoDAO = new ProyectoDAO();
        List<Proyecto> proyectos = proyectoDAO.listarTodos();
        proyectos.forEach(System.out::println);
    }
}