package com.taskpro;

import com.taskpro.dao.TareaDAO;
import com.taskpro.model.Tarea;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        TareaDAO tareaDAO = new TareaDAO();
        List<Tarea> tareas = tareaDAO.listarTodas();
        tareas.forEach(System.out::println);
    }
}